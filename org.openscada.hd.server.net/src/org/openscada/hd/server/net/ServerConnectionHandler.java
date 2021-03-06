/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.hd.server.net;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.mina.core.session.IoSession;
import org.openscada.core.ConnectionInformation;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.net.MessageHelper;
import org.openscada.core.server.Session.SessionListener;
import org.openscada.core.server.net.AbstractServerConnectionHandler;
import org.openscada.hd.InvalidItemException;
import org.openscada.hd.ItemListListener;
import org.openscada.hd.Query;
import org.openscada.hd.QueryState;
import org.openscada.hd.data.HistoricalItemInformation;
import org.openscada.hd.data.QueryParameters;
import org.openscada.hd.data.ValueInformation;
import org.openscada.hd.net.ItemListHelper;
import org.openscada.hd.net.Messages;
import org.openscada.hd.net.QueryHelper;
import org.openscada.hd.server.Service;
import org.openscada.hd.server.Session;
import org.openscada.net.base.MessageListener;
import org.openscada.net.base.data.IntegerValue;
import org.openscada.net.base.data.LongValue;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.net.base.data.VoidValue;
import org.openscada.net.utils.MessageCreator;
import org.openscada.sec.callback.PropertiesCredentialsCallback;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

public class ServerConnectionHandler extends AbstractServerConnectionHandler implements ItemListListener
{

    public final static String VERSION = "0.1.0";

    public final static int MAX_DATA_SIZE = Integer.getInteger ( "org.openscada.hd.server.net.maxDataSize", 1024 );

    private final static Logger logger = LoggerFactory.getLogger ( ServerConnectionHandler.class );

    private Service service = null;

    private Session session = null;

    private final Map<Long, QueryHandler> queries = new HashMap<Long, QueryHandler> ();

    private final ExecutorService queryDisposer;

    public ServerConnectionHandler ( final Service service, final IoSession ioSession, final ConnectionInformation connectionInformation )
    {
        super ( ioSession, connectionInformation );

        this.queryDisposer = Executors.newCachedThreadPool ( new NamedThreadFactory ( "ServerConnectionHandler/QueryDisposer" ) );

        this.service = service;

        this.messenger.setHandler ( MessageHelper.CC_CREATE_SESSION, new MessageListener () {

            @Override
            public void messageReceived ( final Message message )
            {
                createSession ( message );
            }
        } );

        this.messenger.setHandler ( MessageHelper.CC_CLOSE_SESSION, new MessageListener () {

            @Override
            public void messageReceived ( final Message message )
            {
                closeSession ();
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_START_LIST, new MessageListener () {

            @Override
            public void messageReceived ( final Message message )
            {
                ServerConnectionHandler.this.setItemList ( true );
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_STOP_LIST, new MessageListener () {

            @Override
            public void messageReceived ( final Message message )
            {
                ServerConnectionHandler.this.setItemList ( false );
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_CREATE_QUERY, new MessageListener () {

            @Override
            public void messageReceived ( final Message message )
            {
                ServerConnectionHandler.this.handleCreateQuery ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_CLOSE_QUERY, new MessageListener () {

            @Override
            public void messageReceived ( final Message message )
            {
                ServerConnectionHandler.this.handleCloseQuery ( message );
            }
        } );

        this.messenger.setHandler ( Messages.CC_HD_CHANGE_QUERY_PARAMETERS, new MessageListener () {

            @Override
            public void messageReceived ( final Message message )
            {
                ServerConnectionHandler.this.handleUpdateQueryParameters ( message );
            }
        } );

    }

    protected void handleUpdateQueryParameters ( final Message message )
    {
        // get the query id
        final long queryId = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();

        final QueryParameters parameters = QueryHelper.fromValue ( message.getValues ().get ( "parameters" ) );

        logger.debug ( "Request parameter change: {}", parameters );

        synchronized ( this )
        {
            final QueryHandler handler = this.queries.get ( queryId );
            if ( handler != null )
            {
                handler.changeParameters ( parameters );
            }
        }
    }

    protected void handleCloseQuery ( final Message message )
    {
        final Profiler p = new Profiler ( "Close Query" );
        p.setLogger ( logger );

        p.start ( "init" );

        // get the query id
        final long queryId = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();

        logger.info ( "Handle close query: {}", queryId );

        final QueryHandler handler;
        synchronized ( this )
        {
            p.start ( "remove" );

            sendQueryState ( queryId, QueryState.DISCONNECTED );
            handler = this.queries.remove ( queryId );
        }

        // close outside of lock
        if ( handler != null )
        {
            p.start ( "Close" );
            // throw it in the disposer queue ... the storage module takes too long
            this.queryDisposer.execute ( new Runnable () {

                @Override
                public void run ()
                {
                    handler.close ();
                }
            } );
        }

        p.stop ().log ();
    }

    protected void handleCreateQuery ( final Message message )
    {
        final Profiler p = new Profiler ( "Create query" );
        p.setLogger ( logger );

        // get the query id
        final long queryId = ( (LongValue)message.getValues ().get ( "id" ) ).getValue ();

        logger.debug ( "Creating new query with id: {}", queryId );

        try
        {
            p.start ( "Prepare" );

            // get the query item
            final String itemId = ( (StringValue)message.getValues ().get ( "itemId" ) ).getValue ();
            // get the initial query parameters
            final QueryParameters parameters = QueryHelper.fromValue ( message.getValues ().get ( "parameters" ) );
            final boolean updateData = message.getValues ().containsKey ( "updateData" );

            p.start ( "Make query" );
            makeQuery ( message, queryId, itemId, parameters, updateData );

            p.start ( "Finish" );
        }
        catch ( final Throwable e )
        {
            sendQueryState ( queryId, QueryState.DISCONNECTED );
        }
        finally
        {
            p.stop ().log ();
        }
    }

    private void makeQuery ( final Message message, final long queryId, final String itemId, final QueryParameters parameters, final boolean updateData ) throws InvalidSessionException, InvalidItemException
    {

        final QueryHandler handler;
        synchronized ( this )
        {
            if ( this.queries.containsKey ( queryId ) )
            {
                logger.warn ( "Duplicate query request: {}", queryId );
                this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Duplicate query id" ) );
                return;
            }

            // create the handler and set the query
            handler = new QueryHandler ( queryId, this );
            this.queries.put ( queryId, handler );
        }

        final Query query = this.service.createQuery ( this.session, itemId, parameters, handler, updateData );

        if ( query == null )
        {
            synchronized ( this )
            {
                // we already added the query .. so remove it here
                sendQueryState ( queryId, QueryState.DISCONNECTED );
                this.queries.remove ( queryId );
            }
        }
        else
        {
            logger.debug ( "Adding query: {}", queryId );
            handler.setQuery ( query );
        }
    }

    public void sendQueryData ( final long queryId, final int index, final Map<String, List<Double>> values, final List<ValueInformation> valueInformation )
    {
        synchronized ( this )
        {
            if ( !this.queries.containsKey ( queryId ) )
            {
                return;
            }

            final int len = valueInformation.size ();

            if ( len < MAX_DATA_SIZE )
            {
                sendQueryDataPacket ( queryId, index, values, valueInformation );
            }
            else
            {
                logger.debug ( "Using split send: {}", MAX_DATA_SIZE );
                int count = 0;

                do
                {
                    // get remaining items
                    final int size = Math.min ( len - count, MAX_DATA_SIZE );

                    logger.debug ( "Sending - query-id: {}, index: {}, size: {}", new Object[] { queryId, count, size } );

                    // copy vi
                    final List<ValueInformation> vi = valueInformation.subList ( count, count + size );

                    // copy values
                    final Map<String, List<Double>> v = new HashMap<String, List<Double>> ();
                    for ( final Map.Entry<String, List<Double>> entry : values.entrySet () )
                    {
                        final List<Double> vs = entry.getValue ().subList ( count, count + size );
                        v.put ( entry.getKey (), vs );
                    }

                    sendQueryDataPacket ( queryId, count, v, vi );

                    count += MAX_DATA_SIZE;
                } while ( count < len );
            }
        }
    }

    private void sendQueryDataPacket ( final long queryId, final int index, final Map<String, List<Double>> values, final List<ValueInformation> valueInformation )
    {
        logger.debug ( "Sending data - queryId: {}, index: {}, @values: {}, @informations: {}", new Object[] { queryId, index, values.size (), valueInformation.size () } );

        final Message message = new Message ( Messages.CC_HD_UPDATE_QUERY_DATA );
        message.getValues ().put ( "id", new LongValue ( queryId ) );
        message.getValues ().put ( "index", new IntegerValue ( index ) );

        message.getValues ().put ( "values", QueryHelper.toValueData ( values ) );
        message.getValues ().put ( "valueInformation", QueryHelper.toValueInfo ( valueInformation ) );

        this.messenger.sendMessage ( message );
    }

    public void sendQueryParameters ( final long queryId, final QueryParameters parameters, final Set<String> valueTypes )
    {
        logger.debug ( "Sending query parameters: {} / {} / {}", new Object[] { queryId, parameters, valueTypes } );

        synchronized ( this )
        {
            if ( !this.queries.containsKey ( queryId ) )
            {
                return;
            }

            // new message
            final Message message = new Message ( Messages.CC_HD_UPDATE_QUERY_PARAMETERS );

            // set data
            message.getValues ().put ( "id", new LongValue ( queryId ) );
            message.getValues ().put ( "parameters", QueryHelper.toValue ( parameters ) );
            message.getValues ().put ( "valueTypes", QueryHelper.toValueTypes ( valueTypes ) );

            // send message without feedback
            this.messenger.sendMessage ( message );
        }
    }

    public void sendQueryState ( final long queryId, final QueryState state )
    {
        logger.debug ( "Sending query state: {} -> {}", new Object[] { queryId, state } );

        synchronized ( this )
        {
            if ( !this.queries.containsKey ( queryId ) )
            {
                logger.info ( "Query not found {}", queryId );
                return;
            }
            final Message message = new Message ( Messages.CC_HD_UPDATE_QUERY_STATUS );
            message.getValues ().put ( "id", new LongValue ( queryId ) );
            message.getValues ().put ( "state", new StringValue ( state.toString () ) );
            this.messenger.sendMessage ( message );
        }
    }

    protected void setItemList ( final boolean flag )
    {
        if ( flag )
        {
            this.session.setItemListListener ( this );
        }
        else
        {
            this.session.setItemListListener ( null );
        }
    }

    private void createSession ( final Message message )
    {
        // if session exists this is an error
        if ( this.session != null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "Connection already bound to a session" ) );
            return;
        }

        // get the session properties
        final Properties props = new Properties ();
        MessageHelper.getProperties ( props, message.getValues ().get ( "properties" ) );

        // now check client version
        final String clientVersion = props.getProperty ( "client-version", "" );
        if ( clientVersion.equals ( "" ) )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "client does not pass \"client-version\" property! You may need to upgrade your client!" ) );
            return;
        }
        // client version does not match server version
        if ( !clientVersion.equals ( VERSION ) )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "protocol version mismatch: client '" + clientVersion + "' server: '" + VERSION + "'" ) );
            return;
        }

        this.service.createSession ( props, new PropertiesCredentialsCallback ( props ) ).addListener ( new FutureListener<Session> () {

            @Override
            public void complete ( final Future<Session> future )
            {
                handleCreateSessionComplete ( future, message, props );
            }
        } );

    }

    /**
     * @since 1.1
     */
    protected void handleCreateSessionComplete ( final Future<Session> future, final Message message, final Properties props )
    {
        try
        {
            this.session = future.get ();
        }
        catch ( final Exception e )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, e ) );
            return;
        }

        // unknown reason why we did not get a session
        if ( this.session == null )
        {
            this.messenger.sendMessage ( MessageCreator.createFailedMessage ( message, "unable to create session" ) );
            return;
        }

        // send success
        replySessionCreated ( props, message, this.session.getProperties () );

        // hook up privs
        this.session.addSessionListener ( new SessionListener () {

            @Override
            public void privilegeChange ()
            {
                sendPrivilegeChange ( ServerConnectionHandler.this.session.getPrivileges () );
            }
        } );
    }

    @Override
    protected void cleanUp ()
    {
        super.cleanUp ();
        disposeSession ();
    }

    private void disposeSession ()
    {
        this.queryDisposer.shutdown ();

        // if session does not exists, silently ignore it
        if ( this.session != null )
        {
            final Session session = this.session;
            this.session = null;
            try
            {
                this.service.closeSession ( session );
            }
            catch ( final InvalidSessionException e )
            {
                logger.warn ( "Failed to close session", e );
            }
        }
    }

    private void closeSession ()
    {
        cleanUp ();
    }

    @Override
    public void listChanged ( final Set<HistoricalItemInformation> addedOrModified, final Set<String> removed, final boolean full )
    {
        final Message message = new Message ( Messages.CC_HD_LIST_UPDATE );
        if ( addedOrModified != null )
        {
            message.getValues ().put ( ItemListHelper.FIELD_ADDED, ItemListHelper.toValueAdded ( addedOrModified ) );
        }
        if ( removed != null )
        {
            message.getValues ().put ( ItemListHelper.FIELD_REMOVED, ItemListHelper.toValueRemoved ( removed ) );
        }
        if ( full )
        {
            message.getValues ().put ( ItemListHelper.FIELD_FULL, VoidValue.INSTANCE );
        }
        this.messenger.sendMessage ( message );
    }

}
