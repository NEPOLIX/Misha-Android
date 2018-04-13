/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights reserved.
 * * * The source code, other & all material, and documentation               * contained herein
 * are, and remains the property of HEX Inc. * and its suppliers, if any. The intellectual and
 * technical              * concepts contained herein are proprietary to NOX Inc. and its          *
 * suppliers and may be covered by U.S. and Foreign Patents, patents      * in process, and are
 * protected by trade secret or copyright law. * Dissemination of the foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.nepolix.misha.db.core;

import android.database.Cursor;
import android.provider.BaseColumns;
import com.nepolix.misha.android.sdk.json.JSONArray;
import com.nepolix.misha.android.sdk.json.JSONObject;
import com.nepolix.misha.db.common.DBCommons;
import com.nepolix.misha.db.entry.FieldEntry;
import com.nepolix.misha.db.entry.ObjectEntry;
import com.nepolix.misha.db.exception.DBException;
import com.nepolix.misha.db.json.FlattenJSONObject;
import com.nepolix.misha.db.json.JSONHelper;

import java.util.HashMap;
import java.util.List;

import static com.nepolix.misha.db.common.DBCommons.*;
import static com.nepolix.misha.db.core.MishaLDB.db;

/**
 * @author Behrooz Shahriari
 * @since 2/28/17
 */

class DBHelperMigrate
{
	 
	 private final static DBHelperMigrate MIGRATE_FIELD = new DBHelperMigrate ( );
	 
	 private
	 DBHelperMigrate ( )
	 {
			
	 }
	 
	 public static
	 DBHelperMigrate getInstance ( )
	 {
			
			return MIGRATE_FIELD;
	 }
	 
	 void migrate$DELETE ( String collectionName,
												 String previousFieldName )
	 {
			
			String sqlStatement = "DELETE FROM " + FieldEntry.TABLE_NAME + " WHERE "
														+ FieldEntry.COLUMN_NAME_FIELD_CHAIN + " = '" + previousFieldName
														+ "' AND " + FieldEntry.COLUMN_NAME_COLLECTION_NAME + " = '"
														+ collectionName + "'";
			db.rawQuery ( sqlStatement, null )
				.close ( );
			sqlStatement = "SELECT * FROM " + ObjectEntry.TABLE_NAME + " WHERE "
										 + ObjectEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName + "'";
			Cursor cursor = db.rawQuery ( sqlStatement, null );
			while ( cursor.moveToNext ( ) )
			{
				 String mid = cursor.getString ( cursor.getColumnIndex ( ObjectEntry.COLUMN_NAME_MID ) );
				 String json = cursor.getString ( cursor.getColumnIndex ( ObjectEntry.COLUMN_NAME_JSON ) );
				 JSONObject        jsonObject        = JSONObject.toJSON ( json );
				 FlattenJSONObject flattenJSONObject = JSONHelper.flattenObjectFields ( jsonObject );
				 List< String >    fieldChains       = flattenJSONObject.$getFieldChains ( );
				 for ( String fc : fieldChains )
				 {
						if ( fc.equals ( previousFieldName ) )
						{
							 flattenJSONObject.getMap ( )
																.remove ( fc );
						}
				 }
				 JSONObject newJsonObject = JSONHelper.deleteFieldChain ( jsonObject, previousFieldName );
				 sqlStatement = "UPDATE " + ObjectEntry.TABLE_NAME + " SET " + ObjectEntry.COLUMN_NAME_JSON
												+ " = '" + newJsonObject.toString ( ) + "' WHERE "
												+ ObjectEntry.COLUMN_NAME_MID + " = '" + mid + "'";
				 db.rawQuery ( sqlStatement, null )
					 .close ( );
			}
			cursor.close ( );
	 }
	 
	 
	 void migrate$UPDATE ( String collectionName,
												 String previousFieldName,
												 JSONObject fieldMigrationData )
					 throws
					 DBException
	 {
			
			String newFieldChain = fieldMigrationData.optString ( "name" );
			String newFieldType = fieldMigrationData.optString ( "type" )
																							.toUpperCase ( );
			JSONArray                 array    = fieldMigrationData.optJSONArray ( "map" );
			HashMap< String, String > mapValue = new HashMap<> ( );
			if ( array != null )
			{
				 for ( int i = 0 ; i < array.length ( ) ; ++i )
				 {
						JSONObject iObject = array.optJSONObject ( i );
						if ( iObject != null )
						{
							 String pv = iObject.optString ( "previous_value" );
							 String nv = iObject.optString ( "value" );
							 if ( pv.equalsIgnoreCase ( "null" ) ) mapValue.put ( null, nv );
							 else mapValue.put ( pv, nv );
						}
				 }
			}
			migrateField$UPDATE ( collectionName, previousFieldName, newFieldType, newFieldChain,
														mapValue );
			migrateObject$UPDATE ( collectionName, previousFieldName, newFieldType, newFieldChain,
														 mapValue );
	 }
	 
	 private
	 void migrateField$UPDATE ( String collectionName,
															String previousFieldName,
															String newFieldType,
															String newFieldChain,
															HashMap< String, String > mapValue )
	 {
			
			String sqlStatement = "SELECT * FROM " + FieldEntry.TABLE_NAME + " WHERE "
														+ FieldEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName
														+ "' AND " + FieldEntry.COLUMN_NAME_FIELD_CHAIN + " = '"
														+ previousFieldName + "'";
			Cursor cursor = db.rawQuery ( sqlStatement, null );
			while ( cursor.moveToNext ( ) )
			{
				 long _id = cursor.getLong ( cursor.getColumnIndex ( BaseColumns._ID ) );
				 String cFieldType = cursor.getString (
								 cursor.getColumnIndex ( FieldEntry.COLUMN_NAME_FIELD_TYPE ) );
				 String dbValue  = getFieldValueAsString ( cursor );
				 String newValue = mapValue.get ( dbValue );
				 //XXX NEED DEEP REVIEW
				 if ( newValue == null ) newValue = dbValue;
				 if ( newValue.equals ( MIGRATION_DELETION_VALUE ) || dbValue == null )
				 {
						//DELETE
						sqlStatement = "DELETE FROM " + FieldEntry.TABLE_NAME + " WHERE " + BaseColumns._ID
													 + " = " + _id;
						db.rawQuery ( sqlStatement, null )
							.close ( );
				 }
				 else
				 {
						//UPDATE
						updateField ( cFieldType, newFieldType, newValue, newFieldChain, _id );
				 }
			}
			cursor.close ( );
	 }
	 
	 private
	 void migrateObject$UPDATE ( String collectionName,
															 String previousFieldNChainName,
															 String newFieldType,
															 String newFieldChain,
															 HashMap< String, String > mapValue )
					 throws
					 DBException
	 {
			
			String sqlStatement = "SELECT * FROM " + ObjectEntry.TABLE_NAME + " WHERE "
														+ ObjectEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName
														+ "'";
			Cursor cursor = db.rawQuery ( sqlStatement, null );
			while ( cursor.moveToNext ( ) )
			{
				 long _id = cursor.getLong ( cursor.getColumnIndex ( BaseColumns._ID ) );
				 String json = cursor.getString ( cursor.getColumnIndex ( ObjectEntry.COLUMN_NAME_JSON ) );
				 String mid = cursor.getString ( cursor.getColumnIndex ( ObjectEntry.COLUMN_NAME_MID ) );
				 JSONObject        jsonObject        = JSONObject.toJSON ( json );
				 jsonObject = JSONHelper.updateFieldChainValueName ( jsonObject, previousFieldNChainName,
																														 newFieldChain, newFieldType,
																														 mapValue );
				 json = jsonObject.toString ( );
				 sqlStatement = "UPDATE " + ObjectEntry.TABLE_NAME + " SET " + ObjectEntry.COLUMN_NAME_JSON
												+ "'" + json + "'" + " WHERE " + BaseColumns._ID + " = " + _id;
				 db.rawQuery ( sqlStatement, null )
					 .close ( );
			}
			cursor.close ( );
	 }
	 
	 private
	 String getFieldValueAsString ( Cursor cursor )
	 {
			
			String fieldType = cursor.getString (
							cursor.getColumnIndex ( FieldEntry.COLUMN_NAME_FIELD_TYPE ) );
			String value = null;
			try
			{
				 if ( fieldType.equals ( FIELD_TYPE_INTEGER ) || fieldType.equals ( FIELD_TYPE_LONG ) )
				 {
						value = "" + cursor.getLong (
										cursor.getColumnIndex ( FieldEntry.COLUMN_NAME_VALUE_INT ) );
				 }
				 if ( fieldType.equals ( FIELD_TYPE_BOOLEAN ) )
				 {
						int v = cursor.getInt ( cursor.getColumnIndex ( FieldEntry.COLUMN_NAME_VALUE_INT ) );
						value = "" + ( v == 1 ? "true" : "false" );
				 }
				 if ( fieldType.equals ( DBCommons.FIELD_TYPE_DOUBLE ) )
				 {
						value = "" + cursor.getDouble (
										cursor.getColumnIndex ( FieldEntry.COLUMN_NAME_VALUE_DOUBLE ) );
				 }
				 if ( fieldType.equals ( DBCommons.FIELD_TYPE_STRING ) )
				 {
						value = "" + cursor.getString ( cursor.getColumnIndex ( DBCommons.FIELD_TYPE_STRING
						) );
						if ( value.equals ( "null" ) ) value = null;
				 }
			}
			catch ( Exception ignored )
			{
			}
			return value;
	 }
	 
	 private
	 void updateField ( String previousFieldType,
											String newFieldType,
											String newValue,
											String newFieldChain,
											long _id )
	 {
			
			String valueSQLStatement = "";
			if ( newFieldType.equals ( FIELD_TYPE_INTEGER ) || newFieldType.equals ( FIELD_TYPE_LONG ) )
			{
				 valueSQLStatement = FieldEntry.COLUMN_NAME_VALUE_INT + " = " + Long.parseLong (
				 				newValue );
			}
			if ( newFieldType.equals ( FIELD_TYPE_DOUBLE ) )
			{
				 valueSQLStatement = FieldEntry.COLUMN_NAME_VALUE_DOUBLE + " = " + Double.parseDouble (
								 newValue );
			}
			if ( newFieldType.equals ( FIELD_TYPE_BOOLEAN ) )
			{
				 newValue = newValue.toLowerCase ( )
														.trim ( );
				 valueSQLStatement = FieldEntry.COLUMN_NAME_VALUE_INT + " = " + ( Boolean.parseBoolean (
								 newValue ) ? 1 : 0 );
			}
			if ( newFieldType.equals ( FIELD_TYPE_STRING ) )
			{
				 valueSQLStatement = FieldEntry.COLUMN_NAME_VALUE_STRING + " = '" + newValue + "'";
			}
			valueSQLStatement += ", ";
			if ( previousFieldType.equals ( FIELD_TYPE_LONG ) || previousFieldType.equals (
							FIELD_TYPE_INTEGER ) || previousFieldType.equals ( FIELD_TYPE_BOOLEAN ) )
			{
				 valueSQLStatement += FieldEntry.COLUMN_NAME_VALUE_INT + " = NULL";
			}
			if ( previousFieldType.equals ( FIELD_TYPE_DOUBLE ) )
			{
				 valueSQLStatement += FieldEntry.COLUMN_NAME_VALUE_DOUBLE + " = NULL";
			}
			if ( previousFieldType.equals ( FIELD_TYPE_STRING ) )
			{
				 valueSQLStatement += FieldEntry.COLUMN_NAME_VALUE_STRING + " = NULL";
			}
			String sqlStatement = "UPDATE " + FieldEntry.TABLE_NAME + " SET "
														+ FieldEntry.COLUMN_NAME_FIELD_CHAIN + " = '" + newFieldChain + "', "
														+ "" + FieldEntry.COLUMN_NAME_FIELD_TYPE + " = '" + newFieldType + "',"
														+ " " + valueSQLStatement + " WHERE " + BaseColumns._ID + " = " + _id;
			db.rawQuery ( sqlStatement, null )
				.close ( );
	 }
	 
	
}
