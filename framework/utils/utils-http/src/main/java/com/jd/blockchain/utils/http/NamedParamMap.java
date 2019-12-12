package com.jd.blockchain.utils.http;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NamedParamMap {

	private Map<String, List<NamedParam>> params = new LinkedHashMap<>();

	public NamedParam[] getParams() {
		List<NamedParam> allParams = new ArrayList<>();
		for (List<NamedParam> namedParams : params.values()) {
			allParams.addAll(namedParams);
		}
		return allParams.toArray(new NamedParam[allParams.size()]);
	}

	public NamedParam addParam(String name, String value) {
		List<NamedParam> values = params.get(name);
		if (values == null) {
			values = new ArrayList<>();
			params.put(name, values);
		}
		NamedParam p = new NamedParam(name, value);
		values.add(p);
		return p;
	}

	public boolean isEmpty() {
		return params.isEmpty();
	}
	
	/**
	 * 合并；
	 * @param extParams
	 * @param prefix 附加的新参数的前缀；
	 */
	public void merge(NamedParamMap extParams) {
		merge(extParams, null);
	}

	/**
	 * 合并；
	 * @param extParams
	 * @param prefix 附加的新参数的前缀；
	 */
	public void merge(NamedParamMap extParams, String prefix) {
		if (prefix == null || prefix.length() == 0) {
			NamedParam[] paramArr = extParams.getParams();
			for (NamedParam np : paramArr) {
				addParam(np.getName(), np.getValue());
			}
		} else {
			NamedParam[] paramArr = extParams.getParams();
			for (NamedParam np : paramArr) {
				addParam(prefix + np.getName(), np.getValue());
			}
		}
	}
}
