/*******************************************************************************
 * Copyright (c) 2013 Jens Reimann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jens Reimann - initial API and implementation
 *******************************************************************************/

package org.eclipse.scada.da.modbus.configuration.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.xml.type.XMLTypeFactory;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.scada.da.modbus.configuration.ConfigurationFactory;
import org.eclipse.scada.da.modbus.configuration.ConfigurationPackage;
import org.eclipse.scada.da.modbus.configuration.DeviceType;
import org.eclipse.scada.da.modbus.configuration.DevicesType;
import org.eclipse.scada.da.modbus.configuration.DocumentRoot;
import org.eclipse.scada.da.modbus.configuration.ItemType;
import org.eclipse.scada.da.modbus.configuration.ModbusSlave;
import org.eclipse.scada.da.modbus.configuration.ProtocolType;
import org.eclipse.scada.da.modbus.configuration.RootType;
import org.eclipse.scada.da.modbus.configuration.TypeType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ConfigurationFactoryImpl extends EFactoryImpl implements ConfigurationFactory
{
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ConfigurationFactory init ()
    {
        try
        {
            ConfigurationFactory theConfigurationFactory = (ConfigurationFactory)EPackage.Registry.INSTANCE.getEFactory ( ConfigurationPackage.eNS_URI );
            if ( theConfigurationFactory != null )
            {
                return theConfigurationFactory;
            }
        }
        catch ( Exception exception )
        {
            EcorePlugin.INSTANCE.log ( exception );
        }
        return new ConfigurationFactoryImpl ();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ConfigurationFactoryImpl ()
    {
        super ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create ( EClass eClass )
    {
        switch ( eClass.getClassifierID () )
        {
            case ConfigurationPackage.DEVICES_TYPE:
                return createDevicesType ();
            case ConfigurationPackage.DEVICE_TYPE:
                return createDeviceType ();
            case ConfigurationPackage.DOCUMENT_ROOT:
                return createDocumentRoot ();
            case ConfigurationPackage.ITEM_TYPE:
                return createItemType ();
            case ConfigurationPackage.MODBUS_SLAVE:
                return createModbusSlave ();
            case ConfigurationPackage.ROOT_TYPE:
                return createRootType ();
            default:
                throw new IllegalArgumentException ( "The class '" + eClass.getName () + "' is not a valid classifier" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString ( EDataType eDataType, String initialValue )
    {
        switch ( eDataType.getClassifierID () )
        {
            case ConfigurationPackage.PROTOCOL_TYPE:
                return createProtocolTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.TYPE_TYPE:
                return createTypeTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.HOST_TYPE:
                return createHostTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.ID_TYPE:
                return createIdTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.ID_TYPE1:
                return createIdType1FromString ( eDataType, initialValue );
            case ConfigurationPackage.ID_TYPE_OBJECT:
                return createIdTypeObjectFromString ( eDataType, initialValue );
            case ConfigurationPackage.PROTOCOL_TYPE_OBJECT:
                return createProtocolTypeObjectFromString ( eDataType, initialValue );
            case ConfigurationPackage.START_ADDRESS_TYPE:
                return createStartAddressTypeFromString ( eDataType, initialValue );
            case ConfigurationPackage.TYPE_TYPE_OBJECT:
                return createTypeTypeObjectFromString ( eDataType, initialValue );
            default:
                throw new IllegalArgumentException ( "The datatype '" + eDataType.getName () + "' is not a valid classifier" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString ( EDataType eDataType, Object instanceValue )
    {
        switch ( eDataType.getClassifierID () )
        {
            case ConfigurationPackage.PROTOCOL_TYPE:
                return convertProtocolTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.TYPE_TYPE:
                return convertTypeTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.HOST_TYPE:
                return convertHostTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.ID_TYPE:
                return convertIdTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.ID_TYPE1:
                return convertIdType1ToString ( eDataType, instanceValue );
            case ConfigurationPackage.ID_TYPE_OBJECT:
                return convertIdTypeObjectToString ( eDataType, instanceValue );
            case ConfigurationPackage.PROTOCOL_TYPE_OBJECT:
                return convertProtocolTypeObjectToString ( eDataType, instanceValue );
            case ConfigurationPackage.START_ADDRESS_TYPE:
                return convertStartAddressTypeToString ( eDataType, instanceValue );
            case ConfigurationPackage.TYPE_TYPE_OBJECT:
                return convertTypeTypeObjectToString ( eDataType, instanceValue );
            default:
                throw new IllegalArgumentException ( "The datatype '" + eDataType.getName () + "' is not a valid classifier" ); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DevicesType createDevicesType ()
    {
        DevicesTypeImpl devicesType = new DevicesTypeImpl ();
        return devicesType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DeviceType createDeviceType ()
    {
        DeviceTypeImpl deviceType = new DeviceTypeImpl ();
        return deviceType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public DocumentRoot createDocumentRoot ()
    {
        DocumentRootImpl documentRoot = new DocumentRootImpl ();
        return documentRoot;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ItemType createItemType ()
    {
        ItemTypeImpl itemType = new ItemTypeImpl ();
        return itemType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ModbusSlave createModbusSlave ()
    {
        ModbusSlaveImpl modbusSlave = new ModbusSlaveImpl ();
        return modbusSlave;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public RootType createRootType ()
    {
        RootTypeImpl rootType = new RootTypeImpl ();
        return rootType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ProtocolType createProtocolTypeFromString ( EDataType eDataType, String initialValue )
    {
        ProtocolType result = ProtocolType.get ( initialValue );
        if ( result == null )
            throw new IllegalArgumentException ( "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName () + "'" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertProtocolTypeToString ( EDataType eDataType, Object instanceValue )
    {
        return instanceValue == null ? null : instanceValue.toString ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TypeType createTypeTypeFromString ( EDataType eDataType, String initialValue )
    {
        TypeType result = TypeType.get ( initialValue );
        if ( result == null )
            throw new IllegalArgumentException ( "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName () + "'" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertTypeTypeToString ( EDataType eDataType, Object instanceValue )
    {
        return instanceValue == null ? null : instanceValue.toString ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String createHostTypeFromString ( EDataType eDataType, String initialValue )
    {
        return (String)XMLTypeFactory.eINSTANCE.createFromString ( XMLTypePackage.Literals.STRING, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertHostTypeToString ( EDataType eDataType, Object instanceValue )
    {
        return XMLTypeFactory.eINSTANCE.convertToString ( XMLTypePackage.Literals.STRING, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String createIdTypeFromString ( EDataType eDataType, String initialValue )
    {
        return (String)XMLTypeFactory.eINSTANCE.createFromString ( XMLTypePackage.Literals.STRING, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertIdTypeToString ( EDataType eDataType, Object instanceValue )
    {
        return XMLTypeFactory.eINSTANCE.convertToString ( XMLTypePackage.Literals.STRING, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Integer createIdType1FromString ( EDataType eDataType, String initialValue )
    {
        return (Integer)XMLTypeFactory.eINSTANCE.createFromString ( XMLTypePackage.Literals.INT, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertIdType1ToString ( EDataType eDataType, Object instanceValue )
    {
        return XMLTypeFactory.eINSTANCE.convertToString ( XMLTypePackage.Literals.INT, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Integer createIdTypeObjectFromString ( EDataType eDataType, String initialValue )
    {
        return createIdType1FromString ( ConfigurationPackage.Literals.ID_TYPE1, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertIdTypeObjectToString ( EDataType eDataType, Object instanceValue )
    {
        return convertIdType1ToString ( ConfigurationPackage.Literals.ID_TYPE1, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ProtocolType createProtocolTypeObjectFromString ( EDataType eDataType, String initialValue )
    {
        return createProtocolTypeFromString ( ConfigurationPackage.Literals.PROTOCOL_TYPE, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertProtocolTypeObjectToString ( EDataType eDataType, Object instanceValue )
    {
        return convertProtocolTypeToString ( ConfigurationPackage.Literals.PROTOCOL_TYPE, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String createStartAddressTypeFromString ( EDataType eDataType, String initialValue )
    {
        return (String)XMLTypeFactory.eINSTANCE.createFromString ( XMLTypePackage.Literals.STRING, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertStartAddressTypeToString ( EDataType eDataType, Object instanceValue )
    {
        return XMLTypeFactory.eINSTANCE.convertToString ( XMLTypePackage.Literals.STRING, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TypeType createTypeTypeObjectFromString ( EDataType eDataType, String initialValue )
    {
        return createTypeTypeFromString ( ConfigurationPackage.Literals.TYPE_TYPE, initialValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertTypeTypeObjectToString ( EDataType eDataType, Object instanceValue )
    {
        return convertTypeTypeToString ( ConfigurationPackage.Literals.TYPE_TYPE, instanceValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ConfigurationPackage getConfigurationPackage ()
    {
        return (ConfigurationPackage)getEPackage ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ConfigurationPackage getPackage ()
    {
        return ConfigurationPackage.eINSTANCE;
    }

} //ConfigurationFactoryImpl
