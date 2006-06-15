package org.openscada.da.client.test.wizards;


import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.openscada.da.client.test.impl.HiveConnection;
import org.openscada.da.client.test.impl.HiveItem;
import org.openscada.da.core.data.Variant;

public class WriteOperationWizard extends Wizard implements INewWizard
{
    
    private WriteOperationWizardValuePage _page = null;
    
    private IStructuredSelection _selection = null;
    
    @Override
    public boolean performFinish ()
    {
        final String item = _page.getItem ();
        final Variant value = _page.getValue ();
        final HiveConnection connection = _page.getConnection ();
        
        IRunnableWithProgress op = new IRunnableWithProgress()
        {
            public void run ( IProgressMonitor monitor ) throws InvocationTargetException
            {
                try
                {
                    doFinish ( monitor, connection, item, value );
                }
                catch ( Exception e )
                {
                    throw new InvocationTargetException ( e );
                }
                finally
                {
                    monitor.done ();
                }
            }
        };
        try
        {
            getContainer().run(true, false, op);
        }
        catch (InterruptedException e)
        {
            return false;
        }
        catch (InvocationTargetException e)
        {
            Throwable realException = e.getTargetException();
            MessageDialog.openError ( getShell(), "Error writing to item", realException.getMessage () );
            return false;
        }
        return true;
    }
    
    private void doFinish ( IProgressMonitor monitor, HiveConnection hiveConnection, String item, Variant value ) throws Exception
    {
        monitor.beginTask ( "Writing value to item" , 2 );
        
        monitor.worked ( 1 );
        hiveConnection.getConnection ().write ( item, value );
        monitor.worked ( 1 );
    }

    public void init ( IWorkbench workbench, IStructuredSelection selection )
    {
        setNeedsProgressMonitor ( true );
        
        _selection = selection;
    }
    
    @Override
    public void addPages ()
    {
        super.addPages ();
        
        addPage ( _page = new WriteOperationWizardValuePage() );
        
        _page.setSelection ( _selection );
    }
    

}
