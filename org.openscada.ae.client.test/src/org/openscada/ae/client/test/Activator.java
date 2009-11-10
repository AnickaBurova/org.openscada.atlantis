/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.ae.client.test;

import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openscada.ae.client.test.impl.StorageConnection;
import org.openscada.ae.client.test.impl.StorageConnectionInformation;
import org.openscada.ae.client.test.impl.StorageRepository;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

    // The plug-in ID
    public static final String PLUGIN_ID = "org.openscada.ae.client.test";

    // The shared instance
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator ()
    {
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start ( final BundleContext context ) throws Exception
    {
        super.start ( context );
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop ( final BundleContext context ) throws Exception
    {
        plugin = null;
        super.stop ( context );
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Activator getDefault ()
    {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor ( final String path )
    {
        return imageDescriptorFromPlugin ( PLUGIN_ID, path );
    }

    public static String getId ()
    {
        return getDefault ().getBundle ().getSymbolicName ();
    }

    public static void logError ( final int code, final String msg, final Throwable ex )
    {
        getDefault ().getLog ().log ( new Status ( IStatus.ERROR, getId (), code, msg, ex ) );
    }

    public void notifyError ( final String message, final Throwable error )
    {
        final Display display = getWorkbench ().getDisplay ();

        if ( !display.isDisposed () )
        {
            display.asyncExec ( new Runnable () {

                public void run ()
                {
                    final Shell shell = getWorkbench ().getActiveWorkbenchWindow ().getShell ();
                    if ( !shell.isDisposed () )
                    {
                        final IStatus status = new OperationStatus ( IStatus.ERROR, PLUGIN_ID, 0, error.getMessage (), error );
                        ErrorDialog.openError ( shell, null, message, status );
                    }
                }
            } );
        }
    }

    @Override
    protected void initializeImageRegistry ( final ImageRegistry reg )
    {
        super.initializeImageRegistry ( reg );

        getImageRegistry ().put ( ISharedImages.IMG_HIVE_CONNECTION, getImageDescriptor ( "icons/stock_channel.png" ) );
        getImageRegistry ().put ( ISharedImages.IMG_HIVE_CONNECTED, getImageDescriptor ( "icons/stock_connect.png" ) );
        getImageRegistry ().put ( ISharedImages.IMG_HIVE_DISCONNECTED, getImageDescriptor ( "icons/stock_disconnect.png" ) );
    }

    private static StorageRepository repository = null;

    public static StorageRepository getRepository ()
    {
        if ( repository == null )
        {
            repository = new StorageRepository ();

            final IPath storages = getRepostoryFile ();
            if ( storages.toFile ().canRead () )
            {
                repository.load ( storages );
            }
            else
            {
                final StorageConnectionInformation connection = new StorageConnectionInformation ();
                connection.setHost ( "localhost" );
                connection.setPort ( 1302 );
                repository.getConnections ().add ( new StorageConnection ( connection ) );
                repository.save ( storages );
            }

        }
        return repository;
    }

    public static IPath getRepostoryFile ()
    {
        return getDefault ().getStateLocation ().append ( "storages.xml" );
    }

}