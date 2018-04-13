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

package com.nepolix.misha.android.sdk.commons.security;


import com.nepolix.misha.android.sdk.commons.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

import static com.nepolix.misha.android.sdk.commons.Base64.decodeBase64;
import static com.nepolix.misha.android.sdk.commons.Base64.toBase64;

/**
 * @author Behrooz Shahriari
 * @since 11/22/16
 */
public final
class AES
{
	 
	 private final static String GLOBAL_AES_KEY = null;
	 
	 public static
	 String getGlobalAEAKey ( )
	 {
			
			return GLOBAL_AES_KEY;
	 }
	 
	 public static
	 String encrypt ( String text ,
										String key )
	 {
			
			byte[] encrypted = null;
			try
			{
				 Key    aesKey = new SecretKeySpec ( key.getBytes ( ) , "AES" );
				 Cipher cipher = Cipher.getInstance ( "AES" );
				 // encrypt the text
				 cipher.init ( Cipher.ENCRYPT_MODE , aesKey );
				 encrypted = cipher.doFinal ( text.getBytes ( ) );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
			return toBase64 ( encrypted );
	 }
	 
	 public static
	 String decrypt ( String encrypted ,
										String key )
	 {
			
			String decrypted = null;
			try
			{
				 byte[] bytes  = decodeBase64 ( encrypted );
				 Key    aesKey = new SecretKeySpec ( key.getBytes ( ) , "AES" );
				 Cipher cipher = Cipher.getInstance ( "AES" );
				 cipher.init ( Cipher.DECRYPT_MODE , aesKey );
				 decrypted = new String ( cipher.doFinal ( bytes ) );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
			return decrypted;
	 }
	 
	 public static
	 String createRandomAESKey ( )
	 {
			
			return Utils.generateRandomString ( 16 );
	 }
}
