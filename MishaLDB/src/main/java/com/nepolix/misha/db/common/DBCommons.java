/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights reserved.
 * * * The source code, other & all material, and documentation               * contained herein
 * are, and remains the property of HEX Inc.             * and its suppliers, if any. The
 * intellectual and technical * concepts contained herein are proprietary to NOX Inc. and its *
 * suppliers and may be covered by U.S. and Foreign Patents, patents      * in process, and are
 * protected by trade secret or copyright law.        * Dissemination of the foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.nepolix.misha.db.common;


import com.nepolix.misha.android.sdk.json.JSONException;
import com.nepolix.misha.android.sdk.json.JSONObject;
import com.nepolix.misha.db.entry.FieldEntry;
import com.nepolix.misha.db.json.FlattenJSONObject;
import com.nepolix.misha.db.json.JSONHelper;

import java.util.List;

/**
 * @author Behrooz Shahriari
 * @since 1/8/17
 */
public
class DBCommons
{
	 
	 public final static String FIELD_TYPE_STRING = "STRING";
	 
	 public final static String FIELD_TYPE_INTEGER = "INTEGER";
	 
	 public final static String FIELD_TYPE_LONG = "LONG";
	 
	 public final static String FIELD_TYPE_BOOLEAN = "BOOLEAN";
	 
	 public final static String FIELD_TYPE_DOUBLE = "DOUBLE";
	 
	 public final static String MIGRATION_DELETION_VALUE = "$X$NULL";
	 
	 
	 public static
	 String getDBFieldType ( Object value )
	 {
			
			String valueString = value.toString ( );
			try
			{
				 Integer.parseInt ( valueString );
				 if ( value.getClass ( )
									 .equals ( Integer.class ) || value.getClass ( )
																										 .equals ( int.class ) ) return FIELD_TYPE_INTEGER;
			}
			catch ( Exception ignored )
			{
			}
			try
			{
				 Long.parseLong ( valueString );
				 if ( value.getClass ( )
									 .equals ( Long.class ) || value.getClass ( )
																									.equals ( long.class ) ) return FIELD_TYPE_LONG;
			}
			catch ( Exception ignored )
			{
			}
			try
			{
				 if ( valueString.equalsIgnoreCase ( "true" ) || valueString.equalsIgnoreCase ( "false" ) ) return FIELD_TYPE_BOOLEAN;
			}
			catch ( Exception ignored )
			{
			}
			try
			{
				 Double.parseDouble ( valueString );
				 if ( value.getClass ( )
									 .equals ( Double.class ) || value.getClass ( )
																										.equals ( double.class ) ) return FIELD_TYPE_DOUBLE;
			}
			catch ( Exception ignored )
			{
			}
			return FIELD_TYPE_STRING;
	 }
	 
	 public static
	 JSONObject parseGeneralConditionToSQLCondition ( String fieldChain,
																										JSONObject fcCondition )
	 {
			
			JSONObject        sqlCondition      = new JSONObject ( );
			JSONObject        subCondition      = new JSONObject ( );
			FlattenJSONObject flattenJSONObject = JSONHelper.flattenObjectFields ( fcCondition );
			List< String >    fcs               = flattenJSONObject.$getFieldChains ( );
			Object            value             = null;
			for ( int i = 0 ; i < fcs.size ( ) && value == null ; ++i )
			{
				 List< Object > values = flattenJSONObject.$getValues ( fcs.get ( i ) );
				 for ( int j = 0 ; j < values.size ( ) ; ++j )
				 {
						if ( values.get ( j ) != null && !values.get ( i )
																										.equals ( null ) )
						{
							 value = values.get ( j );
							 String dBFieldType = getDBFieldType ( value );
							 if ( dBFieldType.equals ( FIELD_TYPE_STRING ) )
							 {
									String  x      = value.toString ( );
									boolean adjust = false;
									if ( x.startsWith ( "'" ) && x.endsWith ( "'" ) )
									{
										 adjust = true;
										 x = x.substring ( 1, x.length ( ) - 1 );
									}
									x = escapeString$QueryExecute ( x );
									if ( adjust )
									{
										 x = "'" + x + "'";
									}
									values.set ( j, x );
							 }
						}
				 }
			}
			value = flattenJSONObject.$getValues ( flattenJSONObject.$getFieldChains ( )
																															.get ( 0 ) )
															 .get ( 0 );
			String dBFieldType = getDBFieldType ( value );
			try
			{
				 subCondition.put ( "$=", "'" + escapeString$QueryExecute ( fieldChain ) + "'" );
				 sqlCondition.put ( FieldEntry.COLUMN_NAME_FIELD_CHAIN, subCondition.clone ( ) );
				 subCondition.clear ( );
				 subCondition.put ( "$=", "'" + dBFieldType + "'" );
				 sqlCondition.put ( FieldEntry.COLUMN_NAME_FIELD_TYPE, subCondition.clone ( ) );
				 subCondition.clear ( );
				 if ( dBFieldType.equals ( FIELD_TYPE_INTEGER ) || dBFieldType.equals ( FIELD_TYPE_LONG ) )
						sqlCondition.put ( FieldEntry.COLUMN_NAME_VALUE_INT, fcCondition );
				 if ( dBFieldType.equals ( FIELD_TYPE_BOOLEAN ) )
				 {
						int v;
						String operator = fcCondition.keys ( )
																				 .get ( 0 );
						v = Boolean.parseBoolean ( value.toString ( )
																						.toLowerCase ( ) ) ? 1 : 0;
						fcCondition.remove ( operator );
						fcCondition.put ( operator, v );
						sqlCondition.put ( FieldEntry.COLUMN_NAME_VALUE_INT, fcCondition );
				 }
				 if ( dBFieldType.equals ( FIELD_TYPE_DOUBLE ) ) sqlCondition.put ( FieldEntry.COLUMN_NAME_VALUE_DOUBLE, fcCondition );
				 if ( dBFieldType.equals ( FIELD_TYPE_STRING ) ) sqlCondition.put ( FieldEntry.COLUMN_NAME_VALUE_STRING, fcCondition );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
			}
			return sqlCondition;
	 }
	 
	 public static
	 String escapeString$QueryExecute ( String input )
	 {
			
			if ( input == null ) throw new NullPointerException ( );
			else
			{
				 String inputX = new String ( input );
				 inputX = inputX.replace ( "'", "''" )
												.replace ( "\"", "\\\"" )
												.replace ( "\\", "\\\\" );
				 return inputX;
			}
	 }
}
