/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.datasource.base;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.da.client.DataItemValue;
import org.openscada.da.datasource.DataSource;
import org.openscada.da.datasource.DataSourceListener;
import org.openscada.da.datasource.SingleDataSourceTracker;
import org.openscada.da.datasource.SingleDataSourceTracker.ServiceListener;
import org.openscada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of a data source which is based on another data source.
 * 
 * @author Jens Reimann
 * @since 0.15.0
 */
public abstract class AbstractDataSourceHandler extends AbstractDataSource
{

    private final static Logger logger = LoggerFactory.getLogger ( AbstractDataSourceHandler.class );

    private final ObjectPoolTracker<DataSource> poolTracker;

    private SingleDataSourceTracker tracker;

    private final ServiceListener serviceListener;

    private DataSource dataSource;

    private final DataSourceListener dataSourceListener;

    private final Lock dataSourceReadLock;

    private final Lock dataSourceWriteLock;

    private final Lock trackerLock;

    public AbstractDataSourceHandler ( final ObjectPoolTracker<DataSource> poolTracker )
    {
        this.poolTracker = poolTracker;
        this.serviceListener = new ServiceListener () {

            @Override
            public void dataSourceChanged ( final DataSource dataSource )
            {
                AbstractDataSourceHandler.this.setDataSource ( dataSource );
            }
        };

        this.dataSourceListener = new DataSourceListener () {

            @Override
            public void stateChanged ( final DataItemValue value )
            {
                AbstractDataSourceHandler.this.stateChanged ( value );
            }
        };

        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock ();
        this.dataSourceReadLock = lock.readLock ();
        this.dataSourceWriteLock = lock.writeLock ();

        this.trackerLock = new ReentrantLock ();
    }

    protected abstract void stateChanged ( DataItemValue value );

    protected void setDataSource ( final DataSource dataSource )
    {
        logger.debug ( "Set datasource: {}", dataSource );

        try
        {
            this.dataSourceWriteLock.lock ();

            if ( this.dataSource != null )
            {
                this.dataSource.removeListener ( this.dataSourceListener );
                this.dataSource = null;

                stateChanged ( null );
            }

            this.dataSource = dataSource;

            if ( this.dataSource != null )
            {
                this.dataSource.addListener ( this.dataSourceListener );
            }
        }
        finally
        {
            this.dataSourceWriteLock.unlock ();
        }
    }

    protected DataSource getDataSource ()
    {
        try
        {
            this.dataSourceReadLock.lock ();
            return this.dataSource;
        }
        finally
        {
            this.dataSourceReadLock.unlock ();
        }
    }

    protected void setDataSource ( final String dataSourceId ) throws InvalidSyntaxException
    {
        logger.debug ( "Set datasource request: {}", dataSourceId );

        try
        {
            this.trackerLock.lock ();

            if ( this.tracker != null )
            {
                this.tracker.close ();
                this.tracker = null;
            }

            if ( dataSourceId != null )
            {
                this.tracker = new SingleDataSourceTracker ( this.poolTracker, dataSourceId, this.serviceListener );
                this.tracker.open ();
            }
        }
        finally
        {
            this.trackerLock.unlock ();
        }
    }

    public void dispose ()
    {
        if ( this.tracker != null )
        {
            this.tracker.close ();
            this.tracker = null;
        }
    }
}
