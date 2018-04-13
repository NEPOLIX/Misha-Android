/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to HEX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.nepolix.misha.android.sdk.rest.client;

/**
 * @author Behrooz Shahriari
 * @since 12/2/16
 */
public
class CUrlParameter
{
	 
	 private String flag;
	 
	 private String parameter;
	 
	 public
	 CUrlParameter ( String flag,
									 String parameter )
	 {
			
			this.flag = flag;
			this.parameter = parameter;
	 }
	 
	 public
	 String getFlag ( )
	 {
			
			return flag;
	 }
	 
	 public
	 void setFlag ( String flag )
	 {
			
			this.flag = flag;
	 }
	 
	 public
	 String getParameter ( )
	 {
			
			return parameter;
	 }
	 
	 public
	 void setParameter ( String parameter )
	 {
			
			this.parameter = parameter;
	 }
}
