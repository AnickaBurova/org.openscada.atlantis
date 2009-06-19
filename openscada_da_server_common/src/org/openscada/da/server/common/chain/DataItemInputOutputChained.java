/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.chain;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteResult;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.utils.concurrent.NotifyFuture;

public abstract class DataItemInputOutputChained extends DataItemInputChained
{
    public DataItemInputOutputChained ( final DataItemInformation di, final Executor executor )
    {
        super ( di, executor );
    }

    public DataItemInputOutputChained ( final String id, final Executor executor )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ), executor );
    }

    @Override
    public NotifyFuture<WriteResult> startWriteValue ( Variant value )
    {
        synchronized ( this )
        {
            final Collection<ChainProcessEntry> chain = getChainCopy ();

            final Map<String, Variant> primaryAttributes = new HashMap<String, Variant> ( this.primaryAttributes );

            for ( final ChainProcessEntry entry : chain )
            {
                if ( entry.getWhen ().contains ( IODirection.OUTPUT ) )
                {
                    final Variant newValue = entry.getWhat ().process ( value, primaryAttributes );
                    if ( newValue != null )
                    {
                        value = newValue;
                    }
                }
            }
        }
        // FIXME: for the moment output chain item don't show up in the attribute list
        // secondaryAttributes.set ( primaryAttributes );

        return startWriteCalculatedValue ( value );
    }

    protected abstract NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value );
}
