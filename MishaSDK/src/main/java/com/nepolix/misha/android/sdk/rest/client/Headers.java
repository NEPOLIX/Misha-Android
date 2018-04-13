package com.nepolix.misha.android.sdk.rest.client;

import java.util.HashMap;

/**
 * @author Behrooz Shahriari
 * @since 9/13/17
 */

public
class Headers
{
	 
	 private HashMap< String, String > headers;
	 
	 public
	 Headers ( )
	 {
			
			headers = new HashMap<> ( );
	 }
	 
	 public
	 Headers addHeader ( String key,
											 String value )
	 {
			
			headers.put ( key, value );
			return this;
	 }
	 
	 public
	 HashMap< String, String > toMap ( )
	 {
			
			return headers;
	 }
	 
}
