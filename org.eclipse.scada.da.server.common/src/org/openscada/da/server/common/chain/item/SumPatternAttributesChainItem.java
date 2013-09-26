/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.chain.item;

import java.util.regex.Pattern;

import org.eclipse.scada.core.Variant;

/**
 * A chain item that summarizes based on a pattern.
 * 
 * @author Jens Reimann &lt;jens.reimann@th4-systems.com&gt;
 */
public class SumPatternAttributesChainItem extends SummarizeChainItem
{
    private final Pattern pattern;

    public SumPatternAttributesChainItem ( final String baseName, final String pattern )
    {
        this ( baseName, Pattern.compile ( pattern ) );
    }

    public SumPatternAttributesChainItem ( final String baseName, final Pattern pattern )
    {
        super ( baseName );

        this.pattern = pattern;
    }

    @Override
    protected boolean matches ( final Variant value, final String attributeName, final Variant attributeValue )
    {
        return this.pattern.matcher ( attributeName ).matches ();
    }

}