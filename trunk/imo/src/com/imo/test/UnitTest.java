package com.imo.test;

import junit.framework.Assert;
import android.test.InstrumentationTestCase;
import android.util.Log;

public class UnitTest extends InstrumentationTestCase {
	private static final String Tag = "MyTest";

	public void testSave() throws Throwable {
		Log.v(Tag, "test the testSave");
		
		int i = 4 + 8;
		Assert.assertEquals(5, i);
	}

	public void testSomethingElse() throws Throwable {
		Log.v(Tag, "test the testSomethingElse");
		
		int i = 1+1;
		Assert.assertEquals(2, i);
	}
}
