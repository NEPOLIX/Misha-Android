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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Behrooz Shahriari
 * @since 1/5/17
 */
class AndStatement
				implements Statement
{
	 
	 List< Statement > statementsAND;
	 
	 public
	 AndStatement ( )
	 {
			
			statementsAND = new ArrayList<> ( );
	 }
	 
	 public
	 List< Statement > getStatementsAND ( )
	 {
			
			return statementsAND;
	 }
	 
	 public
	 void setStatementsAND ( List< Statement > statementsAND )
	 {
			
			this.statementsAND = statementsAND;
	 }
	 
	 @Override
	 public
	 void addStatement ( Statement statement )
	 {
			
			statementsAND.add ( statement );
	 }
	 
	 @Override
	 public
	 String toSQLStatement ( )
	 {
			
			StringBuilder builder = new StringBuilder ( );
			for ( int i = 0 ; i < statementsAND.size ( ) ; ++i )
			{
				 Statement statement = statementsAND.get ( i );
				 builder.append ( " (" )
								.append ( statement.toSQLStatement ( ) )
								.append ( ") " );
				 if ( i != statementsAND.size ( ) - 1 ) builder.append ( " AND " );
			}
			
			return builder.toString ( );
	 }
}
