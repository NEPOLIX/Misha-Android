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

package com.nepolix.misha.android.sdk.task.handler.core.task.callback;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.nepolix.misha.android.sdk.json.JSONObject;

/**
 * @author Behrooz Shahriari
 * @since 3/9/17
 */

public abstract
class AsyncCallback < R, T, E >
				implements Callback< R >
{
	 
	 protected Context context;
	 
	 public
	 AsyncCallback ( )
	 {
			
	 }
	 
	 public
	 AsyncCallback ( Context context )
	 {
			
			this.context = context;
	 }
	 
	 public
	 Context getContext ( )
	 {
			
			return context;
	 }
	 
	 @Override
	 public
	 void onResult ( R result )
	 {
			
			T       t       = onResultBackground ( result );
			Handler handler = context == null ? new Handler ( Looper.getMainLooper ( ) ) : new Handler ( context.getMainLooper ( ) );
			handler.post ( new Runnable ( )
			{
				 
				 @Override
				 public
				 void run ( )
				 {
						
						onResultUI ( t );
				 }
			} );
	 }
	 
	 protected abstract
	 T onResultBackground ( R result );
	 
	 protected abstract
	 void onResultUI ( T t );
	 
	 @Override
	 public
	 void onError ( JSONObject e )
	 {
			
			E       er      = onErrorBackground ( e );
			Handler handler = context == null ? new Handler ( Looper.getMainLooper ( ) ) : new Handler ( context.getMainLooper ( ) );
			handler.post ( new Runnable ( )
			{
				 
				 @Override
				 public
				 void run ( )
				 {
						
						onErrorUI ( er );
				 }
			} );
	 }
	 
	 protected abstract
	 E onErrorBackground ( JSONObject result );
	 
	 protected abstract
	 void onErrorUI ( E e );
}
