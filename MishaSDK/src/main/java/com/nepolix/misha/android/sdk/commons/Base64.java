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

import java.util.ArrayList;

/**
 * @author Behrooz Shahriari
 * @since 10/14/16
 */
public
class Base64
{
	 
	 private final static String BASE64_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+_";
	 
	 public static
	 String toBase64 ( byte array[] )
	 {
			
			if ( array == null || array.length == 0 ) return null;
			StringBuilder builder = new StringBuilder ( );
			for ( int i = 0 ; i < array.length ; ++i )
			{
				 if ( i % 3 == 0 )
				 {
						char c = BASE64_CHARACTERS.charAt ( getLastKBits ( array[ i ], 6 ) );
						builder.append ( c );
						if ( i == array.length - 1 )
						{
							 byte x = getFirstKBits ( array[ i ], 2 );
							 x = ( byte ) ( x << 4 );
							 String c2 = BASE64_CHARACTERS.charAt ( x ) + "==";
							 builder.append ( c2 );
						}
				 }
				 if ( i % 3 == 1 )
				 {
						byte r = getFirstKBits ( array[ i - 1 ], 2 );
						r = ( byte ) ( r << 4 );
						byte x = getLastKBits ( array[ i ], 4 );
						x = ( byte ) ( x | r );
						char c1 = BASE64_CHARACTERS.charAt ( x );
						builder.append ( c1 );
						r = getFirstKBits ( array[ i ], 4 );
						if ( i != array.length - 1 )
						{
							 x = getLastKBits ( array[ i + 1 ], 2 );
							 r = ( byte ) ( r << 2 );
							 x = ( byte ) ( x | r );
							 char c2 = BASE64_CHARACTERS.charAt ( x );
							 builder.append ( c2 );
						}
						else
						{
							 String c2 = BASE64_CHARACTERS.charAt ( r << 2 ) + "=";
							 builder.append ( c2 );
						}
				 }
				 if ( i % 3 == 2 )
				 {
						char c = BASE64_CHARACTERS.charAt ( getFirstKBits ( array[ i ], 6 ) );
						builder.append ( c );
				 }
			}
			return builder.toString ( );
	 }
	 
	 
	 public static
	 byte[] decodeBase64 ( String s )
	 {
			
			if ( s == null || s.isEmpty ( ) ) return null;
			char              ss[]  = s.toCharArray ( );
			ArrayList< Byte > bytes = new ArrayList<> ( );
			for ( int i = 0 ; i < ss.length ; ++i )
			{
				 if ( i % 4 == 0 )
				 {
						byte x = ( byte ) BASE64_CHARACTERS.indexOf ( ss[ i ] );
						x = ( byte ) ( x << 2 );
						byte z = ( byte ) BASE64_CHARACTERS.indexOf ( ss[ i + 1 ] );
						z = ( byte ) ( getFirstKBits ( z, 6 ) << 2 );
						z = getLastKBits ( z, 2 );
						byte b = ( byte ) ( x | z );
						bytes.add ( b );
				 }
				 if ( i % 4 == 1 )
				 {
						if ( ss[ i ] != '=' )
						{
							 byte x = ( byte ) BASE64_CHARACTERS.indexOf ( ss[ i ] );
							 x = getFirstKBits ( x, 4 );
							 x = ( byte ) ( x << 4 );
							 if ( ss[ i + 1 ] != '=' )
							 {
									byte z = ( byte ) BASE64_CHARACTERS.indexOf ( ss[ i + 1 ] );
									z = ( byte ) ( getFirstKBits ( z, 6 ) << 2 );
									z = getLastKBits ( z, 4 );
									x = ( byte ) ( z | x );
									byte b = x;
									bytes.add ( b );
							 }
						}
						else break;
				 }
				 if ( i % 4 == 2 )
				 {
						if ( ss[ i ] != '=' )
						{
							 byte x = ( byte ) BASE64_CHARACTERS.indexOf ( ss[ i ] );
							 x = getFirstKBits ( x, 2 );
							 x = ( byte ) ( x << 6 );
							 if ( ss[ i + 1 ] != '=' )
							 {
									byte z = ( byte ) BASE64_CHARACTERS.indexOf ( ss[ i + 1 ] );
									z = getFirstKBits ( z, 6 );
									x = ( byte ) ( x | z );
									byte b = x;
									bytes.add ( b );
							 }
							 
						}
						else break;
				 }
				 if ( i % 4 == 3 )
				 {
						
				 }
			}
			byte bs[] = new byte[ bytes.size ( ) ];
			for ( int i = 0 ; i < bytes.size ( ) ; ++i ) bs[ i ] = bytes.get ( i );
			return bs;
	 }
	 
	 private static
	 byte getFirstKBits ( byte b,
												int k )
	 {
			
			byte a = 1;
			for ( int i = 0 ; i < k - 1 ; ++i )
				 a = ( byte ) ( ( a << 1 ) | 1 );
			byte x = ( byte ) ( b & a );
			return x;
	 }
	 
	 private static
	 byte getLastKBits ( byte b,
											 int k )
	 {
			
			byte x = ( byte ) ( b >> ( 8 - k ) );
			return getFirstKBits ( x, k );
	 }
	 
}
