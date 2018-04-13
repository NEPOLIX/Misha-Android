/******************************************************************************
 * Copyright © 2015-7532 NEX, Inc. [NEPOLIX]-(Behrooz Shahriari)              * All rights reserved. * * The source
 * code, other & all material, and documentation               * contained herein are, and remains the property of HEX
 * Inc.             * and its suppliers, if any. The intellectual and technical * concepts contained herein are
 * proprietary to HEX Inc. and its          * suppliers and may be covered by U.S. and Foreign Patents, patents      *
 * in process, and are protected by trade secret or copyright law.        * Dissemination of the foregoing material or
 * reproduction of this        * material is strictly forbidden forever. *
 ******************************************************************************/

package com.nepolix.misha.android.sdk.task.scheduler;

/**
 * @author Behrooz Shahriari
 * @since 11/1/16
 */
public abstract
class Scheduler
{
	 
	 public final static int DEFAULT_SCHEDULER_WORKER_SIZE = 1;
	 
	 public static
	 Scheduler getScheduler ( int numberWorkers )
	 {
			
			return SchedulerImp.getScheduler_ ( numberWorkers );
	 }
	 
	 public abstract
	 Scheduler schedule ( ScheduleTask task );

	 public abstract
	 void terminate ( );
}
