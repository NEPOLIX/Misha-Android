package com.nepolix.misha.db.core;

import android.content.Context;
import com.nepolix.misha.android.sdk.commons.Base64;
import com.nepolix.misha.android.sdk.json.serialization.MJSON;
import com.nepolix.misha.db.common.DBCommons;
import com.nepolix.misha.db.entry.FieldEntry;
import com.nepolix.misha.db.entry.ObjectEntry;
import com.nepolix.misha.db.json.FlattenJSONObject;
import com.nepolix.misha.db.json.JSONHelper;
import com.nepolix.misha.db.model.MModel;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.nepolix.misha.db.common.DBCommons.*;
import static com.nepolix.misha.db.core.LDBConstants.MAXIMUM_DATA_SIZE;
import static com.nepolix.misha.db.core.LDBConstants.SUFFIX_DB_FILES;
import static com.nepolix.misha.db.core.MishaLDB.db;
import static com.nepolix.misha.db.entry.ObjectEntry.*;

/**
 * Created by ubuntu on 2/16/17.
 */

class DBSaveHelper
{
	 
	 private final static DBSaveHelper DB_SAVE_HELPER = new DBSaveHelper ( );
	 
	 private
	 DBSaveHelper ( )
	 {
			
	 }
	 
	 public static
	 DBSaveHelper getInstance ( )
	 {
			
			return DB_SAVE_HELPER;
	 }
	 
	 void save ( MModel object )
	 {
			
			DBDeleteHelper dbDeleteHelper = DBDeleteHelper.getInstance ( );
			dbDeleteHelper.deleteObject ( object.getMid ( ), object.modelName ( ) );
			List< MModel > mModels = new ArrayList<> ( );
			mModels.add ( object );
			saveModels ( mModels );
			saveObjectFields ( mModels );
	 }
	 
	 private
	 < T extends MModel > void saveModels ( List< T > mModels )
	 {

//			String deleteStatement = "DELETE FROM " + ObjectEntry.TABLE_NAME + " WHERE " + COLUMN_NAME_MID + " = '" + mModel.getMid ( ) + "'";
//			db.execSQL ( deleteStatement );
			
			db.beginTransaction ( );
			for ( int i = 0 ; i < mModels.size ( ) ; ++i )
			{
				 MModel mModel = mModels.get ( i );
				 String vs = Base64.toBase64 ( MJSON.toJSON ( mModel )
																						.toString ( )
																						.getBytes ( ) );
				 vs = compressLargeDataToFile ( vs, mModel, null );
				 StringBuilder sqlInsert = new StringBuilder ( "INSERT INTO " + ObjectEntry.TABLE_NAME + " (" + COLUMN_NAME_MID + ", "
																											 + COLUMN_NAME_JSON + ", " + COLUMN_NAME_HASH + ", " + COLUMN_NAME_COLLECTION_NAME
																											 + ") VALUES (" + "'" + mModel.getMid ( ) + "'" + ", " + "'" + vs + "'" + ", " + 0
																											 + ", " + "'" + mModel.modelName ( ) + "'" + ")" );
				 db.execSQL ( sqlInsert.toString ( ) );
			}
			db.setTransactionSuccessful ( );
			db.endTransaction ( );
	 }
	 
	 private
	 < T extends MModel > void saveObjectFields ( List< T > objects )
	 {
			
			{
				 String sqlStatement = "DELETE FROM " + FieldEntry.TABLE_NAME + " WHERE " + FieldEntry.COLUMN_NAME_MID + " IN (";
				 for ( int i = 0 ; i < objects.size ( ) ; ++i )
				 {
						MModel mModel = objects.get ( i );
						sqlStatement += "'" + mModel.getMid ( ) + "'";
						if ( i < objects.size ( ) - 1 ) sqlStatement += ", ";
				 }
				 sqlStatement += ")";
				 db.execSQL ( sqlStatement );
			}
			{
				 
				 db.beginTransaction ( );
				 for ( int i = 0 ; i < objects.size ( ) ; ++i )
				 {
						MModel            object            = objects.get ( i );
						FlattenJSONObject flattenJSONObject = JSONHelper.flattenObjectFields ( MJSON.toJSON ( object ) );
						List< String >    fieldChains       = flattenJSONObject.$getFieldChains ( );
						for ( String fc : fieldChains )
						{
							 List< Object > values = flattenJSONObject.$getValues ( fc );
							 for ( Object v : values )
							 {
									StringBuilder sqlInsert = new StringBuilder ( "INSERT INTO " + FieldEntry.TABLE_NAME + " (" + FieldEntry.COLUMN_NAME_MID
																																+ ", " + FieldEntry.COLUMN_NAME_FIELD_CHAIN + ", "
																																+ FieldEntry.COLUMN_NAME_HASH + ", "
																																+ FieldEntry.COLUMN_NAME_COLLECTION_NAME + ", "
																																+ FieldEntry.COLUMN_NAME_FIELD_TYPE + ", "
																																+ FieldEntry.COLUMN_NAME_VALUE_INT + ", "
																																+ FieldEntry.COLUMN_NAME_VALUE_DOUBLE + ", "
																																+ FieldEntry.COLUMN_NAME_VALUE_STRING + ") VALUES " );
									if ( v == null ) continue;
									String type = DBCommons.getDBFieldType ( v );
									String vi = "(" + "'" + object.getMid ( ) + "'" + ", " + "'" + fc + "'" + ", " + 0 + ", " + "'" + object.modelName ( )
															+ "'" + ", " + "'" + type + "', ";
									if ( type.equals ( FIELD_TYPE_INTEGER ) || type.equals ( FIELD_TYPE_LONG ) )
									{
										 vi = vi + Long.parseLong ( v.toString ( ) ) + ", " + 0.0 + ", 'NULL'" + ")";
									}
									if ( type.equals ( FIELD_TYPE_BOOLEAN ) )
									{
										 vi = vi + ( ( Boolean ) v ? 1 : 0 ) + ", " + 0.0 + ", " + "'NULL'" + ")";
									}
									if ( type.equals ( FIELD_TYPE_DOUBLE ) )
									{
										 vi = vi + 0 + ", " + Double.parseDouble ( v.toString ( ) ) + ", 'NULL'" + ")";
									}
									if ( type.equals ( FIELD_TYPE_STRING ) )
									{
										 String vs = escapeString$QueryExecute ( v.toString ( ) );
										 vs = compressLargeDataToFile ( vs, object, fc );
										 vi = vi + 0 + ", " + 0.0 + ", " + "'" + vs + "'" + ")";
									}
									sqlInsert.append ( vi );
									db.execSQL ( sqlInsert.toString ( ) );
							 }
						}
				 }
				 db.setTransactionSuccessful ( );
				 db.endTransaction ( );
			}
	 }
	 
	 private static
	 String compressLargeDataToFile ( String data,
																		MModel object,
																		String fc )
	 {
			
			String vs = data;
			if ( data.length ( ) > MAXIMUM_DATA_SIZE )
			{
				 //SAVE in FILE
				 String           fileName = SUFFIX_DB_FILES + object.getMid ( ) + ( fc != null ? "$" + fc : "" );
				 FileOutputStream outputStream;
				 try
				 {
						outputStream = MishaLDB.context.openFileOutput ( fileName, Context.MODE_PRIVATE );
						outputStream.write ( data.getBytes ( ) );
						outputStream.close ( );
						vs = fileName;
				 }
				 catch ( Exception e )
				 {
						e.printStackTrace ( );
				 }
			}
			return vs;
	 }
	 
	 public
	 < T extends MModel > void save ( List< T > models )
	 {
			
			DBDeleteHelper dbDeleteHelper = DBDeleteHelper.getInstance ( );
			dbDeleteHelper.deleteObjects ( models );
			saveModels ( models );
			saveObjectFields ( models );
	 }
}
