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
import com.nepolix.misha.db.entry.FieldEntry;
import com.nepolix.misha.db.entry.ObjectEntry;
import com.nepolix.misha.db.model.MModel;

import java.util.List;

import static com.nepolix.misha.db.core.MishaLDB.db;

/**
 * Created by ubuntu on 2/16/17.
 */

class DBDeleteHelper
{
	 
	 private final static DBDeleteHelper DB_DELETE_HELPER = new DBDeleteHelper ( );
	 
	 DBDeleteHelper ( )
	 {
			
	 }
	 
	 public static
	 DBDeleteHelper getInstance ( )
	 {
			
			return DB_DELETE_HELPER;
	 }
	 
	 void deleteObjectFields ( String mid,
														 String fieldChain,
														 String collectionName )
	 {
			
			String t = "'" + mid + "'";
			if ( mid.startsWith ( "'" ) && mid.endsWith ( "'" ) ) t = mid;
			String SQLStatement = "DELETE FROM " + FieldEntry.TABLE_NAME + " WHERE " + FieldEntry.COLUMN_NAME_MID + " = " + t + " AND" + " "
														+ FieldEntry.COLUMN_NAME_FIELD_CHAIN + " = '" + fieldChain + "' AND " + FieldEntry.COLUMN_NAME_COLLECTION_NAME
														+ " = '" + collectionName + "'";
			db.execSQL ( SQLStatement );
	 }
	 
	 void deleteObjectFields ( String mid,
														 String collectionName )
	 {
			
			String t = "'" + mid + "'";
			if ( mid.startsWith ( "'" ) && mid.endsWith ( "'" ) ) t = mid;
			String sQLStatement = "DELETE FROM " + FieldEntry.TABLE_NAME + " WHERE " + FieldEntry.COLUMN_NAME_MID + " = " + t + " AND "
														+ FieldEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName + "'";
			db.execSQL ( sQLStatement );
	 }
	 
	 void deleteObject ( String mid,
											 String collectionName )
	 {
			
			String x = mid.startsWith ( "'" ) && mid.endsWith ( "'" ) ? mid : "'" + mid + "'";
			String sqlStatement = "SELECT * FROM " + ObjectEntry.TABLE_NAME + " WHERE " + ObjectEntry.COLUMN_NAME_MID + " " + "= " + x + " AND "
														+ ObjectEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName + "'";
			Cursor cursor = db.rawQuery ( sqlStatement, null );
			while ( cursor.moveToNext ( ) )
			{
				 String data = cursor.getString ( cursor.getColumnIndex ( ObjectEntry.COLUMN_NAME_JSON ) );
				 if ( data.startsWith ( LDBConstants.SUFFIX_DB_FILES ) )
				 {
						MishaLDB.context.deleteFile ( data );
				 }
			}
			cursor.close ( );
			sqlStatement = "DELETE FROM " + ObjectEntry.TABLE_NAME + " WHERE " + ObjectEntry.COLUMN_NAME_MID + " = " + x + " AND "
										 + ObjectEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName + "'";
			db.execSQL ( sqlStatement );
	 }
	 
	 
	 void clearDB ( )
	 {
			
			for ( int i = 0 ; i < 10 ; ++i )
				 System.out.println ( "clearDB" );
			String sqlStatement = "DELETE FROM " + ObjectEntry.TABLE_NAME + " WHERE " + ObjectEntry._ID + " > -1";
			db.execSQL ( sqlStatement );
			sqlStatement = "DELETE FROM " + FieldEntry.TABLE_NAME + " WHERE " + FieldEntry._ID + " > -1";
			db.execSQL ( sqlStatement );
			String[] fileNames = MishaLDB.context.fileList ( );
			for ( String fn : fileNames )
			{
				 if ( fn.startsWith ( LDBConstants.SUFFIX_DB_FILES ) )
				 {
						MishaLDB.context.deleteFile ( fn );
				 }
			}
	 }
	 
	 void deleteModelDBFiles ( String mid )
	 {
			
			String[] fileNames = MishaLDB.context.fileList ( );
			for ( String fn : fileNames )
			{
				 if ( fn.startsWith ( LDBConstants.SUFFIX_DB_FILES + mid ) )
				 {
						MishaLDB.context.deleteFile ( fn );
				 }
			}
	 }
	 
	 public
	 void deleteAllObjects ( String collectionName )
	 {
			
			String sqlStatement = "DELETE FROM " + ObjectEntry.TABLE_NAME + " WHERE " + ObjectEntry.COLUMN_NAME_COLLECTION_NAME + " = '"
														+ collectionName + "'";
			db.execSQL ( sqlStatement );
			sqlStatement = "DELETE FROM " + FieldEntry.TABLE_NAME + " WHERE " + FieldEntry.COLUMN_NAME_COLLECTION_NAME + " = '" + collectionName
										 + "'";
			db.execSQL ( sqlStatement );
	 }
	 
	 public
	 < T extends MModel > void deleteObjects ( List< T > models )
	 {
			
			StringBuilder sqlDelete = new StringBuilder ( "DELETE FROM " + ObjectEntry.TABLE_NAME + " WHERE " + ObjectEntry.COLUMN_NAME_MID + " "
																										+ "IN (" );
			for ( int i = 0 ; i < models.size ( ) ; ++i )
			{
				 MModel mModel = models.get ( i );
				 sqlDelete.append ( "'" + mModel.getMid ( ) + "'" );
				 if ( i < models.size ( ) - 1 ) sqlDelete.append ( ", " );
			}
			sqlDelete.append ( ")" );
			db.execSQL ( sqlDelete.toString ( ) );
	 }
}
