package com.nepolix.misha.db;

import com.nepolix.misha.db.core.MishaLDB;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public
class ExampleUnitTest
{

	 public static
	 void main ( String[] args )
	 {

			LinkedBlockingQueue queue    = new LinkedBlockingQueue ( );
			MishaLDB            mishaLDB = MishaLDB.getInstance ( null, null );
	 }
}