package com.nepolix.misha.db.model;

import com.nepolix.misha.android.sdk.json.JSONObject;

/**
 * @author Behrooz Shahriari
 * @since 4/27/17
 */
public
interface IMigrationProtocol
{
	 
	 JSONObject migrate ( JSONObject rowModel );
}