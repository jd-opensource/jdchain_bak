package com.jd.blockchain.utils.http.agent;

import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

class RequestBodyResolverComposite implements RequestBodyResolver {

	private List<RequestBodyResolver> resolverList = new LinkedList<RequestBodyResolver>();

	@Override
	public void resolve(Object[] args, OutputStream out) {
		for (RequestBodyResolver resolver : resolverList) {
			resolver.resolve(args, out);
		}
	}

	public void addRequestBodyResolver(RequestBodyResolver resolver) {
		resolverList.add(resolver);
	}
}
