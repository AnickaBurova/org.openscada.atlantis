package org.openscada.da.client.net.operations;

import org.apache.log4j.Logger;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.net.OperationFailedException;
import org.openscada.da.client.net.OperationTimedOutException;
import org.openscada.da.client.net.ProtocolErrorException;
import org.openscada.da.core.browser.Entry;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;
import org.openscada.net.da.handler.ListBrowser;
import org.openscada.net.da.handler.Messages;
import org.openscada.utils.exec.AsyncBasedOperation;
import org.openscada.utils.exec.OperationResult;

public class BrowserListOperation extends AsyncBasedOperation<Entry[], String[]> 
{
    private static Logger _log = Logger.getLogger ( BrowserListOperation.class );
    
    private Connection _connection = null;
    
    public BrowserListOperation ( Connection connection )
    {
        _connection = connection;
    }

    @Override
    protected void startExecute ( final OperationResult<Entry[]> or, String[] arg0 )
    {
        Message message = ListBrowser.createRequest ( arg0 );
        
        _connection.sendMessage ( message, new MessageStateListener() {

            public void messageReply ( Message message )
            {
                switch ( message.getCommandCode () )
                {
                case Messages.CC_BROWSER_LIST_RES:
                    Entry[] entries = ListBrowser.parseResponse ( message );
                    or.notifySuccess ( entries );
                    break;
                    
                case Message.CC_FAILED:
                    String failure = "unknown failure";

                    if ( message.getValues ().containsKey ( Message.FIELD_ERROR_INFO ) )
                        failure = message.getValues ().get ( Message.FIELD_ERROR_INFO ).toString();

                    or.notifyFailure ( new OperationFailedException ( failure ) );
                    break;

                default:
                {
                    _log.warn ( "Invalid reply to write operation cc=" + message.getCommandCode () );
                    or.notifyFailure ( new ProtocolErrorException () );
                    break;
                }
                }
            }

            public void messageTimedOut ()
            {
                or.notifyFailure ( new OperationTimedOutException () );
            }} );
    }

}
