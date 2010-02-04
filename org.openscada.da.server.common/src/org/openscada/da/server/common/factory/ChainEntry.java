/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common.factory;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;

public class ChainEntry
{
    private Class<?> _what = null;

    private EnumSet<IODirection> _when = EnumSet.noneOf ( IODirection.class );

    public Class<?> getWhat ()
    {
        return this._what;
    }

    public void setWhat ( final Class<?> what )
    {
        this._what = what;
    }

    public EnumSet<IODirection> getWhen ()
    {
        return this._when;
    }

    public void setWhen ( final EnumSet<IODirection> when )
    {
        this._when = when;
    }

}