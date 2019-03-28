package com.jd.blockchain.utils.http.agent;

import com.jd.blockchain.utils.http.HttpMethod;
import com.jd.blockchain.utils.http.NamedParamMap;
import com.jd.blockchain.utils.http.RequestParamFilter;

public class NullRequestParamFilter implements RequestParamFilter{
	
	public static RequestParamFilter INSTANCE = new NullRequestParamFilter();
	
	private NullRequestParamFilter() {
	}

	@Override
	public void filter(HttpMethod requestMethod, NamedParamMap requestParams) {
	}

}
