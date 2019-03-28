package com.jd.blockchain.utils.test;

import static org.junit.Assert.*;

import org.hamcrest.CustomMatcher;

import com.jd.blockchain.utils.serialize.json.JSONSerializeUtils;

public class JunitAssertMatcher  {
	
	public static <T> CustomMatcher<T> assertEqualsMatch(final T expected){
		return new CustomMatcher<T>("expected arg equals the value [" + expected + "].") {
			@Override
			public boolean matches(Object item) {
				assertEquals(expected, item);
				return true;
			}
		};
	}
	
	public static <T> CustomMatcher<T> assertJsonEqualMatch(final T expected){
		return new CustomMatcher<T>("expected arg equals the value [" + expected + "].") {
			@Override
			public boolean matches(Object item) {
				assertNotNull(item);
				String actualJSON = JSONSerializeUtils.serializeToJSON(item);
				String expectedJSON = JSONSerializeUtils.serializeToJSON(expected);
				assertEquals(expectedJSON, actualJSON);
				return true;
			}
		};
	}

}
