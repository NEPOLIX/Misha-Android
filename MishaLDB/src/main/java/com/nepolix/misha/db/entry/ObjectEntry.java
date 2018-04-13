package com.nepolix.misha.db.entry;

import android.provider.BaseColumns;

/**
 * @author Behrooz Shahriari
 * @since 2/16/17
 */

public final
class ObjectEntry
				implements BaseColumns
{

	 public static final String TABLE_NAME = "OBJECTS";

	 public final static String COLUMN_NAME_MID = "MID";

	 public final static String COLUMN_NAME_JSON = "JSON";

	 public final static String COLUMN_NAME_HASH = "HASH";

	 public final static String COLUMN_NAME_COLLECTION_NAME = "COLLECTION_NAME";
}