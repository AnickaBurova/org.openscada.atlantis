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

package org.eclipse.scada.core.connection.provider;

import org.eclipse.scada.core.client.AutoReconnectController;
import org.eclipse.scada.core.client.Connection;
import org.eclipse.scada.sec.callback.CallbackHandler;

public interface ConnectionService
{
    public Connection getConnection ();

    public AutoReconnectController getAutoReconnectController ();

    public void connect ();

    /**
     * @since 1.1
     */
    public void setConnectCallbackHandler ( CallbackHandler callbackHandler );

    public void disconnect ();

    public static final String CONNECTION_URI = "connection.uri";

    public Class<?>[] getSupportedInterfaces ();

    public void dispose ();
}