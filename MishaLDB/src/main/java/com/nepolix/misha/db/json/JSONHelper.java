/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights
 * reserved. * * The source
 * code, other & all material, and documentation               * contained herein are, and
 * remains the property of HEX
 * Inc.             * and its suppliers, if any. The intellectual and technical * concepts
 * contained herein are
 * proprietary to NOX Inc. and its          * suppliers and may be covered by U.S. and Foreign
 * Patents, patents      *
 * in process, and are protected by trade secret or copyright law.        * Dissemination of the
 * foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.nepolix.misha.db.json;

import com.nepolix.misha.android.sdk.json.JSONArray;
import com.nepolix.misha.android.sdk.json.JSONException;
import com.nepolix.misha.android.sdk.json.JSONObject;
import com.nepolix.misha.db.common.DBCommons;
import com.nepolix.misha.db.exception.DBException;

import java.util.HashMap;
import java.util.List;

import static com.nepolix.misha.db.common.DBCommons.*;

/**
 * @author Behrooz Shahriari
 * @since 1/5/17
 */
public
class JSONHelper
{
	 
	 public static
	 FlattenJSONObject flattenObjectFields ( JSONObject object )
	 {
			
			FlattenJSONObject flattenJSONObject = new FlattenJSONObject ( );
			try
			{
				 FlattenJSONObject.flattenFields ( flattenJSONObject, object, "" );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
			}
			return flattenJSONObject;
	 }
	 
	 /**
		* keeps the original json untouched
		*
		* @param jsonObject
		* @param previousFieldNChainName
		*
		* @return
		*/
	 public static
	 JSONObject deleteFieldChain ( JSONObject jsonObject,
																 String previousFieldNChainName )
	 {
			
			String     fields[] = previousFieldNChainName.split ( "\\." );
			JSONObject object   = jsonObject.clone ( );
			deleteOneFieldInChain$JSONObject ( object, fields, 0 );
			return object;
	 }
	 
	 private static
	 void deleteOneFieldInChain$JSONObject ( JSONObject jsonObject,
																					 String fieldChainArray[],
																					 int idxField )
	 {
			
			List< String > keyFields = jsonObject.keys ( );
			for ( String field : keyFields )
			{
				 if ( field.equals ( fieldChainArray[ idxField ] ) )
				 {
						Object object = jsonObject.opt ( field );
						if ( object != null )
						{
							 if ( object.getClass ( )
													.equals ( JSONObject.class ) )
							 {
									deleteOneFieldInChain$JSONObject ( ( JSONObject ) object, fieldChainArray,
																										 idxField + 1 );
							 }
							 else
							 {
									if ( object.getClass ( )
														 .equals ( JSONArray.class ) )
									{
										 deleteOneFieldInChain$JSONArray ( ( JSONArray ) object, fieldChainArray,
																											 idxField );
									}
									else
									{
										 jsonObject.remove ( field );
									}
							 }
						}
				 }
			}
	 }
	 
	 private static
	 void deleteOneFieldInChain$JSONArray ( JSONArray jsonArray,
																					String fieldChainArray[],
																					int idxField )
	 {
			
			for ( int i = 0 ; i < jsonArray.length ( ) ; ++i )
			{
				 Object o = jsonArray.opt ( i );
				 if ( o != null )
				 {
						if ( o.getClass ( )
									.equals ( JSONObject.class ) )
						{
							 deleteOneFieldInChain$JSONObject ( ( JSONObject ) o, fieldChainArray, idxField +
																																										 1 );
						}
						else
						{
							 if ( o.getClass ( )
										 .equals ( JSONArray.class ) )
							 {
									//WE DO NOT SUPPORT
							 }
							 else
							 {
									jsonArray.remove ( i );
									--i;
							 }
						}
				 }
			}
	 }
	 
	 public static
	 JSONObject updateFieldChainValue ( JSONObject jsonObject,
																			String previousFieldNChainName,
																			String newFieldType,
																			HashMap< String, String > mapValue )
					 throws
					 DBException
	 {
			
			String fields[] = previousFieldNChainName.split ( "\\." );
			updateFieldValues ( jsonObject, fields, 0, newFieldType, mapValue );
			return jsonObject;
	 }
	 
	 public static
	 JSONObject updateFieldChainName ( JSONObject jsonObject,
																		 String previousFieldNChainName,
																		 String newFieldChain )
					 throws
					 DBException
	 {
			if ( newFieldChain == null || newFieldChain.isEmpty ( ) )
				 return deleteFieldChain ( jsonObject, previousFieldNChainName );
			String fields[]    = previousFieldNChainName.split ( "\\." );
			String newFields[] = newFieldChain.split ( "\\." );
			if ( newFields.length != fields.length )
			{
				 throw new DBException ( "new field chain does not have " + "the same elements as old "
																 + "field chain to rename them" );
			}
			updateFieldChainName ( jsonObject, fields, newFields, 0 );
			return jsonObject;
	 }
	 
	 private static
	 void updateFieldChainName ( JSONObject object,
															 String[] fieldChainArray,
															 String[] newFieldChainArray,
															 int idxField )
	 {
			
			String fieldName    = fieldChainArray[ idxField ];
			String newFieldName = newFieldChainArray[ idxField ];
			Object o            = object.opt ( fieldName );
			if ( object.has ( fieldName ) && o != null )
			{
				 object.putOpt ( newFieldName, o );
				 if ( o.getClass ( )
							 .equals ( JSONObject.class ) )
				 {
						updateFieldChainName ( ( JSONObject ) o, fieldChainArray, newFieldChainArray,
																	 idxField + 1 );
				 }
				 else
				 {
						if ( o.getClass ( )
									.equals ( JSONArray.class ) )
						{
							 JSONArray array = ( JSONArray ) o;
							 for ( int i = 0 ; i < array.length ( ) ; ++i )
							 {
									Object o1 = array.opt ( i );
									if ( o1 != null && o1.getClass ( )
																			 .equals ( JSONObject.class ) )
									{
										 updateFieldChainName ( ( JSONObject ) o1, fieldChainArray, newFieldChainArray,
																						idxField + 1 );
									}
							 }
						}
				 }
			}
	 }
	 
	 private static
	 void updateFieldValues ( JSONObject object,
														String fieldChainArray[],
														int idxField,
														String newFieldType,
														HashMap< String, String > mapValue )
					 throws
					 DBException
	 {
			
			String fieldName = fieldChainArray[ idxField ];
			if ( idxField == fieldChainArray.length - 1 )
			{
				 Object dbRawValue = object.opt ( fieldName );
				 if ( dbRawValue.getClass ( )
												.equals ( JSONObject.class ) )
				 {
						//WE DO NOT SUPPORT
						System.err.println ( "we do not support update of field chain for json field, you "
																 + "need to update the inner fields" );
						throw new DBException ( "we do not support update of field chain for json field, you "
																		+ "need to update the inner fields" );
				 }
				 else
				 {
						if ( dbRawValue.getClass ( )
													 .equals ( JSONArray.class ) )
						{
							 JSONArray array = ( JSONArray ) dbRawValue;
							 for ( int i = 0 ; i < array.length ( ) ; ++i )
							 {
									Object o = array.opt ( i );
									if ( o == null )
									{
										 array.remove ( i );
										 --i;
									}
									else
									{
										 if ( o.getClass ( )
													 .equals ( JSONObject.class ) )
										 {
												throw new DBException ( "we do not support update of field chain of "
																								+ "json inside array" );
										 }
										 else
										 {
												if ( o.getClass ( )
															.equals ( JSONArray.class ) )
												{
													 throw new DBException ( "we do not support update of field chain of "
																									 + "array inside array" );
												}
												else
												{
													 array.putOpt ( i, updateObject ( o, mapValue, newFieldType ) );
												}
										 }
									}
							 }
						}
						else
						{
							 object.putOpt ( fieldName, updateObject ( dbRawValue, mapValue, newFieldType ) );
						}
				 }
			}
			else
			{
				 Object o = object.opt ( fieldName );
				 if ( o != null )
				 {
						if ( o.getClass ( )
									.equals ( JSONObject.class ) )
						{
							 updateFieldValues ( ( JSONObject ) o, fieldChainArray, idxField + 1, newFieldType,
																	 mapValue );
						}
						else
						{
							 if ( o.getClass ( )
										 .equals ( JSONArray.class ) )
							 {
									JSONArray array = ( JSONArray ) o;
									for ( int i = 0 ; i < array.length ( ) ; ++i )
									{
										 Object o1 = array.opt ( i );
										 if ( o1 != null )
										 {
												if ( o1.getClass ( )
															 .equals ( JSONObject.class ) )
												{
													 updateFieldValues ( ( JSONObject ) o1, fieldChainArray, idxField + 1,
																							 newFieldType, mapValue );
												}
												else
												{
													 throw new DBException ( "we do not support array inside array" );
												}
										 }
									}
							 }
							 else
							 {
									//DO NOTHING GO UP...
							 }
						}
				 }
			}
	 }
	 
	 private static
	 Object updateObject ( Object data,
												 HashMap< String, String > mapValue,
												 String newFieldType )
	 {
			
			String dbValue  = convertObjectValueToString ( data );
			Object result   = null;
			String newValue = mapValue.get ( dbValue );
			if ( newValue == null ) newValue = dbValue;
			if ( newValue.equals ( MIGRATION_DELETION_VALUE ) || dbValue == null ) newValue = null;
			if ( newFieldType.equals ( FIELD_TYPE_INTEGER ) )
			{
				 if ( newValue == null ) newValue = "0";
				 result = Integer.parseInt ( newValue );
			}
			if ( newFieldType.equals ( FIELD_TYPE_LONG ) )
			{
				 if ( newValue == null ) newValue = "0";
				 result = Long.parseLong ( newValue );
			}
			if ( newFieldType.equals ( FIELD_TYPE_DOUBLE ) )
			{
				 if ( newValue == null ) newValue = "0.0";
				 result = Double.parseDouble ( newValue );
			}
			if ( newFieldType.equals ( FIELD_TYPE_STRING ) )
			{
				 if ( newValue == null ) newValue = "";
				 result = newValue;
			}
			if ( newFieldType.equals ( FIELD_TYPE_BOOLEAN ) )
			{
				 if ( newValue == null ) newValue = "false";
				 result = Boolean.parseBoolean ( newValue );
			}
			return result;
	 }
	 
	 public static
	 JSONObject updateFieldChainValueName ( JSONObject jsonObject,
																					String previousFieldNChainName,
																					String newFieldChain,
																					String newFieldType,
																					HashMap< String, String > mapValue )
					 throws
					 DBException
	 {
			
			JSONObject object = jsonObject.clone ( );
			object = updateFieldChainValue ( object, previousFieldNChainName, newFieldType, mapValue );
			object = updateFieldChainName ( object, previousFieldNChainName, newFieldChain );
			return object;
	 }
	 
	 private static
	 String convertObjectValueToString ( Object value )
	 {
			
			String valueString = null;
			if ( value != null )
			{
				 try
				 {
						String valueType = DBCommons.getDBFieldType ( value );
						if ( valueType.equals ( FIELD_TYPE_LONG ) || valueType.equals ( FIELD_TYPE_INTEGER ) )
						{
							 Long aLong = ( Long ) value;
							 valueString = aLong.toString ( );
						}
						if ( valueType.equals ( FIELD_TYPE_DOUBLE ) )
						{
							 Double aDouble = ( Double ) value;
							 valueString = aDouble.toString ( );
						}
						if ( valueType.equals ( FIELD_TYPE_BOOLEAN ) )
						{
							 Boolean aBoolean = ( Boolean ) value;
							 valueString = aBoolean.toString ( );
						}
						if ( valueType.equals ( FIELD_TYPE_STRING ) )
						{
							 valueString = value.toString ( );
						}
				 }
				 catch ( Exception ignored )
				 {
				 }
			}
			return valueString;
	 }
}
