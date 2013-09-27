/*******************************************************************************
 * Copyright (c) 2013 TH4 SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     TH4 SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.da.server.modbus.io;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.eclipse.scada.da.server.modbus.io.message.RequestWrapper;
import org.eclipse.scada.da.server.modbus.io.message.request.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusRtuCodecFactory implements ProtocolCodecFactory, ResetableCodecFactory
{
    private static final Logger logger = LoggerFactory.getLogger ( ModbusRtuCodecFactory.class );

    public static final String SESSION_KEY_CURRENT_REQUEST = ModbusRtuCodecFactory.class.getName () + ".currentRequest";

    public static final int RTU_HEADER_SIZE = 3;

    private final ProtocolDecoder decoder;

    private final ProtocolEncoder encoder;

    public ModbusRtuCodecFactory ( final ScheduledExecutorService scheduler, final long timeout, final TimeUnit timeUnit )
    {
        this.decoder = new ModbusRtuDecoder ( scheduler, timeout, timeUnit );
        this.encoder = new ModbusRtuEncoder ();
    }

    @Override
    public ProtocolDecoder getDecoder ( final IoSession session ) throws Exception
    {
        return this.decoder;
    }

    @Override
    public ProtocolEncoder getEncoder ( final IoSession session ) throws Exception
    {
        return this.encoder;
    }

    @Override
    public void reset ( final IoSession session )
    {
        logger.debug ( "reset ()" );
        if ( session != null )
        {
            session.removeAttribute ( SESSION_KEY_CURRENT_REQUEST );
        }
    }

    public static RequestMessage getOriginalRequest ( final IoSession session ) throws ModbusProtocolError
    {
        final RequestWrapper requestWrapper = (RequestWrapper)session.getAttribute ( SESSION_KEY_CURRENT_REQUEST );
        if ( requestWrapper == null )
        {
            throw new ModbusProtocolError ( "no request message found" );
        }
        return requestWrapper.getMessage ();
    }
}
