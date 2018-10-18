package com.cvtv.app;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map.Entry;

public class Person implements BeanInfo
{
	private static final long				serialVersionUID	= -6434718827972747817L;
	private final HashMap< String, String >	values				= new HashMap<>();

	public Person()
	{
		super();

	}

	public void setValue( String strType, String strValue )
	{
		values.put( strType, strValue );
	}

	public Object getValue( String strType )
	{
		return values.get( strType );
	}

	public Object getValue()
	{
		return "";
	}

	@Override
	public BeanDescriptor getBeanDescriptor()
	{
		return new BeanDescriptor( getClass() );
	}

	@Override
	public EventSetDescriptor[] getEventSetDescriptors()
	{
		return new EventSetDescriptor[ 0 ];
	}

	@Override
	public int getDefaultEventIndex()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors()
	{
		System.out.println( "getPropertyDescriptors" );
		final PropertyDescriptor[] list = new PropertyDescriptor[ MainView.headerRowMap.size() ];

		int iCount = 0;

		for ( final Entry< Integer, String > entry : MainView.headerRowMap.entrySet() )
		{
			entry.getKey();
			final String value = entry.getValue();

			try
			{
				final PropertyDescriptor propertyDescriptor = new PropertyDescriptor( value, getClass(), "getValue", null );

				propertyDescriptor.setName( value );
				propertyDescriptor.setValue( value, value );

				list[ iCount ] = new PropertyDescriptor( value, getClass(), "getValue", null );
			}
			catch ( final Exception e )
			{
				e.printStackTrace();
			}
			iCount++;
		}

		return list;
	}

	@Override
	public int getDefaultPropertyIndex()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public MethodDescriptor[] getMethodDescriptors()
	{
		return new MethodDescriptor[ 0 ];
	}

	@Override
	public BeanInfo[] getAdditionalBeanInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getIcon( int iconKind )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
