package com.nepolix.misha.db.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nepolix.misha.android.sdk.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.db.entry.FieldEntry;
import com.nepolix.misha.db.entry.ObjectEntry;

/**
 * Created by ubuntu on 2/16/17.
 */

class LDBHelper
				extends SQLiteOpenHelper
{

	 public final static String DB_NAME = "MISHA.db";


	 private Context context;

	 private ITaskEngine taskRunner;


	 LDBHelper ( Context context )
	 {

			super ( context, DB_NAME, null, 1 );
			this.context = context;
	 }

	 LDBHelper ( Context context,
							 ITaskEngine taskRunner )
	 {

			super ( context, DB_NAME, null, 1 );
			this.context = context;
			this.taskRunner = taskRunner;
	 }

	 public
	 Context getContext ( )
	 {

			return context;
	 }

	 public
	 void setContext ( Context context )
	 {

			this.context = context;
	 }

	 @Override
	 public
	 void onCreate ( SQLiteDatabase db )
	 {

			String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + ObjectEntry.TABLE_NAME + " ("
																	+ ObjectEntry._ID + " INTEGER " + "PRIMARY KEY AUTOINCREMENT, "
																	+ ObjectEntry.COLUMN_NAME_MID + " TEXT NOT NULL, "
																	+ ObjectEntry.COLUMN_NAME_JSON + " TEXT, "
																	+ ObjectEntry.COLUMN_NAME_HASH + " INTEGER, " + ""
																	+ ObjectEntry.COLUMN_NAME_COLLECTION_NAME + " TEXT)";
			db.execSQL ( SQL_CREATE_ENTRIES );
			SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + FieldEntry.TABLE_NAME + " ("
													 + FieldEntry._ID + " " + "INTEGER " + "PRIMARY KEY AUTOINCREMENT, "
													 + FieldEntry.COLUMN_NAME_MID + " TEXT NOT NULL, "
													 + FieldEntry.COLUMN_NAME_FIELD_CHAIN + " TEXT NOT NULL, "
													 + FieldEntry.COLUMN_NAME_HASH +
													 " INTEGER, " + "" + FieldEntry.COLUMN_NAME_COLLECTION_NAME + " TEXT, "
													 + FieldEntry.COLUMN_NAME_FIELD_TYPE + " TEXT, "
													 + FieldEntry.COLUMN_NAME_VALUE_INT + " INTEGER, "
													 + FieldEntry.COLUMN_NAME_VALUE_DOUBLE + " REAL, "
													 + FieldEntry.COLUMN_NAME_VALUE_STRING + " TEXT)";
			db.execSQL ( SQL_CREATE_ENTRIES );
	 }

	 @Override
	 public
	 void onUpgrade ( SQLiteDatabase db,
										int oldVersion,
										int newVersion )
	 {

			if ( newVersion > oldVersion )
			{
				 String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FieldEntry.TABLE_NAME;
				 db.execSQL ( SQL_DELETE_ENTRIES );
				 SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ObjectEntry.TABLE_NAME;
				 db.execSQL ( SQL_DELETE_ENTRIES );
				 onCreate ( db );
			}
	 }
}
