/*******************************************************************************
 * Copyright (c) 2009, 2010 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.da.server.simulation.component.modules;

public class ItemAlreadyRegisteredException extends RuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = -8771067823875105860L;

    public ItemAlreadyRegisteredException ( final String itemTag )
    {
        super ( String.format ( "Item '%s' is already registered", itemTag ) );
    }
}
