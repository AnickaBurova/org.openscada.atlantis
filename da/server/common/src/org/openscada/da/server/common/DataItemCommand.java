/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.core.WriteAttributeResults;

public class DataItemCommand extends DataItemOutput
{

    private static Logger _log = Logger.getLogger ( DataItemCommand.class );

    public DataItemCommand ( final String name )
    {
        super ( name );
    }

    public static interface Listener
    {
        public void command ( Variant value );
    }

    private final List<Listener> _listeners = new ArrayList<Listener> ();

    public void writeValue ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {

        List<Listener> listeners;
        synchronized ( this._listeners )
        {
            listeners = new ArrayList<Listener> ( this._listeners );
        }

        for ( final Listener listener : listeners )
        {
            try
            {
                listener.command ( value );
            }
            catch ( final Exception e )
            {
                _log.warn ( "Failed to run listener", e );

            }
        }
    }

    public void addListener ( final Listener listener )
    {
        synchronized ( this._listeners )
        {
            this._listeners.add ( listener );
        }
    }

    public void removeListener ( final Listener listener )
    {
        synchronized ( this._listeners )
        {
            this._listeners.remove ( listener );
        }
    }

    public Map<String, Variant> getAttributes ()
    {
        return new HashMap<String, Variant> ();
    }

    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        return WriteAttributesHelper.errorUnhandled ( null, attributes );
    }

}
