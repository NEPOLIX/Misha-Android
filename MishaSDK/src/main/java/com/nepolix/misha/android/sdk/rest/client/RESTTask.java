/******************************************************************************
 * Copyright © 2015-7532 NEX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights reserved. * * The source
 * code, other & all material, and documentation               * contained herein are, and remains the property of HEX
 * Inc.             * and its suppliers, if any. The intellectual and technical * concepts contained herein are
 * proprietary to HEX Inc. and its          * suppliers and may be covered by U.S. and Foreign Patents, patents      *
 * in process, and are protected by trade secret or copyright law.        * Dissemination of the foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.nepolix.misha.android.sdk.rest.client;


import com.nepolix.misha.android.sdk.json.JSONObject;
import com.nepolix.misha.android.sdk.task.handler.core.task.callback.Callback;
import com.nepolix.misha.android.sdk.task.handler.core.task.callback.CallbackTask;

/**
 * @author Behrooz Shahriari
 * @since 10/10/16
 */
abstract
class RESTTask
				extends CallbackTask< JSONObject >
{
	 
	 RESTTask ( Callback< JSONObject > callback )
	 {
			
			super ( callback );
	 }
	 
}
