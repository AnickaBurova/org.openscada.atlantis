/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.ae.client.test.wizzard;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

class NewStorageWizardConnectionPage extends WizardPage implements IWizardPage
{

    private Text _hostNameText;

    private Text _portNumberText;

    private boolean _hostValid = false;

    private Button _checkHost;

    protected NewStorageWizardConnectionPage ()
    {
        super ( "wizardPage" );
        setTitle ( "Connection information" );
        setDescription ( "Enter the connection information" );
    }

    public void createControl ( final Composite parent )
    {
        final Composite container = new Composite ( parent, SWT.NULL );

        final GridLayout layout = new GridLayout ();
        container.setLayout ( layout );
        layout.numColumns = 3;
        layout.verticalSpacing = 9;

        Label label = new Label ( container, SWT.NULL );
        label.setText ( "&Host:" );

        this._hostNameText = new Text ( container, SWT.BORDER | SWT.SINGLE );
        GridData gd = new GridData ( GridData.FILL_HORIZONTAL );
        this._hostNameText.setLayoutData ( gd );
        this._hostNameText.addModifyListener ( new ModifyListener () {
            public void modifyText ( final ModifyEvent e )
            {
                NewStorageWizardConnectionPage.this._hostValid = false;
                dialogChanged ();
            }
        } );
        this._checkHost = new Button ( container, SWT.NONE );
        this._checkHost.setText ( "Check" );
        this._checkHost.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( final SelectionEvent e )
            {
                handleCheckHost ();
            }
        } );

        label = new Label ( container, SWT.NULL );
        label.setText ( "&Port:" );

        this._portNumberText = new Text ( container, SWT.BORDER | SWT.SINGLE );
        gd = new GridData ( GridData.FILL_HORIZONTAL );
        this._portNumberText.setLayoutData ( gd );
        this._portNumberText.addModifyListener ( new ModifyListener () {
            public void modifyText ( final ModifyEvent e )
            {
                dialogChanged ();
            }
        } );

        setControl ( container );

        dialogChanged ();
    }

    private void handleCheckHost ()
    {
        try
        {
            InetAddress.getByName ( this._hostNameText.getText () );
            this._hostValid = true;
        }
        catch ( final UnknownHostException e )
        {
            MessageDialog.openError ( getShell (), "Host name lookup", "Failed to look up host: " + e.getMessage () );
        }
        finally
        {
            dialogChanged ();
        }
    }

    private void dialogChanged ()
    {
        this._checkHost.setEnabled ( false );

        if ( this._hostNameText.getText ().length () <= 0 )
        {
            updateStatus ( "Host name may not be empty" );
            return;
        }
        if ( !this._hostNameText.getText ().matches ( "([\\p{L}0-9-:]+(|\\.))+" ) )
        {
            updateStatus ( "Host name looks not like a valid hostname" );
            return;
        }
        this._checkHost.setEnabled ( true );
        if ( this._portNumberText.getText ().length () <= 0 )
        {
            updateStatus ( "Port number may not be empty" );
            return;
        }
        if ( !this._portNumberText.getText ().matches ( "[-+]?[0-9]+" ) )
        {
            updateStatus ( "Port number must be an number" );
            return;
        }
        // check port number
        int port;
        try
        {
            port = Integer.valueOf ( this._portNumberText.getText () );
            if ( port < 1 )
            {
                updateStatus ( "Port number must be greater than 0" );
                return;
            }
            if ( port >= 0xFFFF )
            {
                updateStatus ( "Port number must be lesser than " + 0xFFFF );
                return;
            }
        }
        catch ( final Exception e )
        {
            updateStatus ( "Port number invalid: " + e.getMessage () );
            return;
        }
        // check host valid
        if ( !this._hostValid )
        {
            updateStatus ( "Host is not checked!" );
            return;
        }
        updateStatus ( null );
    }

    private void updateStatus ( final String message )
    {
        setErrorMessage ( message );
        setPageComplete ( message == null );
    }

    public String getHostName ()
    {
        return this._hostNameText.getText ();
    }

    public int getPort ()
    {
        try
        {
            return Integer.valueOf ( this._portNumberText.getText () );
        }
        catch ( final Exception e )
        {
            return -1;
        }
    }
}