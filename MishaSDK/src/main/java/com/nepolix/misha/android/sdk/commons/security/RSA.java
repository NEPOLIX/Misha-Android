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

import com.nepolix.misha.android.sdk.commons.Base64;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * @author Behrooz Shahriari
 * @since 11/22/16
 */


public
class RSA
{
	 

	 /**
		* Constructs a private key (RSA) from the given string
		*
		* @param key
		* 				PEM Private Key
		*
		* @return RSA Private Key
		*
		* @throws IOException
		* @throws GeneralSecurityException
		*/
	 public static
	 RSAPrivateKey getPrivateKeyFromString ( String key )
					 throws
					 IOException,
					 GeneralSecurityException
	 {
			
			String privateKeyPEM = key;
			
			// Remove the first and last lines
			privateKeyPEM = privateKeyPEM.replace ( "\n", "" );
			privateKeyPEM = privateKeyPEM.replace ( "-----BEGIN PRIVATE KEY-----", "" );
			privateKeyPEM = privateKeyPEM.replace ( "-----END PRIVATE KEY-----", "" );
			
			// Base64 decode data
			byte[] encoded = Base64.decodeBase64 ( privateKeyPEM );
			
			KeyFactory    kf      = KeyFactory.getInstance ( "RSA" );
			RSAPrivateKey privKey = ( RSAPrivateKey ) kf.generatePrivate ( new PKCS8EncodedKeySpec ( encoded ) );
			return privKey;
	 }
	 

	 /**
		* Constructs a public key (RSA) from the given string
		*
		* @param key
		* 				PEM Public Key
		*
		* @return RSA Public Key
		*
		* @throws IOException
		* @throws GeneralSecurityException
		*/
	 public static
	 RSAPublicKey getPublicKeyFromString ( String key )
					 throws
					 IOException,
					 GeneralSecurityException
	 {
			
			String publicKeyPEM = key;
			
			// Remove the first and last lines
			publicKeyPEM = publicKeyPEM.replace ( "\n", "" );
			publicKeyPEM = publicKeyPEM.replace ( "-----BEGIN PUBLIC KEY-----", "" );
			publicKeyPEM = publicKeyPEM.replace ( "-----END PUBLIC KEY-----", "" );
			System.out.println ( publicKeyPEM );
			
			// Base64 decode data
			byte[] encoded = Base64.decodeBase64 ( publicKeyPEM );
			
			KeyFactory   kf     = KeyFactory.getInstance ( "RSA" );
			RSAPublicKey pubKey = ( RSAPublicKey ) kf.generatePublic ( new X509EncodedKeySpec ( encoded ) );
			return pubKey;
	 }
	 

	 /**
		* @param privateKey
		* @param message
		*
		* @return
		*
		* @throws NoSuchAlgorithmException
		* @throws InvalidKeyException
		* @throws SignatureException
		* @throws UnsupportedEncodingException
		*/
	 public static
	 String sign ( PrivateKey privateKey,
								 String message )
					 throws
					 NoSuchAlgorithmException,
					 InvalidKeyException,
					 SignatureException,
					 
					 UnsupportedEncodingException
	 {
			
			Signature sign = Signature.getInstance ( "SHA1withRSA" );
			sign.initSign ( privateKey );
			sign.update ( message.getBytes ( ) );
			return Base64.toBase64 ( sign.sign ( ) );
	 }
	 
	 /**
		* @param publicKey
		* @param message
		* @param signature
		*
		* @return
		*
		* @throws SignatureException
		* @throws NoSuchAlgorithmException
		* @throws UnsupportedEncodingException
		* @throws InvalidKeyException
		*/
	 public static
	 boolean verify ( PublicKey publicKey,
										String message,
										String signature )
					 throws
					 SignatureException,
					 NoSuchAlgorithmException,
					 UnsupportedEncodingException,
					 InvalidKeyException
	 {
			
			Signature sign = Signature.getInstance ( "SHA1withRSA" );
			sign.initVerify ( publicKey );
			sign.update ( message.getBytes ( ) );
			return sign.verify ( Base64.decodeBase64 ( signature ) );
	 }
	 
	 /**
		* Encrypts the text with the public key (RSA)
		*
		* @param rawText
		* 				Text to be encrypted
		* @param publicKey
		*
		* @return
		*
		* @throws IOException
		* @throws GeneralSecurityException
		*/
	 public static
	 String encrypt ( String rawText,
										PublicKey publicKey )
					 throws
					 IOException,
					 GeneralSecurityException
	 {
			
			Cipher cipher = Cipher.getInstance ( "RSA" );
			cipher.init ( Cipher.ENCRYPT_MODE, publicKey );
			return Base64.toBase64 ( cipher.doFinal ( rawText.getBytes ( "UTF-8" ) ) );
	 }
	 

	 /**
		* Decrypts the text with the private key (RSA)
		*
		* @param cipherText
		* 				Text to be decrypted
		* @param privateKey
		*
		* @return Decrypted text (Base64 encoded)
		*
		* @throws IOException
		* @throws GeneralSecurityException
		*/
	 public static
	 String decrypt ( String cipherText,
										PrivateKey privateKey )
					 throws
					 IOException,
					 GeneralSecurityException
	 {
			
			Cipher cipher = Cipher.getInstance ( "RSA" );
			cipher.init ( Cipher.DECRYPT_MODE, privateKey );
			return new String ( cipher.doFinal ( Base64.decodeBase64 ( cipherText ) ), "UTF-8" );
	 }
	 

	 /**
		* @return publicKey - privateKey
		*
		* @throws NoSuchAlgorithmException
		*/
	 public static
	 String[] generateRSAKeys ( )
					 throws
					 NoSuchAlgorithmException
	 {
			
			KeyPair keyPair = generateRSAPairKey ( );
			byte[] publicKeyBytes = keyPair.getPublic ( )
																		 .getEncoded ( );
			byte privateKeyBytes[] = keyPair.getPrivate ( )
																			.getEncoded ( );
			System.out.println ( Base64.toBase64 ( publicKeyBytes ) );
			System.out.println ( Base64.toBase64 ( privateKeyBytes ) );
			return new String[] {
							Base64.toBase64 ( publicKeyBytes ) ,
							Base64.toBase64 ( privateKeyBytes )
			};
	 }
	 
	 
	 /**
		* @return publicKey - privateKey
		*
		* @throws NoSuchAlgorithmException
		*/
	 public static
	 KeyPair generateRSAPairKey ( )
					 throws
					 NoSuchAlgorithmException
	 {
			
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance ( "RSA" );
			keyPairGenerator.initialize ( 1024 * 8 );
			return keyPairGenerator.genKeyPair ( );
	 }
}
