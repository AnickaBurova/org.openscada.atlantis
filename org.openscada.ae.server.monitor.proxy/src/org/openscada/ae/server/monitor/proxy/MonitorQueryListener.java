/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.ae.server.monitor.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.openscada.ae.MonitorStatusInformation;
import org.openscada.ae.client.MonitorListener;
import org.openscada.ae.connection.provider.ConnectionService;
import org.openscada.core.connection.provider.ConnectionIdTracker;
import org.openscada.core.connection.provider.ConnectionTracker.Listener;
import org.openscada.core.subscription.SubscriptionState;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MonitorQueryListener implements Listener, MonitorListener
{

    private final static Logger logger = LoggerFactory.getLogger ( MonitorQueryListener.class );

    private final ConnectionIdTracker tracker;

    private ConnectionService connection;

    private final String monitorQueryId;

    private final Map<String, MonitorStatusInformation> dataCache = new HashMap<String, MonitorStatusInformation> ();

    private final Lock lock;

    private final ProxyMonitorQuery proxyMonitorQuery;

    private final String info;

    public MonitorQueryListener ( final BundleContext context, final String connectionId, final String monitorQueryId, final ProxyMonitorQuery proxyMonitorQuery, final Lock lock )
    {
        logger.info ( "Creating new listener - connection: {}, query: {}", connectionId, monitorQueryId );

        this.info = connectionId + "#" + monitorQueryId;

        this.lock = lock;
        this.monitorQueryId = monitorQueryId;
        this.proxyMonitorQuery = proxyMonitorQuery;
        this.tracker = new ConnectionIdTracker ( context, connectionId, this, ConnectionService.class );
        this.tracker.open ();
    }

    public void dispose ()
    {
        this.tracker.close ();
    }

    @Override
    public void setConnection ( final org.openscada.core.connection.provider.ConnectionService connectionService )
    {
        logger.debug ( "Setting connection: {}", connectionService );

        this.lock.lock ();
        try
        {
            if ( this.connection != null )
            {
                this.connection.getConnection ().setConditionListener ( this.monitorQueryId, null );
                clearAll ();
            }

            this.connection = (ConnectionService)connectionService;

            if ( this.connection != null )
            {
                this.connection.getConnection ().setConditionListener ( this.monitorQueryId, this );
            }
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    @Override
    public void statusChanged ( final SubscriptionState state )
    {
        logger.info ( "State of {} changed: {}", this.info, state );
        switch ( state )
        {
            case DISCONNECTED:
                //$FALL-THROUGH$
            case GRANTED:
                clearAll ();
                break;
            case CONNECTED:
                break;
        }
    }

    private void clearAll ()
    {
        this.lock.lock ();
        try
        {
            final String[] removed = this.dataCache.keySet ().toArray ( new String[0] );
            this.dataCache.clear ();
            notifyChange ( null, removed );
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    @Override
    public void dataChanged ( final MonitorStatusInformation[] addedOrUpdated, final String[] removed )
    {
        logger.debug ( "Data of {} changed - added: @{}, removed: @{}", new Object[] { this.info, addedOrUpdated == null ? -1 : addedOrUpdated.length, removed == null ? -1 : removed.length } );

        this.lock.lock ();
        try
        {
            if ( addedOrUpdated != null )
            {
                for ( final MonitorStatusInformation info : addedOrUpdated )
                {
                    this.dataCache.put ( info.getId (), info );
                }
            }
            if ( removed != null )
            {
                for ( final String id : removed )
                {
                    this.dataCache.remove ( id );
                }
            }
            notifyChange ( addedOrUpdated, removed );
        }
        finally
        {
            this.lock.unlock ();
        }
    }

    private void notifyChange ( final MonitorStatusInformation[] addedOrUpdated, final String[] removed )
    {
        this.proxyMonitorQuery.handleDataUpdate ( addedOrUpdated, removed );
    }
}