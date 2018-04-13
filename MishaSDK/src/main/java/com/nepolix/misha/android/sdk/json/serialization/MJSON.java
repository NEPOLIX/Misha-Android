package com.nepolix.misha.android.sdk.json.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepolix.misha.android.sdk.json.JSONArray;
import com.nepolix.misha.android.sdk.json.JSONException;
import com.nepolix.misha.android.sdk.json.JSONObject;

/**
 * @author Behrooz Shahriari
 * @since 10/14/16
 */
public final
class MJSON
{
	 
	 public static
	 JSONObject toJSON ( Object object )
	 {

//			JSONParser jsonParser = new JSONParser ( object );
//			return jsonParser.parse ( );
			
			if ( object == null ) return new JSONObject ( );
			
			JSONObject   jsonObject = null;
			ObjectMapper mapper     = new ObjectMapper ( );
			mapper.setVisibility ( mapper.getSerializationConfig ( ).getDefaultVisibilityChecker ( ).withFieldVisibility ( JsonAutoDetect.Visibility.ANY )
																	 .withGetterVisibility ( JsonAutoDetect.Visibility.NONE ).withSetterVisibility ( JsonAutoDetect.Visibility.NONE )
																	 .withCreatorVisibility ( JsonAutoDetect.Visibility.NONE ) );
			try
			{
				 jsonObject = new JSONObject ( mapper.writeValueAsString ( object ) );
				 
				 if ( jsonObject.has ( "_id" ) && ( jsonObject.getString ( "_id" ) == null || jsonObject.getString ( "_id" ).equals ( "null" ) ) )
				 {
						jsonObject.remove ( "_id" );
				 }
			}
			catch ( JSONException | JsonProcessingException e )
			{
				 e.printStackTrace ( );
				 return jsonObject;
			}
			return jsonObject;
	 }
	 
	 public static
	 < T > T toObject ( String data ,
											Class clazz )
	 {
			
			return toObject ( toJSON ( data ) , clazz );
	 }
	 
	 public static
	 < T > T toObject ( JSONObject jsonObject ,
											Class clazz )
	 {
			
			try
			{
				 if ( jsonObject.isEmpty ( ) ) return null;
				 ObjectMapper mapper = new ObjectMapper ( );
				 mapper.setVisibility (
								 mapper.getSerializationConfig ( ).getDefaultVisibilityChecker ( ).withFieldVisibility ( JsonAutoDetect.Visibility.ANY )
											 .withGetterVisibility ( JsonAutoDetect.Visibility.NONE ).withSetterVisibility ( JsonAutoDetect.Visibility.NONE )
											 .withCreatorVisibility ( JsonAutoDetect.Visibility.NONE ) )
							 .configure ( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES , false );
				 return mapper.readValue ( jsonObject.toString ( ) , ( Class< T > ) clazz );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
			return null;
	 }
	 
	 public static
	 void print ( JSONObject jsonObject )
	 {
			
			System.out.println ( toString ( jsonObject ) );
	 }
	 
	 public static
	 String toString ( JSONObject jsonObject )
	 {
			
			try
			{
				 return jsonObject.toString ( 2 );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
			}
			return null;
	 }
	 
	 public static
	 JSONObject toJSON ( String data )
	 {
			
			JSONObject object = null;
			try
			{
				 object = new JSONObject ( data );
			}
			catch ( JSONException e )
			{
				 e.printStackTrace ( );
			}
			return object;
	 }
	 
	 public static
	 < T > T getOptionalJSON ( JSONObject jsonObject ,
														 String name ,
														 Class< T > cls )
	 {
			
			JSONObject optionalJson = jsonObject.optJSONObject ( name );
			if ( optionalJson != null ) return MJSON.toObject ( optionalJson , cls );
			return null;
	 }
	 
	 public static
	 < T > T getOptionalJSONArray ( JSONArray jsonArray ,
																	int index ,
																	Class< T > cls )
	 {
			
			JSONObject optionalJson = jsonArray.optJSONObject ( index );
			if ( optionalJson != null ) return MJSON.toObject ( optionalJson , cls );
			return null;
	 }
}
