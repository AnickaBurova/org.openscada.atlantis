/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.chain.storage;

import java.io.File;

import org.openscada.da.server.common.impl.HiveCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChainStorageServiceHelper
{

    private final static Logger logger = LoggerFactory.getLogger ( ChainStorageServiceHelper.class );

    public static void registerService ( final HiveCommon hive, final ChainStorageService service )
    {
        hive.registerService ( ChainStorageService.SERVICE_ID, service );
    }

    public static void registerDefaultPropertyService ( final HiveCommon hive )
    {
        // add property file chain item storage
        final String propName = ChainStorageService.SERVICE_ID + ".path";
        final String dirName = System.getProperty ( propName, null );

        if ( dirName == null )
        {
            logger.info ( "Property file chain item storage service is not set ({})", propName );
            return;
        }

        final File dir = new File ( dirName );
        if ( dir.exists () && dir.isDirectory () )
        {
            hive.registerService ( ChainStorageService.SERVICE_ID, new PropertyFileChainStorageService ( new File ( dirName ) ) );
        }
    }
}
