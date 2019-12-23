package test.my.utils.http.agent;

import com.jd.blockchain.utils.http.StringConverter;

public class CustomBooleanConverter implements StringConverter {

	@Override
	public String toString(Object obj) {
		Boolean value = (Boolean) obj;
		return value.booleanValue() ? "1" : "0";
	}

}
