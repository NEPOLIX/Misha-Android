/****************************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)                        *
 *           All rights reserved.                                                       *
 *                                                                                      *
 *     The source code, other & all material, and documentation                         *
 *     contained herein are, and remains the property of HEX Inc.                       *
 *     and its suppliers, if any. The intellectual and technical                        *
 *     concepts contained herein are proprietary to HEX Inc. and its                    *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents                *
 *     in process, and are protected by trade secret or copyright law.                  *
 *     Dissemination of the foregoing material or reproduction of this                  *
 *     material is strictly forbidden forever.                                          *
 ****************************************************************************************/

package com.nepolix.misha.android.sdk.commons;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.*;

/**
 * @author Behrooz Shahriari
 * @since 11/26/16
 */
public
class Utils
{
	 
	 private final static SecureRandom RANDOM = new SecureRandom ( );
	 
	 private final static String NUMBERS = "0123456789";
	 
	 private final static String STRING = "0123456789qwertyuiopasdfghjklzxcvbnmPOIUYTREWQASDFGHJKLMNBVCXZ=_";
	 
	 private final static SimpleDateFormat FORMAT = new SimpleDateFormat ( "yyyy-MM-dd'T'HH:mm:ss" + ".SSS" );
	 
	 private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*" + "(\\.[A-Za-z]{2,})$";
	 
	 private final static Pattern patternEmail = Pattern.compile ( EMAIL_PATTERN );
	 
	 private static final Charset UTF_8 = Charset.forName ( "UTF-8" );
	 
	 public static
	 String normalizePhone ( String phone )
	 {
			
			String p = phone;
			p = p.replace ( "(", "" );
			p = p.replace ( ")", "" );
			p = p.replace ( " ", "" );
			p = p.replace ( "-", "" );
			p = p.replace ( "_", "" );
			return p;
	 }
	 
	 ///////////////////////////////////////////////////////////////////
	 ///////////////////////////////////////////////////////////////////
	 ///////////////////////////////////////////////////////////////////
	 
	 public static
	 String randomPassCode ( int len )
	 {
			
			String r = "";
			for ( int i = 0 ; i < len ; ++i )
			{
				 r += NUMBERS.charAt ( RANDOM.nextInt ( NUMBERS.length ( ) ) );
			}
			return r;
	 }
	 
	 public static
	 String generateRefreshToken ( String password )
	 {
			
			byte[] bytes = new byte[ 128 ];
			RANDOM.nextBytes ( bytes );
			byte bs[] = password.getBytes ( );
			for ( int i = 0 ; i < password.length ( ) ; ++i )
			{
				 for ( int j = 0 ; j < 5 ; ++j )
				 {
						int idx = RANDOM.nextInt ( bytes.length );
						int idy = RANDOM.nextInt ( bs.length );
						bytes[ idx ] ^= bs[ idy ];
				 }
			}
			return Base64.toBase64 ( bytes );
	 }
	 
	 public static
	 String hash1024 ( String message )
	 {
			
			int    len = message.length ( );
			String p1  = message.substring ( 0, len / 3 );
			String p2  = message.substring ( len / 3, 2 * len / 3 );
			String p3  = message.substring ( 2 * len / 3 );
			
			String h1 = hash512 ( p1, null );
			String h2 = hash512 ( p2, null );
			String h3 = hash512 ( p3, null );
			String h  = h1 + h2 + h3;
			return hash512 ( h.substring ( 0, h.length ( ) / 2 ), null ) + hash512 ( h.substring ( h.length ( ) / 2 ), null );
	 }
	 
	 public static
	 String hash512 ( String message,
										String salt )
	 {
			
			String hashCode = null;
			try
			{
				 if ( salt == null ) salt = randomBase64String ( 40 );
				 MessageDigest md = MessageDigest.getInstance ( "SHA-512" );
				 md.update ( salt.getBytes ( "UTF-8" ) );
				 byte[]        bytes = md.digest ( message.getBytes ( "UTF-8" ) );
				 StringBuilder sb    = new StringBuilder ( );
				 for ( int i = 0 ; i < bytes.length ; i++ )
				 {
						sb.append ( Integer.toString ( ( bytes[ i ] & 0xff ) + 0x100, 16 ).substring ( 1 ) );
				 }
				 hashCode = sb.toString ( );
			}
			catch ( NoSuchAlgorithmException | UnsupportedEncodingException e )
			{
				 e.printStackTrace ( );
			}
			return hashCode;
	 }
	 
	 public static
	 String randomBase64String ( int len )
	 {
			
			byte[] bytes  = new byte[ len ];
			Random random = new Random ( );
			random.nextBytes ( bytes );
			return Base64.toBase64 ( bytes );
	 }
	 
	 public static
	 String getCurrentCodeInfo ( final Thread currentThread )
	 {
			
			final StackTraceElement[] stackTrace   = currentThread.getStackTrace ( );
			long                      memoryInfo[] = memoryInfo ( );
			return "Line Number: " + stackTrace[ 2 ].getLineNumber ( ) + ", File Name: " + stackTrace[ 2 ].getFileName ( ) +
//						 ", Class Name: " + stackTrace[ 2 ].getClassName ( ) +
						 ", Method Name: " + stackTrace[ 2 ].getMethodName ( ) + ", Memory:[aloc:" + memoryInfo[ 2 ] + " free:" + memoryInfo[ 2 ] + " totl:"
						 + memoryInfo[ 2 ] + "]" + ", Time: " + getCurrentFormattedUTCTime ( );
	 }
	 
	 public static
	 long[] memoryInfo ( )
	 {
			
			long freeSize  = 0L;
			long totalSize = 0L;
			long usedSize  = -1L;
			try
			{
				 Runtime info = getRuntime ( );
				 freeSize = info.freeMemory ( );
				 totalSize = info.totalMemory ( );
				 usedSize = totalSize - freeSize;
				 usedSize /= 1048576;
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
			return new long[] {
							totalSize / 1048576 ,
							freeSize / 1048576 ,
							usedSize
			};
	 }
	 
	 public static
	 String getCurrentFormattedUTCTime ( )
	 {
			
			Calendar calendar = getUTCTime ( );
			return FORMAT.format ( calendar.getTime ( ) ) + "Z";
	 }
	 
	 public static
	 Calendar getUTCTime ( )
	 {
			
			Calendar calendar           = Calendar.getInstance ( );
			long     timeInMilliseconds = calendar.getTimeInMillis ( );
			int offsetFromUTC = calendar.getTimeZone ( ).getOffset ( timeInMilliseconds );
			calendar.setTimeInMillis ( calendar.getTimeInMillis ( ) - offsetFromUTC );
			return calendar;
	 }
	 
	 
	 public static
	 long getCurrentTime$TimeZone ( String timeZoneID )
	 {
			
			return getTimeZoneCalendar ( timeZoneID ).getTimeInMillis ( );
	 }
	 
	 public static
	 Calendar getTimeZoneCalendar ( String timeZoneID )
	 {
			
			return Calendar.getInstance ( TimeZone.getTimeZone ( timeZoneID ) );
	 }
	 
	 public static
	 int daysBetween ( Calendar startDate,
										 Calendar endDate )
	 {
			
			long end   = endDate.getTimeInMillis ( );
			long start = startDate.getTimeInMillis ( );
			return ( int ) TimeUnit.MILLISECONDS.toDays ( Math.abs ( end - start ) );
	 }
	 
	 public static
	 String convertUTC$Locale ( String formattedUTCTime )
	 {
			
			long utcTime       = getTimeFromFormattedUTCTime ( formattedUTCTime );
			int  offsetFromUTC = getOffsetFromUTC ( );
			long localeTime    = utcTime + offsetFromUTC;
			return getFormattedUTCTime ( localeTime );
	 }
	 
	 public static
	 long getTimeFromFormattedUTCTime ( String formattedUTCTime )
	 {
			
			if ( formattedUTCTime == null || formattedUTCTime.length ( ) < 3 ) return -1;
			try
			{  //remove 'Z'
				 formattedUTCTime = formattedUTCTime.substring ( 0, formattedUTCTime.length ( ) - 1 );
				 Date date = FORMAT.parse ( formattedUTCTime );
				 return date.getTime ( );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
				 return -1;
			}
	 }
	 
	 public static
	 int getOffsetFromUTC ( )
	 {
			
			Calendar calendar           = Calendar.getInstance ( );
			long     timeInMilliseconds = calendar.getTimeInMillis ( );
			return calendar.getTimeZone ( ).getOffset ( timeInMilliseconds );
	 }
	 
	 public static
	 String getFormattedUTCTime ( long time )
	 {
			
			return FORMAT.format ( time ) + "Z";
	 }
	 
	 public static
	 boolean validateEmail ( final String email )
	 {
			
			final Matcher matcher;
			matcher = patternEmail.matcher ( email );
			return matcher.matches ( );
			
	 }
	 
	 public static
	 String generateRandomString ( int len )
	 {
			
			StringBuilder buffer = new StringBuilder ( );
			for ( int i = 0 ; i < len ; ++i )
				 buffer.append ( STRING.charAt ( RANDOM.nextInt ( STRING.length ( ) ) ) );
			return buffer.toString ( );
	 }
	 
	 public static
	 void causeGC ( )
	 {
			
			runFinalization ( );
			getRuntime ( ).gc ( );
			gc ( );
	 }
	 
	 public static
	 long randomLong ( )
	 {
			
			return RANDOM.nextLong ( );
	 }
	 
	 public static
	 int randomInt ( )
	 {
			
			return RANDOM.nextInt ( );
	 }
	 
	 
	 public static
	 String convertToUTF8 ( String str )
	 {
			
			byte[] byteArray = str.getBytes ( UTF_8 );
			return new String ( byteArray, UTF_8 );
	 }
	 
	 public static
	 String convertToUTF8 ( int hexString )
	 {
			
			char[] emojiCharArray = Character.toChars ( hexString );
			return new String ( emojiCharArray );
	 }
	 
	 public static
	 String prettyNameFormat ( String name )
	 {
			
			String t = name;
			if ( t != null && !t.isEmpty ( ) )
			{
				 t = name.substring ( 0, 1 ).toUpperCase ( );
				 if ( name.length ( ) > 0 )
				 {
						t = t + name.substring ( 1 ).toLowerCase ( );
				 }
			}
			return t;
	 }
	 
	 ////////////////////////////////////////////
	 public static
	 < T > Set< T > findCommonSet ( Set< T > set1,
																	Set< T > set2 )
	 {
			
			if ( set1 == null || set2 == null ) throw new NullPointerException ( "neither sets must be null" );
			Set< T > smallSet;
			Set< T > largeSet;
			if ( set1.size ( ) != set2.size ( ) )
			{
				 smallSet = set1.size ( ) < set2.size ( ) ? set1 : set2;
				 largeSet = set2.size ( ) < set1.size ( ) ? set1 : set2;
			}
			else
			{
				 smallSet = set1;
				 largeSet = set2;
			}
			Set< T > common = new HashSet< T > ( );
			for ( T t : smallSet )
				 if ( largeSet.contains ( t ) ) common.add ( t );
			return common;
	 }
	 
	 public static
	 int amountToStripeValue ( String cost )
	 {
			
			if ( !cost.contains ( "." ) )
			{
				 return Integer.parseInt ( cost + "00" );
			}
			else
			{
				 String parts[] = cost.split ( "\\." );
				 String cash    = parts[ 0 ];
				 String cent    = parts[ 1 ];
				 if ( cent.length ( ) > 2 ) throw new IllegalArgumentException ( "incorrect cost format" );
				 if ( cent.length ( ) == 1 ) cent = cent + "0";
				 if ( cent.length ( ) == 0 ) cent = cent + "00";
				 return Integer.parseInt ( cash + cent );
			}
	 }
	 
	 
	 public static
	 long getCurrentUTCTime ( )
	 {
			
			Calendar calendar = getUTCTime ( );
			return calendar.getTimeInMillis ( );
	 }
	 
	 public static
	 String getStringFormateFromDate ( int year,
																		 int month,
																		 int day )
	 {
			
			SimpleDateFormat format = new SimpleDateFormat ( "MM/dd/yyyy" );
			Calendar         cal    = Calendar.getInstance ( );
			cal.set ( Calendar.YEAR, year );
			cal.set ( Calendar.MONTH, month );
			cal.set ( Calendar.DAY_OF_MONTH, day );
			return format.format ( cal.getTime ( ) );
	 }
	 
	 
	 public static
	 String prettyPhone ( String ph )
	 {
			
			String x = normalizePhone ( ph );
			if ( x.length ( ) <= 3 ) return x;
			if ( x.length ( ) >= 4 && x.length ( ) < 7 )
			{
				 String x1 = x.substring ( 0, 3 );
				 String x2 = x.substring ( 3, x.length ( ) );
				 return "(" + x1 + ") " + x2;
			}
			if ( x.length ( ) >= 7 )
			{
				 String x1 = x.substring ( 0, 3 );
				 String x2 = x.substring ( 3, 6 );
				 String x3 = x.substring ( 6, x.length ( ) );
				 return "(" + x1 + ") " + x2 + "-" + x3;
			}
			return x;
	 }
	 
}
