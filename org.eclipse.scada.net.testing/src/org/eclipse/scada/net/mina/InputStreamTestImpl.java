/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.net.mina;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.eclipse.scada.net.mina.GMPPProtocolDecoder;
import org.eclipse.scada.net.mina.GMPPProtocolEncoder;

public class InputStreamTestImpl
{
    private List<BytePacketTestImpl> _pseudoStream = new ArrayList<BytePacketTestImpl> ();

    /**
     * 
     */
    public InputStreamTestImpl ()
    {
        super ();
    }

    public InputStreamTestImpl ( final Collection<BytePacketTestImpl> packetList )
    {
        this._pseudoStream = new ArrayList<BytePacketTestImpl> ( packetList );
    }

    public void clear ()
    {
        this._pseudoStream.clear ();
    }

    public void add ( final BytePacketTestImpl packet )
    {
        this._pseudoStream.add ( packet );
    }

    public void run ( final GMPPProtocolDecoder decoder, final GMPPProtocolEncoder encoder, final ProtocolDecoderOutput in ) throws Exception
    {
        BytePacketTestImpl packet;
        final Iterator<BytePacketTestImpl> i = this._pseudoStream.iterator ();
        while ( i.hasNext () )
        {
            packet = i.next ();
            packet.process ( decoder, encoder, in );
        }
    }
}