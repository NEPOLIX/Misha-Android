/******************************************************************************
 * Copyright © 2015-7532 NEX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights reserved. * * The source
 * code, other & all material, and documentation               * contained herein are, and remains the property of HEX
 * Inc.             * and its suppliers, if any. The intellectual and technical * concepts contained herein are
 * proprietary to HEX Inc. and its          * suppliers and may be covered by U.S. and Foreign Patents, patents      *
 * in process, and are protected by trade secret or copyright law.        * Dissemination of the foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.nepolix.misha.android.sdk.task.handler.core.task.callback.condition;


import com.nepolix.misha.android.sdk.json.JSONObject;
import com.nepolix.misha.android.sdk.task.handler.core.task.TaskType;
import com.nepolix.misha.android.sdk.task.handler.core.task.callback.Callback;
import com.nepolix.misha.android.sdk.task.handler.core.task.callback.CallbackTask;

/**
 * @author Behrooz Shahriari
 * @since 10/31/16
 */
public abstract
class ConditionalTask < R >
				extends CallbackTask
{
	 
	 protected TaskCondition taskCondition;
	 
	 protected Callback< R > finalCallback;
	 
	 
	 public
	 ConditionalTask ( Callback< R > callback,
										 final Callback< R > finalCallback )
	 {
			
			super ( callback );
			this.finalCallback = finalCallback;
	 }
	 
	 public
	 ConditionalTask ( TaskType taskType,
										 Callback< R > callback,
										 final Callback< R > finalCallback )
	 {
			
			super ( taskType, callback );
			this.finalCallback = finalCallback;
	 }
	 
	 public
	 ConditionalTask ( TaskType taskType,
										 int repetition,
										 Callback< R > callback,
										 final Callback< R > finalCallback )
	 {
			
			super ( taskType, repetition, callback );
			this.finalCallback = finalCallback;
	 }
	 
	 
	 public abstract
	 TaskCondition taskCondition ( );
	 
	 public
	 void setTaskCondition ( TaskCondition taskCondition )
	 {
			
			this.taskCondition = taskCondition;
	 }
	 
	 
	 public
	 void runFinalCallback ( R result,
													 JSONObject error )
	 {
			
			if ( finalCallback != null ) finalCallback.onResult ( result );
			if ( finalCallback != null && error != null ) finalCallback.onError ( error );
	 }
}
