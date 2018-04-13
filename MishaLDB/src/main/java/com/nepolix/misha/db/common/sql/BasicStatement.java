/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to NOX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.nepolix.misha.db.common.sql;

/**
 * @author Behrooz Shahriari
 * @since 1/5/17
 */
class BasicStatement
				implements Statement
{
	 
	 private String sqlStatement;
	 
	 public
	 BasicStatement ( )
	 {
			
	 }
	 
	 public
	 BasicStatement ( String sqlStatement )
	 {
			
			this.sqlStatement = sqlStatement;
	 }
	 
	 public
	 String getSqlStatement ( )
	 {
			
			return sqlStatement;
	 }
	 
	 public
	 void setSqlStatement ( String sqlStatement )
	 {
			
			this.sqlStatement = sqlStatement;
	 }
	 
	 @Override
	 public
	 void addStatement ( Statement statement )
	 {
			
			sqlStatement = statement.toSQLStatement ( );
	 }
	 
	 @Override
	 public
	 String toSQLStatement ( )
	 {
			
			return sqlStatement;
	 }
}
