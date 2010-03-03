/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.core;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.openscada.utils.lang.Immutable;

@Immutable
public class WriteAttributeResult
{
    private Throwable error = null;

    /**
     * A constant that represents the <q>OK</q> state
     */
    public static final WriteAttributeResult OK = new WriteAttributeResult ();

    /**
     * Create an <q>OK</q> result
     * @deprecated use the {@link #OK} constant instead
     */
    @Deprecated
    public WriteAttributeResult ()
    {
    }

    /**
     * Create a result with error information
     * @param error the error or <code>null</code> if the result is "ok"
     */
    public WriteAttributeResult ( final Throwable error )
    {
        this.error = error;
    }

    public Throwable getError ()
    {
        return this.error;
    }

    public boolean isError ()
    {
        return this.error != null;
    }

    public boolean isSuccess ()
    {
        return this.error == null;
    }

    @Override
    public String toString ()
    {
        if ( this.error == null )
        {
            return "OK";
        }
        String message = this.error.getMessage ();
        if ( message == null )
        {
            final StringWriter sw = new StringWriter ();
            final PrintWriter pw = new PrintWriter ( sw );
            this.error.printStackTrace ( pw );
            pw.close ();
            message = sw.toString ();
        }
        return message;
    }
}