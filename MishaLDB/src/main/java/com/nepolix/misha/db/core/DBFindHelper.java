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
import com.nepolix.misha.android.sdk.commons.Base64;
import com.nepolix.misha.android.sdk.commons.Utils;
import com.nepolix.misha.android.sdk.json.JSONException;
import com.nepolix.misha.android.sdk.json.JSONObject;
import com.nepolix.misha.android.sdk.json.serialization.MJSON;
import com.nepolix.misha.db.common.DBCommons;
import com.nepolix.misha.db.common.sql.SQLStatementBuilder;
import com.nepolix.misha.db.entry.FieldEntry;
import com.nepolix.misha.db.entry.ObjectEntry;
import com.nepolix.misha.db.exception.MishaSQLFormatException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.nepolix.misha.db.core.MishaLDB.db;


/**
 * Created by ubuntu on 2/17/17.
 */

class DBFindHelper
{
	 
	 private final static DBFindHelper DB_FIND_HELPER = new DBFindHelper ( );
	 
	 public static
	 DBFindHelper getInstance ( )
	 {
			
			return DB_FIND_HELPER;
	 }
	 
	 private
	 DBFindHelper ( )
	 {
			
	 }
	 
	 /**
		* @param collectionName
		* @param mid
		*
		* @return can return null
		*/
	 
	 JSONObject getObject ( String collectionName,
													String mid )
	 {
			
			String x = mid.startsWith ( "'" ) && mid.endsWith ( "'" ) ? mid : "'" + mid + "'";
			String sqlStatement = "SELECT * FROM " + ObjectEntry.TABLE_NAME + " WHERE " + ObjectEntry.COLUMN_NAME_MID + " " + "= " + x + " AND "
														+ ObjectEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName + "'";
			Cursor     cursor     = db.rawQuery ( sqlStatement, null );
			JSONObject jsonObject = null;
			while ( cursor.moveToNext ( ) )
			{
				 try
				 {
						String data = cursor.getString ( cursor.getColumnIndex ( ObjectEntry.COLUMN_NAME_JSON ) );
						if ( data != null )
						{
							 data = decompressDataFromFile ( data );
							 data = new String ( Base64.decodeBase64 ( data ) );
							 jsonObject = new JSONObject ( data );
						}
				 }
				 catch ( Exception e )
				 {
						e.printStackTrace ( );
				 }
			}
			cursor.close ( );
			return jsonObject;
	 }
	 
	 private static
	 String decompressDataFromFile ( String data )
	 {
			
			String vs = data;
			if ( data.startsWith ( LDBConstants.SUFFIX_DB_FILES ) )
			{
				 String fileName = data;
				 try
				 {
						InputStream           is        = MishaLDB.context.openFileInput ( fileName );
						ByteArrayOutputStream bos       = new ByteArrayOutputStream ( );
						byte[]                b         = new byte[ 1024 ];
						int                   bytesRead = 0;
						while ( ( bytesRead = is.read ( b ) ) != -1 )
						{
							 bos.write ( b, 0, bytesRead );
						}
						byte[] bytes = bos.toByteArray ( );
						vs = new String ( bytes );
						bos.close ( );
						is.close ( );
				 }
				 catch ( IOException e )
				 {
						e.printStackTrace ( );
				 }
			}
			return vs;
	 }
	 
	 Set< String > getMIDs ( JSONObject condition,
													 int limit,
													 int offset,
													 String collectionName )
	 {
			
			Set< String > mIds       = new HashSet<> ( );
			boolean       firstQuery = true;
			if ( condition != null )
			{
				 for ( String fc : condition.keys ( ) )
				 {
						JSONObject fcCondition        = condition.optJSONObject ( fc );
						JSONObject conditionStatement = DBCommons.parseGeneralConditionToSQLCondition ( fc, fcCondition );
						JSONObject sqlStatementJSON   = new JSONObject ( );
						try
						{
							 sqlStatementJSON.put ( "CE", conditionStatement );
							 sqlStatementJSON.put ( "type", "FIND" );
							 if ( limit >= 0 ) sqlStatementJSON.put ( "LIMIT", limit );
							 if ( offset >= 0 ) sqlStatementJSON.put ( "OFFSET", offset );
							 String sqlStatement = SQLStatementBuilder.buildSQLStatement ( sqlStatementJSON, FieldEntry.TABLE_NAME, collectionName );
							 Cursor        cursor = db.rawQuery ( sqlStatement, null );
							 Set< String > set    = new HashSet<> ( );
							 while ( cursor.moveToNext ( ) )
							 {
									String mid = cursor.getString ( cursor.getColumnIndex ( FieldEntry.COLUMN_NAME_MID ) );
									set.add ( mid );
							 }
							 if ( firstQuery ) mIds.addAll ( set );
							 else mIds = Utils.findCommonSet ( mIds, set );
							 firstQuery = false;
							 cursor.close ( );
						}
						catch ( JSONException | MishaSQLFormatException e )
						{
							 e.printStackTrace ( );
						}
				 }
			}
			else
			{
				 String sqlStatement = "SELECT * FROM " + FieldEntry.TABLE_NAME + " GROUP BY " + FieldEntry.COLUMN_NAME_MID;
				 Cursor cursor       = db.rawQuery ( sqlStatement, null );
				 while ( cursor.moveToNext ( ) )
				 {
						String mid = cursor.getString ( cursor.getColumnIndex ( FieldEntry.COLUMN_NAME_MID ) );
						mIds.add ( mid );
				 }
				 cursor.close ( );
			}
			return mIds;
	 }
	 
	 List< JSONObject > getAllObjects ( String collectionName )
	 {
			
			Cursor cursor = db.rawQuery (
							"SELECT * FROM " + ObjectEntry.TABLE_NAME + " WHERE " + ObjectEntry.COLUMN_NAME_COLLECTION_NAME + "= '" + collectionName
							+ "'", null );
			List< JSONObject > objects = new ArrayList<> ( );
			while ( cursor.moveToNext ( ) )
			{
				 String data = cursor.getString ( cursor.getColumnIndex ( ObjectEntry.COLUMN_NAME_JSON ) );
				 if ( data != null )
				 {
						data = decompressDataFromFile ( data );
						data = new String ( Base64.decodeBase64 ( data ) );
						objects.add ( MJSON.toJSON ( data ) );
				 }
			}
			cursor.close ( );
			return objects;
	 }
	 
	 int count ( JSONObject condition )
					 throws
					 MishaSQLFormatException
	 {
			
			String sqlStatement;
			if ( condition != null )
			{
				 String sqlStatementString = SQLStatementBuilder.buildSQLConditionStatement ( condition );
				 sqlStatement = "SELECT COUNT(*) FROM " + FieldEntry.TABLE_NAME + " " + ( sqlStatementString == null ? "" : sqlStatementString )
												+ " GROUP BY " + FieldEntry.COLUMN_NAME_MID;
				 
			}
			else
			{
				 sqlStatement = "SELECT COUNT(*) FROM " + FieldEntry.TABLE_NAME + " GROUP BY " + FieldEntry.COLUMN_NAME_MID;
			}
			Cursor cursor = db.rawQuery ( sqlStatement, null );
			int    count  = 0;
			while ( cursor.moveToNext ( ) ) count += cursor.getInt ( 1 );
			cursor.close ( );
			return count;
	 }
}
