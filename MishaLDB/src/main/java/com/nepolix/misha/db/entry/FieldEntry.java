package com.nepolix.misha.db.entry;

import android.provider.BaseColumns;

/**
 * @author Behrooz Shahriari
 * @since 2/16/17
 */

public final
class FieldEntry
				implements BaseColumns
{

	 public static final String TABLE_NAME = "FIELDS";

	 public final static String COLUMN_NAME_MID = "MID";

	 public final static String COLUMN_NAME_FIELD_CHAIN = "FIELD_CHAIN";

	 public final static String COLUMN_NAME_HASH = "HASH";

	 public final static String COLUMN_NAME_COLLECTION_NAME = "COLLECTION_NAME";

	 public final static String COLUMN_NAME_FIELD_TYPE = "FIELD_TYPE";

	 public final static String COLUMN_NAME_VALUE_INT = "VALUE_INT";

	 public final static String COLUMN_NAME_VALUE_DOUBLE = "VALUE_DOUBLE";

	 public final static String COLUMN_NAME_VALUE_STRING = "VALUE_STRING";
}
