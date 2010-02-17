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

package org.openscada.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.compression.CompressionFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.openscada.core.ConnectionInformation;
import org.openscada.net.mina.GMPPProtocolDecoder;
import org.openscada.net.mina.GMPPProtocolEncoder;

public class ConnectionHelper
{

    private static final class X509TrustManagerImplementation implements X509TrustManager
    {
        public void checkClientTrusted ( final X509Certificate[] arg0, final String arg1 ) throws CertificateException
        {
            System.out.println ( "checkClientTrusted: " + arg0 + "/" + arg1 );
        }

        public void checkServerTrusted ( final X509Certificate[] arg0, final String arg1 ) throws CertificateException
        {
            System.out.println ( "checkServerTrusted: " + arg0 + "/" + arg1 );
        }

        public X509Certificate[] getAcceptedIssuers ()
        {
            System.out.println ( "getAcceptedIssuers" );
            return new X509Certificate[0];
        }
    }

    private static Logger logger = Logger.getLogger ( ConnectionHelper.class );

    /**
     * Setup the filter chain of a NET/GMPP connection
     * @param connectionInformation the connection information to use
     * @param filterChainBuilder the chain builder
     */
    public static void setupFilterChain ( final ConnectionInformation connectionInformation, final DefaultIoFilterChainBuilder filterChainBuilder, final boolean isClient )
    {
        // set up compression
        final String compress = connectionInformation.getProperties ().get ( "compress" );
        if ( compress != null )
        {
            filterChainBuilder.addLast ( "compress", new CompressionFilter () );
        }

        // set up ssl
        final String ssl = connectionInformation.getProperties ().get ( "ssl" );
        if ( ssl != null )
        {
            initSsl ( connectionInformation, filterChainBuilder, isClient );
        }

        // set up logging
        final String trace = connectionInformation.getProperties ().get ( "trace" );
        if ( trace != null )
        {
            filterChainBuilder.addLast ( "logging", new LoggingFilter () );
        }

        // add the main codec
        filterChainBuilder.addLast ( "codec", new ProtocolCodecFilter ( new GMPPProtocolEncoder (), new GMPPProtocolDecoder () ) );
    }

    /**
     * FIXME: still need to implement correctly
     * @param connectionInformation
     * @param filterChainBuilder
     * @param isClient
     */
    protected static void initSsl ( final ConnectionInformation connectionInformation, final DefaultIoFilterChainBuilder filterChainBuilder, final boolean isClient )
    {
        SSLContext sslContext = null;
        try
        {
            sslContext = createContext ( connectionInformation );
            sslContext.init ( getKeyManagers ( connectionInformation, isClient ), getTrustManagers ( connectionInformation ), getRandom ( connectionInformation ) );
        }
        catch ( final Throwable e )
        {
            logger.warn ( "Failed to enable SSL", e );
        }

        if ( sslContext != null )
        {
            final SslFilter filter = new SslFilter ( sslContext );
            filter.setUseClientMode ( isClient );
            filterChainBuilder.addFirst ( "sslFilter", filter );
        }
    }

    private static SSLContext createContext ( final ConnectionInformation connectionInformation ) throws NoSuchAlgorithmException
    {
        String sslProtocol = connectionInformation.getProperties ().get ( "sslProtocol" );
        if ( sslProtocol == null || sslProtocol.length () == 0 )
        {
            sslProtocol = "SSLv3";
        }

        return SSLContext.getInstance ( sslProtocol );
    }

    private static SecureRandom getRandom ( final ConnectionInformation connectionInformation ) throws NoSuchAlgorithmException
    {
        final String sslRandom = connectionInformation.getProperties ().get ( "sslRandom" );

        SecureRandom random = null;
        if ( sslRandom != null && sslRandom.length () > 0 )
        {
            random = SecureRandom.getInstance ( sslRandom );
            return random;
        }
        return null;
    }

    private static TrustManager[] getTrustManagers ( final ConnectionInformation connectionInformation )
    {
        return new TrustManager[] { new X509TrustManagerImplementation () };
    }

    private static KeyManager[] getKeyManagers ( final ConnectionInformation connectionInformation, final boolean isClient ) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, IOException
    {
        if ( isClient )
        {
            return null;
        }

        final KeyStore keyStore;

        keyStore = createKeyStore ( connectionInformation );

        final String keyManagerFactory = KeyManagerFactory.getDefaultAlgorithm ();
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance ( keyManagerFactory );

        kmf.init ( keyStore, getPassword ( connectionInformation, "sslCertPassword" ) );

        return kmf.getKeyManagers ();
    }

    private static KeyStore createKeyStore ( final ConnectionInformation connectionInformation ) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
    {
        final KeyStore keyStore;
        final String keyStoreType = connectionInformation.getProperties ().get ( "sslKeyStoreType" );
        if ( keyStoreType != null )
        {
            keyStore = KeyStore.getInstance ( keyStoreType );
        }
        else
        {
            keyStore = KeyStore.getInstance ( KeyStore.getDefaultType () );
        }

        keyStore.load ( getKeyStoreStream ( connectionInformation ), getPassword ( connectionInformation, "sslKeyStorePassword" ) );

        return keyStore;
    }

    private static InputStream getKeyStoreStream ( final ConnectionInformation connectionInformation ) throws IOException
    {
        final String uri = connectionInformation.getProperties ().get ( "sslKeyStoreUri" );
        final URL url = new URL ( uri );
        return url.openStream ();
    }

    private static char[] getPassword ( final ConnectionInformation connectionInformation, final String propertyName )
    {
        final char[] passwordChars;
        final String password = connectionInformation.getProperties ().get ( propertyName );
        if ( password != null )
        {
            passwordChars = password.toCharArray ();
        }
        else
        {
            passwordChars = null;
        }
        return passwordChars;
    }
}