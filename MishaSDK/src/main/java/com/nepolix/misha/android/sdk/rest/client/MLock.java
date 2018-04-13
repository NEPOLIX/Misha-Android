/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights reserved. * * The source
 * code, other & all material, and documentation               * contained herein are, and remains the property of HEX
 * Inc.             * and its suppliers, if any. The intellectual and technical * concepts contained herein are
 * proprietary to NOX Inc. and its          * suppliers and may be covered by U.S. and Foreign Patents, patents      *
 * in process, and are protected by trade secret or copyright law.        * Dissemination of the foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.nepolix.misha.android.sdk.rest.client;

/**
 * @author Behrooz Shahriari
 * @since 11/20/16
 */
class MLock
{
	 
	 private boolean flag = false;
	 
	 private final Object LOCK = new Object ( );
	 
	 public
	 MLock ( )
	 {
			
	 }
	 
	 public
	 boolean getFlag ( )
	 {
			
			return flag;
	 }
	 
	 public
	 void setFlag ( boolean flag )
	 {
			
			this.flag = flag;
	 }
	 
	 public
	 void hold ( )
	 {
			
			if ( !flag )
			{
				 synchronized ( LOCK )
				 {
						try
						{
							 LOCK.wait ( );
						}
						catch ( InterruptedException e )
						{
							 e.printStackTrace ( );
						}
				 }
			}
	 }
	 
	 public
	 void notifyLock ( )
	 {
			
			synchronized ( LOCK )
			{
				 setFlag ( true );
				 LOCK.notify ( );
			}
	 }
}
