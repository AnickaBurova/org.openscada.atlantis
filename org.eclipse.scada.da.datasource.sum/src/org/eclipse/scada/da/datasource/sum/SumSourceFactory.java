/*******************************************************************************
 * Copyright (c) 2010, 2012 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.da.datasource.sum;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.ca.common.factory.AbstractServiceConfigurationFactory;
import org.eclipse.scada.da.datasource.DataSource;
import org.eclipse.scada.sec.UserInformation;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolHelper;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolImpl;
import org.eclipse.scada.utils.osgi.pool.ObjectPoolTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SumSourceFactory extends AbstractServiceConfigurationFactory<SumDataSource>
{

    private final static Logger logger = LoggerFactory.getLogger ( SumSourceFactory.class );

    private final Executor executor;

    private final ObjectPoolTracker<DataSource> poolTracker;

    private final ObjectPoolImpl<DataSource> objectPool;

    private final ServiceRegistration<?> poolRegistration;

    public SumSourceFactory ( final BundleContext context, final Executor executor ) throws InvalidSyntaxException
    {
        super ( context );
        this.executor = executor;

        this.objectPool = new ObjectPoolImpl<DataSource> ();
        this.poolRegistration = ObjectPoolHelper.registerObjectPool ( context, this.objectPool, DataSource.class );

        this.poolTracker = new ObjectPoolTracker<DataSource> ( context, DataSource.class );
        this.poolTracker.open ();
    }

    @Override
    public synchronized void dispose ()
    {
        this.poolRegistration.unregister ();
        this.objectPool.dispose ();
        this.poolTracker.close ();
        super.dispose ();
    }

    @Override
    protected Entry<SumDataSource> createService ( final UserInformation userInformation, final String configurationId, final BundleContext context, final Map<String, String> parameters ) throws Exception
    {
        logger.debug ( "Creating new summary source: {}", configurationId );

        final SumDataSource source = new SumDataSource ( this.poolTracker, this.executor );
        source.update ( parameters );

        final Dictionary<String, String> properties = new Hashtable<String, String> ( 1 );
        properties.put ( DataSource.DATA_SOURCE_ID, configurationId );

        this.objectPool.addService ( configurationId, source, properties );

        return new Entry<SumDataSource> ( configurationId, source );
    }

    @Override
    protected void disposeService ( final UserInformation userInformation, final String id, final SumDataSource service )
    {
        logger.info ( "Disposing: {}", id );

        this.objectPool.removeService ( id, service );

        service.dispose ();
    }

    @Override
    protected Entry<SumDataSource> updateService ( final UserInformation userInformation, final String configurationId, final Entry<SumDataSource> entry, final Map<String, String> parameters ) throws Exception
    {
        entry.getService ().update ( parameters );
        return null;
    }

}
