/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.da.server.exec;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.emf.common.util.URI;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.exec.configuration.RootType;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.exec.command.CommandQueue;
import org.eclipse.scada.da.server.exec.command.ContinuousCommand;
import org.eclipse.scada.da.server.exec.command.TriggerCommand;
import org.eclipse.scada.da.server.exec.configuration.ConfigurationException;
import org.eclipse.scada.da.server.exec.configuration.XmlConfigurator;
import org.eclipse.scada.utils.collection.MapBuilder;
import org.openscada.da.server.common.impl.HiveCommon;

public class Hive extends HiveCommon
{
    private static final String TRIGGER_FOLDER_NAME = "triggers";

    /**
     * Root folder of the Hive
     */
    private final FolderCommon rootFolder = new FolderCommon ();

    private final Collection<CommandQueue> queues = new LinkedList<CommandQueue> ();

    private final Collection<ContinuousCommand> continuousCommands = new LinkedList<ContinuousCommand> ();

    private final Collection<TriggerCommand> triggers = new LinkedList<TriggerCommand> ();

    private final FolderCommon triggerFolder;

    /**
     * Default Constructor
     * 
     * @throws XmlException
     * @throws IOException
     * @throws ConfigurationException
     */
    public Hive () throws IOException, ConfigurationException
    {
        this ( new XmlConfigurator ( URI.createFileURI ( "configuration.xml" ) ) );
    }

    public Hive ( final String uri ) throws ConfigurationException
    {
        this ( new XmlConfigurator ( URI.createURI ( uri ) ) );
    }

    public Hive ( final RootType root ) throws ConfigurationException
    {
        this ( new XmlConfigurator ( root ) );
    }

    protected Hive ( final XmlConfigurator configurator ) throws ConfigurationException
    {
        setRootFolder ( this.rootFolder );
        this.triggerFolder = new FolderCommon ();
        this.rootFolder.add ( TRIGGER_FOLDER_NAME, this.triggerFolder, new MapBuilder<String, Variant> ().put ( "description", Variant.valueOf ( "Contains all triggers" ) ).getMap () );

        configurator.configure ( this );
    }

    @Override
    public String getHiveId ()
    {
        return "org.eclipse.scada.da.server.exec";
    }

    /**
     * Initializes all configured command queues and executes them in threads
     */
    protected void startQueues ()
    {
        for ( final CommandQueue queue : this.queues )
        {
            queue.start ( this, this.rootFolder );
        }
        for ( final ContinuousCommand command : this.continuousCommands )
        {
            command.start ( this, this.rootFolder );
        }
        for ( final TriggerCommand command : this.triggers )
        {
            command.register ( this, this.triggerFolder );
        }
    }

    @Override
    protected void performStart () throws Exception
    {
        super.performStart ();
        startQueues ();
    }

    @Override
    protected void performStop () throws Exception
    {
        stopQueues ();
        super.performStop ();
    }

    protected void stopQueues ()
    {
        for ( final CommandQueue queue : this.queues )
        {
            queue.stop ();
        }
        for ( final ContinuousCommand command : this.continuousCommands )
        {
            command.stop ();
        }
        for ( final TriggerCommand command : this.triggers )
        {
            command.unregister ();
        }
    }

    public void addQueue ( final CommandQueue queue )
    {
        this.queues.add ( queue );
    }

    public void addContinuousCommand ( final ContinuousCommand command )
    {
        this.continuousCommands.add ( command );
    }

    /**
     * Add a new trigger command
     * 
     * @param command
     *            the new trigger command
     */
    public void addTrigger ( final TriggerCommand command )
    {
        this.triggers.add ( command );
    }
}