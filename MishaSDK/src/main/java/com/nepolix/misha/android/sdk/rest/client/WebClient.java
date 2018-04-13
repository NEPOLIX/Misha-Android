/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights
 * reserved. * * The source
 * code, other & all material, and documentation               * contained herein are, and
 * remains the property of HEX
 * Inc.             * and its suppliers, if any. The intellectual and technical * concepts
 * contained herein are
 * proprietary to HEX Inc. and its          * suppliers and may be covered by U.S. and Foreign
 * Patents, patents      *
 * in process, and are protected by trade secret or copyright law.        * Dissemination of the
 * foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.nepolix.misha.android.sdk.rest.client;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.nepolix.misha.android.sdk.commons.Utils;
import com.nepolix.misha.android.sdk.json.JSONException;
import com.nepolix.misha.android.sdk.json.JSONObject;
import com.nepolix.misha.android.sdk.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.android.sdk.task.handler.core.engine.TaskListener;
import com.nepolix.misha.android.sdk.task.handler.core.task.Task;
import com.nepolix.misha.android.sdk.task.handler.core.task.callback.Callback;
import timber.log.Timber;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author Behrooz Shahriari
 * @since 10/10/16
 */
public final
class WebClient
{
	 
	 static
	 {
			
			// Initialize configuration
			HttpsURLConnection.setDefaultHostnameVerifier ( ( hostname , sslSession ) -> !hostname.equals ( "localhost" ) );
	 }
	 
	 
	 public
	 enum RESTMethod
	 {
			GET ( "GET" ),
			POST ( "POST" ),
			PUT ( "PUT" ),
			DELETE ( "DELETE" );
			
			private String name;
			
			RESTMethod ( String name )
			{
				 
				 this.name = name;
			}
			
			@Override
			public
			String toString ( )
			{
				 
				 return name;
			}
	 }
	 
	 private static String USER_AGENT = "Misha";
	 
	 private static WebClient WEB_CLIENT = null;
	 
	 private static ITaskEngine TASK_RUNNER;
	 
	 private Context context;
	 
	 private
	 WebClient ( Context context )
	 {
			
			this.context = context;
			TASK_RUNNER = ITaskEngine.buildTaskRunner ( -101 );
			setUserAgent ( );
	 }
	 
	 private
	 WebClient ( ITaskEngine ITaskRunner ,
							 Context context )
	 {
			
			TASK_RUNNER = ITaskRunner;
			this.context = context;
			setUserAgent ( );
	 }
	 
	 private
	 void setUserAgent ( )
	 {
			
			String      packageName = context.getApplicationContext ( ).getPackageName ( );
			PackageInfo pInfo       = null;
			try
			{
				 pInfo = context.getApplicationContext ( ).getPackageManager ( ).getPackageInfo ( packageName , 0 );
				 USER_AGENT = getApplicationName ( context ) + "/" + ( pInfo.versionName ) + " (" + packageName + "; build:" + pInfo.versionCode + "; "
											+ "Android " + ( Build.VERSION.RELEASE + " - " + Build.VERSION.SDK_INT ) + ")";
			}
			catch ( PackageManager.NameNotFoundException e )
			{
				 e.printStackTrace ( );
				 USER_AGENT = getApplicationName ( context ) + " (" + packageName + "; " + "Android " + ( Build.VERSION.RELEASE + " - "
																																																	+ Build.VERSION.SDK_INT ) + ")";
			}
	 }
	 
	 public static
	 String getApplicationName ( Context context )
	 {
			
			ApplicationInfo applicationInfo = context.getApplicationInfo ( );
			int             stringId        = applicationInfo.labelRes;
			return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString ( ) : context.getString ( stringId );
	 }
	 
	 public static
	 WebClient getInstance ( Context context )
	 {
			
			if ( WEB_CLIENT == null ) WEB_CLIENT = new WebClient ( context );
			return WEB_CLIENT;
	 }
	 
	 public static
	 WebClient getInstance ( )
	 {
			
			return WEB_CLIENT;
	 }
	 
	 
	 public
	 void cUrl ( String url ,
							 RESTMethod method ,
							 Callback< JSONObject > callback ,
							 CUrlParameter... cUrlArgs )
	 {
			
			TASK_RUNNER.add ( new Task ( )
			{
				 
				 @Override
				 public
				 void execute ( ITaskEngine iTaskRunner ,
												TaskListener listener )
				 {
						
						JSONObject result = cUrl ( url , method , cUrlArgs );
						callback.onResult ( result );
						listener.setResult ( result );
						listener.finish ( );
				 }
			} );
	 }
	 
	 public
	 JSONObject cUrl ( String url ,
										 RESTMethod method ,
										 CUrlParameter... cUrlArgs )
	 {
			
			String args[] = new String[ ( cUrlArgs != null ? 2 * cUrlArgs.length : 0 ) + 4 ];
			args[ 0 ] = "curl";
			args[ 1 ] = "-X";
			args[ 2 ] = method.toString ( );
			args[ 3 ] = url;
			if ( cUrlArgs != null )
			{
				 for ( int i = 0 ; i < cUrlArgs.length ; i++ )
				 {
						args[ 4 + 2 * i ] = cUrlArgs[ i ].getFlag ( );
						args[ 4 + ( 2 * i + 1 ) ] = cUrlArgs[ i ].getParameter ( );
				 }
			}
			ProcessBuilder p = new ProcessBuilder ( args );
			p.redirectErrorStream ( true );
			try
			{
				 final Process shell       = p.start ( );
				 InputStream   errorStream = shell.getErrorStream ( );
				 InputStream   shellIn     = shell.getInputStream ( );
				 Scanner       scanner     = new Scanner ( shellIn );
				 StringBuilder builder     = new StringBuilder ( );
				 while ( scanner.hasNext ( ) )
				 {
						String line = scanner.nextLine ( );
						if ( line.startsWith ( "{" ) )
						{
							 builder.append ( line );
							 break;
						}
				 }
				 while ( scanner.hasNext ( ) )
				 {
						builder.append ( scanner.nextLine ( ) );
				 }
				 scanner.close ( );
				 return new JSONObject ( builder.toString ( ) );
			}
			catch ( IOException | JSONException e )
			{
				 e.printStackTrace ( );
				 return JSONException.exceptionToJSON ( e );
			}
	 }
	 
	 // Callback wrapper to allow us to do required logging.
	 private static
	 class CallbackWrapper
					 implements Callback< JSONObject >
	 {
			
			private final Callback< JSONObject > callback;
			
			private final String                 method;
			
			private final String                 url;
			
			CallbackWrapper ( Callback< JSONObject > callback ,
												String method ,
												String url )
			{
				 
				 this.callback = callback;
				 this.method = method;
				 this.url = url;
			}
			
			@Override
			public
			void onResult ( JSONObject result )
			{
				 
				 Timber.tag ( "API Call" )
							 .i ( "onResult from " + method + " URL=" + url + "\nRESULT=%s" , ( result != null ? result.toString ( ) : result ) );
				 if ( callback != null )
				 {
						callback.onResult ( result );
				 }
			}
			
			@Override
			public
			void onError ( JSONObject e )
			{
				 
				 Timber.tag ( "API Call" ).i ( "onError from " + method + " URL=" + url + "\nERROR=%s" , ( e != null ? e.toString ( ) : e ) );
				 if ( callback != null )
				 {
						callback.onError ( e );
				 }
			}
	 }
	 
	 public
	 WebClient call ( RESTMethod method ,
										String url ,
										HashMap< String, String > urlParams ,
										HashMap< String, String > headers ,
										JSONObject body ,
										Callback< JSONObject > callback )
	 {
			
			boolean https   = url.contains ( "https" );
			String  _method = "";
			if ( method == RESTMethod.GET ) _method = "GET";
			if ( method == RESTMethod.POST ) _method = "POST";
			if ( method == RESTMethod.PUT ) _method = "PUT";
			if ( method == RESTMethod.DELETE ) _method = "DELETE";
			
			final String final_method = _method;
			final String finalUrl     = buildURL ( url , urlParams );
			
			CallbackWrapper wrappedCallback = new CallbackWrapper ( callback , final_method , finalUrl );
			
			if ( https ) TASK_RUNNER.add ( getSecureRESTTask ( headers , body , final_method , finalUrl , wrappedCallback ) );
			else TASK_RUNNER.add ( getRESTTask ( headers , body , final_method , finalUrl , wrappedCallback ) );
			Timber.tag ( "API Call" )
						.i ( method.toString ( ) + " URL=" + url + "\nHEADERS=" + ( headers != null ? headers.toString ( ) : headers ) + "\nBODY=" + (
										body != null ? body.toString ( ) : body ) );
			return this;
	 }
	 
	 public
	 JSONObject call ( RESTMethod method ,
										 String url ,
										 HashMap< String, String > urlParams ,
										 HashMap< String, String > headers ,
										 JSONObject body )
	 {
			
			final MLock        LOCK       = new MLock ( );
			final JSONObject[] jsonObject = { null };
			call ( method , url , urlParams , headers , body , new Callback< JSONObject > ( )
			{
				 
				 @Override
				 public
				 void onResult ( JSONObject result )
				 {
						
						jsonObject[ 0 ] = result;
						LOCK.notifyLock ( );
				 }
				 
				 @Override
				 public
				 void onError ( JSONObject e )
				 {
						
						jsonObject[ 0 ] = e;
						LOCK.notifyLock ( );
				 }
			} );
			LOCK.hold ( );
			return jsonObject[ 0 ];
	 }
	 
	 private static
	 String buildURL ( String url ,
										 HashMap< String, String > urlParams )
	 {
			
			if ( url == null || url.isEmpty ( ) ) throw new NullPointerException ( "url can't be null" );
			if ( urlParams != null && !urlParams.isEmpty ( ) )
			{
				 StringBuffer builder = new StringBuffer ( url );
				 builder.append ( "?" );
				 for ( String key : urlParams.keySet ( ) )
				 {
						builder.append ( key );
						builder.append ( "=" );
						builder.append ( urlParams.get ( key ) );
						builder.append ( "&" );
				 }
				 builder.deleteCharAt ( builder.length ( ) - 1 );
				 url = builder.toString ( );
				 url = url.replaceAll ( " " , "%20" );
			}
			return url;
	 }
	 
	 private static
	 RESTTask getRESTTask ( final HashMap< String, String > headers ,
													final JSONObject body ,
													final String final_method ,
													final String finalUrl ,
													final Callback< JSONObject > callback )
	 {
			
			RESTTask task = new RESTTask ( callback )
			{
				 
				 @Override
				 protected
				 void callBackExecute ( ITaskEngine iTaskRunner ,
																TaskListener listener )
				 {
						
						JSONObject result = new JSONObject ( );
						try
						{
							 
							 URL               url        = new URL ( finalUrl );
							 HttpURLConnection connection = ( HttpURLConnection ) url.openConnection ( );
							 connection.setRequestMethod ( final_method );
							 connection.setRequestProperty ( "user-agent" , USER_AGENT );
							 connection.setRequestProperty ( "Accept-Language" , "en-US,en;q=0.5" );
							 connection.setConnectTimeout ( 30000 );
							 if ( headers != null && !headers.isEmpty ( ) )
							 {
									for ( String key : headers.keySet ( ) )
										 connection.setRequestProperty ( key , headers.get ( key ) );
							 }
							 Timber.tag ( "<x>" ).v ( "\t" + finalUrl + "   " + final_method );
							 writeBody ( body , final_method , connection );
							 int         responseCode = connection.getResponseCode ( );
							 InputStream inputStream  = null;
							 try
							 {
									inputStream = connection.getInputStream ( );
							 }
							 catch ( Exception ignored )
							 {
							 }
							 if ( inputStream == null ) inputStream = connection.getErrorStream ( );
							 BufferedInputStream bufferedInputStream = new BufferedInputStream ( inputStream );
							 StringBuilder       response            = new StringBuilder ( );
							 byte[]              contents            = new byte[ 1024 * 2 ];
							 int                 bytesRead;
							 try
							 {
									while ( ( bytesRead = bufferedInputStream.read ( contents ) ) != -1 )
									{
										 String x = new String ( contents , 0 , bytesRead );
										 response.append ( x );
									}
							 }
							 catch ( IOException e )
							 {
									e.printStackTrace ( );
							 }
							 String responseString = response.toString ( );
							 responseString = Utils.convertToUTF8 ( responseString );
							 bufferedInputStream.close ( );
							 try
							 {
									inputStream.close ( );
							 }
							 catch ( Exception ignored )
							 {
							 }
							 result = new JSONObject ( responseString );
							 Timber.tag ( "RESTTask RESPONSE=" ).v ( responseString.replace ( "\n" , "" ) );
						}
						catch ( Exception e )
						{
							 result = JSONException.exceptionToJSON ( e );
						}
						finally
						{
							 listener.setResult ( result );
							 listener.finish ( );
						}
				 }
			};
			return task;
	 }
	 
	 private static
	 void writeBody ( JSONObject body ,
										String final_method ,
										HttpURLConnection connection )
					 throws
					 IOException
	 {
			
			if ( body != null && !final_method.equals ( "GET" ) )
			{
				 connection.setDoOutput ( true );
				 DataOutputStream wr     = new DataOutputStream ( connection.getOutputStream ( ) );
				 BufferedWriter   writer = new BufferedWriter ( new OutputStreamWriter ( wr , "UTF-8" ) );
				 writer.write ( body.toString ( ) );
				 writer.close ( );
				 wr.close ( );
			}
	 }
	 
	 private static
	 RESTTask getSecureRESTTask ( final HashMap< String, String > headers ,
																final JSONObject body ,
																final String final_method ,
																final String finalUrl ,
																final Callback< JSONObject > callback )
	 {
			
			RESTTask task = new RESTTask ( callback )
			{
				 
				 @Override
				 protected
				 void callBackExecute ( ITaskEngine iTaskRunner ,
																TaskListener listener )
								 throws
								 Exception
				 {
						
						JSONObject result;
						boolean    failed = false;
						try
						{
							 result = httpsCall ( headers , body , final_method , finalUrl );
						}
						catch ( Exception e )
						{
							 failed = true;
							 result = JSONException.exceptionToJSON ( e );
						}
						if ( failed )
						{
							 try
							 {
									Timber.tag ( "SecRESTTask=" ).v ( "SECOND CALL" );
									result = httpsCall ( headers , body , final_method , finalUrl );
							 }
							 catch ( Exception e )
							 {
									result = JSONException.exceptionToJSON ( e );
							 }
						}
						listener.setResult ( result );
						listener.finish ( );
				 }
			};
			return task;
	 }
	 
	 private static
	 JSONObject httpsCall ( HashMap< String, String > headers ,
													JSONObject body ,
													String final_method ,
													String finalUrl )
					 throws
					 IOException,
					 JSONException
	 {
			
			JSONObject         result     = new JSONObject ( );
			URL                url        = new URL ( finalUrl );
			HttpsURLConnection connection = ( HttpsURLConnection ) url.openConnection ( );
			connection.setRequestMethod ( final_method );
			connection.setRequestProperty ( "User-Agent" , USER_AGENT );
			connection.setRequestProperty ( "Accept-Language" , "en-US,en;q=0.5" );
			connection.setSSLSocketFactory ( ( SSLSocketFactory ) SSLSocketFactory.getDefault ( ) );
//			{
//				 try
//				 {
//						TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance ( TrustManagerFactory.getDefaultAlgorithm ( ) );
//						trustManagerFactory.init ( ( KeyStore ) null );
//						TrustManager[] trustManagers = trustManagerFactory.getTrustManagers ( );
//						if ( trustManagers.length != 1 || !( trustManagers[ 0 ] instanceof X509TrustManager ) )
//						{
//							 throw new IllegalStateException ( "Unexpected default trust managers:" + Arrays.toString ( trustManagers ) );
//						}
//						X509TrustManager trustManager = ( X509TrustManager ) trustManagers[ 0 ];
//						SSLContext       sc           = SSLContext.getInstance ( "TLSv1.2" );
//						sc.init ( null , new TrustManager[] { trustManager } , new java.security.SecureRandom ( ) );
//						connection.setSSLSocketFactory ( sc.getSocketFactory ( ) );
//				 }
//				 catch ( NoSuchAlgorithmException | KeyManagementException | KeyStoreException e )
//				 {
//						e.printStackTrace ( );
//				 }
//			}
			connection.setReadTimeout ( 20000 );
			connection.setConnectTimeout ( 20000 );
			if ( headers != null && !headers.isEmpty ( ) )
			{
				 for ( String key : headers.keySet ( ) )
						connection.setRequestProperty ( key , headers.get ( key ) );
			}
			writeBody ( body , final_method , connection );
			int         responseCode = connection.getResponseCode ( );
			InputStream inputStream  = null;
			try
			{
				 inputStream = connection.getInputStream ( );
			}
			catch ( Exception ignored )
			{
			}
			if ( inputStream == null ) inputStream = connection.getErrorStream ( );
			BufferedInputStream bufferedInputStream = new BufferedInputStream ( inputStream );
			StringBuilder       response            = new StringBuilder ( );
			byte[]              contents            = new byte[ 1024 * 2 ];
			int                 bytesRead;
			try
			{
				 while ( ( bytesRead = bufferedInputStream.read ( contents ) ) != -1 )
				 {
						String x = new String ( contents , 0 , bytesRead );
						response.append ( x );
				 }
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
			String responseString = response.toString ( );
			responseString = Utils.convertToUTF8 ( responseString );
			bufferedInputStream.close ( );
			inputStream.close ( );
			connection.disconnect ( );
			result = new JSONObject ( responseString );
			Timber.tag ( "SecRESTTask RESPONSE=" ).v ( responseString );
			return result;
	 }
	 
	 public
	 void terminate ( )
	 {
			
			TASK_RUNNER.stop ( );
	 }
	 
	 public synchronized
	 void cancel ( )
	 {
			
			synchronized ( this )
			{
				 TASK_RUNNER.clearTasks ( );
			}
	 }


//	 public
//	 void restart ( )
//	 {
//
//			TASK_RUNNER = ITaskRunner.buildTaskRunner ( -1 );
//	 }
	 
	 public
	 ITaskEngine getTaskRunner ( )
	 {
			
			return TASK_RUNNER;
	 }
}
