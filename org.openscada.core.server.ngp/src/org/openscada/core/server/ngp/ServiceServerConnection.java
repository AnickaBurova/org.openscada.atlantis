/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.core.server.ngp;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.mina.core.session.IoSession;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.data.CallbackRequest;
import org.openscada.core.data.CallbackResponse;
import org.openscada.core.data.ErrorInformation;
import org.openscada.core.data.RequestMessage;
import org.openscada.core.data.ResponseMessage;
import org.openscada.core.data.message.CreateSession;
import org.openscada.core.data.message.RequestCallbacks;
import org.openscada.core.data.message.RespondCallbacks;
import org.openscada.core.data.message.SessionAccepted;
import org.openscada.core.data.message.SessionPrivilegesChanged;
import org.openscada.core.data.message.SessionRejected;
import org.openscada.core.ngp.Features;
import org.openscada.core.ngp.MessageSender;
import org.openscada.core.ngp.ResponseManager;
import org.openscada.core.server.Service;
import org.openscada.core.server.Session;
import org.openscada.core.server.Session.SessionListener;
import org.openscada.sec.callback.Callback;
import org.openscada.sec.callback.CallbackHandler;
import org.openscada.sec.callback.Callbacks;
import org.openscada.sec.callback.PropertiesCredentialsCallback;
import org.openscada.utils.concurrent.CallingFuture;
import org.openscada.utils.concurrent.DirectExecutor;
import org.openscada.utils.concurrent.FutureListener;
import org.openscada.utils.concurrent.NotifyFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServiceServerConnection<T extends Session, S extends Service<T>> extends ServerConnection
{
    private final static Logger logger = LoggerFactory.getLogger ( ServiceServerConnection.class );

    protected final S service;

    protected T session;

    private boolean enablePrivs;

    private boolean logonInProgress;

    private boolean enableCallbacks;

    private final ResponseManager responseManager;

    private final MessageSender messageSender = new MessageSender () {

        @Override
        public void sendMessage ( final Object message )
        {
            ServiceServerConnection.this.sendMessage ( message );
        }
    };

    public ServiceServerConnection ( final IoSession session, final S service )
    {
        super ( session );
        this.service = service;
        this.responseManager = new ResponseManager ( this.statistics, this.messageSender, DirectExecutor.INSTANCE );
        this.responseManager.connected ();
    }

    @Override
    public void messageReceived ( final Object message ) throws Exception
    {
        logger.trace ( "Received message : {}", message );

        if ( message instanceof CreateSession )
        {
            handleCreateSession ( (CreateSession)message );
        }
        else if ( message instanceof ResponseMessage )
        {
            this.responseManager.handleResponse ( (ResponseMessage)message );
        }
    }

    protected synchronized void handleCreateSession ( final CreateSession message )
    {
        if ( this.logonInProgress )
        {
            logger.warn ( "Logon already in progress" );
            return;
        }

        try
        {
            this.enablePrivs = message.getProperties ().containsKey ( Features.FEATURE_SESSION_PRIVILEGES );
            logger.info ( "Enable privileges: {}", this.enablePrivs ); //$NON-NLS-1$

            this.enableCallbacks = message.getProperties ().containsKey ( Features.FEATURE_CALLBACKS );
            logger.info ( "Enable callbacks: {}", this.enableCallbacks ); //$NON-NLS-1$

            this.logonInProgress = true;

            final CallbackHandler callbackHandler;

            callbackHandler = createCallbackHandlerFromMessage ( message );

            final NotifyFuture<T> future = performCreateSession ( message.getProperties (), callbackHandler );

            future.addListener ( new FutureListener<T> () {

                @Override
                public void complete ( final Future<T> future )
                {
                    try
                    {
                        setSession ( future );
                    }
                    catch ( final Exception e )
                    {
                        logger.warn ( "Failed to set session", e );
                        failSession ( e );
                    }

                }
            } );
        }
        catch ( final Exception e )
        {
            logger.warn ( "Failed to create session", e );
            failSession ( e );
        }
    }

    private CallbackHandler createCallbackHandlerFromMessage ( final CreateSession message )
    {
        if ( message.getProperties ().containsKey ( "user" ) && message.getProperties ().containsKey ( "password" ) )
        {
            logger.info ( "Using properties based callback handler since 'username' and 'password' are provided" );
            return new PropertiesCredentialsCallback ( message.getProperties () );
        }

        return createCallbackHandler ( message.getCallbackHandlerId () );
    }

    /**
     * @since 1.1
     */
    protected synchronized void failSession ( final Exception e )
    {
        this.logonInProgress = false;
        sendMessage ( makeRejectMessage ( e ) );
        requestClose ( false );
    }

    /**
     * @since 1.1
     */
    protected synchronized void setSession ( final Future<T> sessionFuture ) throws Exception
    {
        this.logonInProgress = false;

        this.session = sessionFuture.get ();

        initializeSession ( this.session );

        sendMessage ( makeSuccessMessage ( this.session.getProperties () ) );
        this.session.addSessionListener ( new SessionListener () {

            @Override
            public void privilegeChange ()
            {
                handlePrivilegeChange ();
            }
        } );
    }

    /**
     * @since 1.1
     */
    protected void initializeSession ( final T session )
    {
    }

    protected SessionPrivilegesChanged makePrivilegeChangeMessage ( final Set<String> privileges )
    {
        return new SessionPrivilegesChanged ( privileges );
    }

    private SessionAccepted makeSuccessMessage ( final Map<String, String> properties )
    {
        return new SessionAccepted ( properties );
    }

    private SessionRejected makeRejectMessage ( final Exception e )
    {
        return new SessionRejected ( e.getMessage () );
    }

    private NotifyFuture<T> performCreateSession ( final Map<String, String> properties, final CallbackHandler callbackHandler )
    {
        if ( this.session != null )
        {
            throw new IllegalStateException ( "A session was already created" );
        }

        final Properties p = new Properties ();
        p.putAll ( properties );
        return createSession ( p, callbackHandler );
    }

    /**
     * @param callbackHandler
     * @since 1.1
     */
    protected NotifyFuture<T> createSession ( final Properties properties, final CallbackHandler callbackHandler )
    {
        return this.service.createSession ( properties, callbackHandler );
    }

    @Override
    public void dispose ()
    {
        final T session;

        synchronized ( this )
        {
            session = this.session;
            this.session = null;
        }

        this.responseManager.disconnected ();

        if ( session != null )
        {
            try
            {
                this.service.closeSession ( session );
            }
            catch ( final InvalidSessionException e )
            {
                logger.warn ( "Failed to close session", e );
            }
        }

        super.dispose ();
    }

    private void handlePrivilegeChange ()
    {
        if ( this.enablePrivs )
        {
            sendMessage ( makePrivilegeChangeMessage ( ServiceServerConnection.this.session.getPrivileges () ) );
        }
    }

    /**
     * @since 1.1
     */
    protected CallbackHandler createCallbackHandler ( final Long callbackHandlerId )
    {
        if ( callbackHandlerId == null )
        {
            return null;
        }
        else
        {
            return new CallbackHandler () {

                @Override
                public NotifyFuture<Callback[]> performCallback ( final Callback[] callbacks )
                {
                    return performCallbacks ( callbackHandlerId, callbacks );
                }
            };
        }
    }

    private synchronized NotifyFuture<Callback[]> performCallbacks ( final long callbackHandlerId, final Callback[] callbacks )
    {
        if ( !this.enableCallbacks )
        {
            return Callbacks.cancelAll ( callbacks );
        }

        final NotifyFuture<ResponseMessage> future = this.responseManager.sendRequestMessage ( makeCallbackMessage ( callbackHandlerId, callbacks ) );
        return new CallingFuture<ResponseMessage, Callback[]> ( future ) {

            @Override
            public Callback[] call ( final Future<ResponseMessage> future ) throws Exception
            {
                parseCallbackResponse ( (RespondCallbacks)future.get (), callbacks );
                return callbacks;
            }
        };
    }

    private void parseCallbackResponse ( final RespondCallbacks response, final Callback[] callbacks )
    {
        final ErrorInformation error = response.getErrorInformation ();

        if ( error == null && response.getCallbacks ().size () == callbacks.length )
        {
            int i = 0;

            for ( final CallbackResponse cr : response.getCallbacks () )
            {
                if ( cr.isCanceled () )
                {
                    callbacks[i].cancel ();
                }
                else
                {
                    callbacks[i].parseResponseAttributes ( cr.getAttributes () );
                }
                i++;
            }
        }
        else if ( error != null )
        {
            // FIXME: should provide a real exception from error information
            throw new RuntimeException ( error.getMessage () );
        }
        else
        {
            throw new RuntimeException ( "Unknown error" );
        }
    }

    private RequestMessage makeCallbackMessage ( final long callbackHandlerId, final Callback[] callbacks )
    {
        final List<CallbackRequest> requests = new LinkedList<CallbackRequest> ();

        for ( final Callback cb : callbacks )
        {
            requests.add ( new CallbackRequest ( cb.getType (), cb.buildRequestAttributes () ) );
        }

        return new RequestCallbacks ( this.responseManager.nextRequest (), callbackHandlerId, requests, Callback.DEFAULT_TIMEOUT );
    }
}