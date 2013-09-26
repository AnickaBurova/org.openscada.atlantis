/**
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 * 
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 * 
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */
package org.eclipse.scada.common.provider;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.scada.common.util.CommonAdapterFactory;

/**
 * This is the factory that is used to provide the interfaces needed to support Viewers.
 * The adapters generated by this factory convert EMF adapter notifications into calls to {@link #fireNotifyChanged fireNotifyChanged}.
 * The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class CommonItemProviderAdapterFactory extends CommonAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier, IDisposable
{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "This file is part of the openSCADA project\n\nCopyright (C) 2013 Jens Reimann (ctron@dentrassi.de)\n\nopenSCADA is free software: you can redistribute it and/or modify\nit under the terms of the GNU Lesser General Public License version 3\nonly, as published by the Free Software Foundation.\n\nopenSCADA is distributed in the hope that it will be useful,\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\nGNU Lesser General Public License version 3 for more details\n(a copy is included in the LICENSE file that accompanied this code).\n\nYou should have received a copy of the GNU Lesser General Public License\nversion 3 along with openSCADA. If not, see\n<http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.";

    /**
     * This keeps track of the root adapter factory that delegates to this adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ComposedAdapterFactory parentAdapterFactory;

    /**
     * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected IChangeNotifier changeNotifier = new ChangeNotifier ();

    /**
     * This keeps track of all the supported types checked by {@link #isFactoryForType isFactoryForType}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected Collection<Object> supportedTypes = new ArrayList<Object> ();

    /**
     * This constructs an instance.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CommonItemProviderAdapterFactory ()
    {
        supportedTypes.add ( IEditingDomainItemProvider.class );
        supportedTypes.add ( IStructuredItemContentProvider.class );
        supportedTypes.add ( ITreeItemContentProvider.class );
        supportedTypes.add ( IItemLabelProvider.class );
        supportedTypes.add ( IItemPropertySource.class );
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.scada.common.AttributesType} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AttributesTypeItemProvider attributesTypeItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.scada.common.AttributesType}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createAttributesTypeAdapter ()
    {
        if ( attributesTypeItemProvider == null )
        {
            attributesTypeItemProvider = new AttributesTypeItemProvider ( this );
        }

        return attributesTypeItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.scada.common.AttributeType} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AttributeTypeItemProvider attributeTypeItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.scada.common.AttributeType}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createAttributeTypeAdapter ()
    {
        if ( attributeTypeItemProvider == null )
        {
            attributeTypeItemProvider = new AttributeTypeItemProvider ( this );
        }

        return attributeTypeItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.scada.common.VariantBooleanType} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected VariantBooleanTypeItemProvider variantBooleanTypeItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.scada.common.VariantBooleanType}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createVariantBooleanTypeAdapter ()
    {
        if ( variantBooleanTypeItemProvider == null )
        {
            variantBooleanTypeItemProvider = new VariantBooleanTypeItemProvider ( this );
        }

        return variantBooleanTypeItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.scada.common.VariantDoubleType} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected VariantDoubleTypeItemProvider variantDoubleTypeItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.scada.common.VariantDoubleType}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createVariantDoubleTypeAdapter ()
    {
        if ( variantDoubleTypeItemProvider == null )
        {
            variantDoubleTypeItemProvider = new VariantDoubleTypeItemProvider ( this );
        }

        return variantDoubleTypeItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.scada.common.VariantInt32Type} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected VariantInt32TypeItemProvider variantInt32TypeItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.scada.common.VariantInt32Type}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createVariantInt32TypeAdapter ()
    {
        if ( variantInt32TypeItemProvider == null )
        {
            variantInt32TypeItemProvider = new VariantInt32TypeItemProvider ( this );
        }

        return variantInt32TypeItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.scada.common.VariantInt64Type} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected VariantInt64TypeItemProvider variantInt64TypeItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.scada.common.VariantInt64Type}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createVariantInt64TypeAdapter ()
    {
        if ( variantInt64TypeItemProvider == null )
        {
            variantInt64TypeItemProvider = new VariantInt64TypeItemProvider ( this );
        }

        return variantInt64TypeItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.scada.common.VariantNullType} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected VariantNullTypeItemProvider variantNullTypeItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.scada.common.VariantNullType}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createVariantNullTypeAdapter ()
    {
        if ( variantNullTypeItemProvider == null )
        {
            variantNullTypeItemProvider = new VariantNullTypeItemProvider ( this );
        }

        return variantNullTypeItemProvider;
    }

    /**
     * This keeps track of the one adapter used for all {@link org.eclipse.scada.common.VariantType} instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected VariantTypeItemProvider variantTypeItemProvider;

    /**
     * This creates an adapter for a {@link org.eclipse.scada.common.VariantType}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter createVariantTypeAdapter ()
    {
        if ( variantTypeItemProvider == null )
        {
            variantTypeItemProvider = new VariantTypeItemProvider ( this );
        }

        return variantTypeItemProvider;
    }

    /**
     * This returns the root adapter factory that contains this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ComposeableAdapterFactory getRootAdapterFactory ()
    {
        return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory ();
    }

    /**
     * This sets the composed adapter factory that contains this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setParentAdapterFactory ( ComposedAdapterFactory parentAdapterFactory )
    {
        this.parentAdapterFactory = parentAdapterFactory;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean isFactoryForType ( Object type )
    {
        return supportedTypes.contains ( type ) || super.isFactoryForType ( type );
    }

    /**
     * This implementation substitutes the factory itself as the key for the adapter.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Adapter adapt ( Notifier notifier, Object type )
    {
        return super.adapt ( notifier, this );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object adapt ( Object object, Object type )
    {
        if ( isFactoryForType ( type ) )
        {
            Object adapter = super.adapt ( object, type );
            if ( ! ( type instanceof Class<?> ) || ( ( (Class<?>)type ).isInstance ( adapter ) ) )
            {
                return adapter;
            }
        }

        return null;
    }

    /**
     * This adds a listener.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void addListener ( INotifyChangedListener notifyChangedListener )
    {
        changeNotifier.addListener ( notifyChangedListener );
    }

    /**
     * This removes a listener.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void removeListener ( INotifyChangedListener notifyChangedListener )
    {
        changeNotifier.removeListener ( notifyChangedListener );
    }

    /**
     * This delegates to {@link #changeNotifier} and to {@link #parentAdapterFactory}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void fireNotifyChanged ( Notification notification )
    {
        changeNotifier.fireNotifyChanged ( notification );

        if ( parentAdapterFactory != null )
        {
            parentAdapterFactory.fireNotifyChanged ( notification );
        }
    }

    /**
     * This disposes all of the item providers created by this factory. 
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void dispose ()
    {
        if ( attributesTypeItemProvider != null )
            attributesTypeItemProvider.dispose ();
        if ( attributeTypeItemProvider != null )
            attributeTypeItemProvider.dispose ();
        if ( variantBooleanTypeItemProvider != null )
            variantBooleanTypeItemProvider.dispose ();
        if ( variantDoubleTypeItemProvider != null )
            variantDoubleTypeItemProvider.dispose ();
        if ( variantInt32TypeItemProvider != null )
            variantInt32TypeItemProvider.dispose ();
        if ( variantInt64TypeItemProvider != null )
            variantInt64TypeItemProvider.dispose ();
        if ( variantNullTypeItemProvider != null )
            variantNullTypeItemProvider.dispose ();
        if ( variantTypeItemProvider != null )
            variantTypeItemProvider.dispose ();
    }

}