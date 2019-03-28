package com.jd.blockchain.utils.http.agent;

import java.util.LinkedList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.jd.blockchain.utils.http.HttpServiceException;
import com.jd.blockchain.utils.http.NamedParamMap;
import com.jd.blockchain.utils.http.StringConverter;

/**
 * 请求参数解析器；
 * 
 * @author haiq
 *
 */
abstract class RequestParamResolvers {

	public static final RequestParamResolver NONE_REQUEST_PARAM_RESOLVER = new NoneRequestParamResolver();

	/**
	 * 创建解析器；
	 * 
	 * @param definitions
	 *            方法参数定义；
	 * @return
	 */
	public static RequestParamResolver createParamMapResolver(
			List<ArgDefEntry<RequestParamDefinition>> reqParamDefinitions,
			List<ArgDefEntry<RequestParamMapDefinition>> reqParamMapDefinitions) {
		if ((!CollectionUtils.isEmpty(reqParamDefinitions)) && (!CollectionUtils.isEmpty(reqParamMapDefinitions))) {
			RequestParamResolver resolver1 = createParamResolver(reqParamDefinitions);
			RequestParamResolver resolver2 = createParamMapResolver(reqParamMapDefinitions);
			return new MultiRequestParamResolverWrapper(resolver1, resolver2);
		}
		if (!CollectionUtils.isEmpty(reqParamDefinitions)) {
			return createParamResolver(reqParamDefinitions);
		}
		if (!CollectionUtils.isEmpty(reqParamMapDefinitions)) {
			return createParamMapResolver(reqParamMapDefinitions);
		}
		return NONE_REQUEST_PARAM_RESOLVER;
	}

	/**
	 * 创建解析器；
	 * 
	 * @param definitions
	 *            方法参数定义；
	 * @return
	 */
	public static RequestParamResolver createParamMapResolver(
			List<ArgDefEntry<RequestParamMapDefinition>> definitions) {
		return new ArgArrayRequestParamMapResolver(definitions);
	}

	/**
	 * 创建解析器；
	 * 
	 * @param definitions
	 *            方法参数定义；
	 * @return
	 */
	public static RequestParamResolver createParamResolver(List<ArgDefEntry<RequestParamDefinition>> definitions) {
		return new ArgArrayRequestParamResolver(definitions);
	}

	/**
	 * 方法参数表解析器；
	 * 
	 * @author haiq
	 *
	 */
	private static class ArgArrayRequestParamMapResolver implements RequestParamResolver {

		private List<ArgDefEntry<RequestParamMapDefinition>> definitions;

		/**
		 * @param definitions
		 */
		public ArgArrayRequestParamMapResolver(List<ArgDefEntry<RequestParamMapDefinition>> definitions) {
			this.definitions = new LinkedList<ArgDefEntry<RequestParamMapDefinition>>(definitions);
		}

		@Override
		public NamedParamMap resolve(Object[] args) {
			NamedParamMap params = new NamedParamMap();
			for (ArgDefEntry<RequestParamMapDefinition> defEntry : definitions) {
				RequestParamMapDefinition def = defEntry.getDefinition();
				Object argValue = args[defEntry.getIndex()];
				if (argValue == null && def.isRequired()) {
					throw new HttpServiceException("The required argument object is null!");
				}

				NamedParamMap extParams = def.getConverter().toProperties(argValue);
				if (extParams.isEmpty()) {
					if (def.isRequired()) {
						throw new HttpServiceException("The required request parameter map is empty!");
					}
					// 非必需参数，忽略空值;
					continue;
				}
				if (extParams != null) {
					// 合并参数；
					params.merge(extParams, def.getPrefix());
				}
			} // End of for;
			return params;
		}

	}

	/**
	 * 方法参数解析器；
	 * 
	 * @author haiq
	 *
	 */
	private static class ArgArrayRequestParamResolver implements RequestParamResolver {

		private List<ArgDefEntry<RequestParamDefinition>> paramDefinitions;

		/**
		 * @param paramDefinitions
		 */
		public ArgArrayRequestParamResolver(List<ArgDefEntry<RequestParamDefinition>> paramDefinitions) {
			this.paramDefinitions = new LinkedList<ArgDefEntry<RequestParamDefinition>>(paramDefinitions);
		}

		@Override
		public NamedParamMap resolve(Object[] args) {
			NamedParamMap params = new NamedParamMap();
			for (ArgDefEntry<RequestParamDefinition> defEntry : paramDefinitions) {
				RequestParamDefinition def = defEntry.getDefinition();
				Object arg = args[defEntry.getIndex()];
				if (arg == null && def.isRequired()) {
					throw new HttpServiceException("The required argument object is null!");
				}

				resovleParams(params, def, arg);
			} // End of for;
			return params;
		}

		private void resovleParams(NamedParamMap params, RequestParamDefinition def, Object arg) {
			if (def.isArray() && arg != null) {
				if (arg.getClass().isArray()) {
					Object[] valObjs = (Object[]) arg;
					for (Object val : valObjs) {
						resovleParamValue(params, def, val);
					}
				}
			}else {
				resovleParamValue(params, def, arg);
			}
		}

		private void resovleParamValue(NamedParamMap params, RequestParamDefinition def, Object arg) {
			String value = def.getConverter().toString(arg);
			if (value == null) {
				if (def.isRequired()) {
					throw new HttpServiceException("The required argument value is null!");
				}
				// not required, and ignore null value;
				return;
			}
			if (value.equals(def.getIgnoreValue())) {
				// ignore ;
				return;
			}
			params.addParam(def.getName(), value);
		}

	}

	/**
	 * 将多个请求参数解析器的解析结果组合在一起的包装器；
	 * 
	 * @author haiq
	 *
	 */
	private static class MultiRequestParamResolverWrapper implements RequestParamResolver {

		private RequestParamResolver[] resolvers;

		public MultiRequestParamResolverWrapper(RequestParamResolver... resolvers) {
			this.resolvers = resolvers;
		}

		@Override
		public NamedParamMap resolve(Object[] args) {
			NamedParamMap params = new NamedParamMap();
			for (RequestParamResolver resolver : resolvers) {
				NamedParamMap extParams = resolver.resolve(args);
				params.merge(extParams);
			}
			return params;
		}

	}

	/**
	 * 空的请求参数解析器；
	 * 
	 * 总是返回空的请求参数；
	 * 
	 * @author haiq
	 *
	 */
	private static class NoneRequestParamResolver implements RequestParamResolver {

		@Override
		public NamedParamMap resolve(Object[] args) {
			return new NamedParamMap();
		}

	}

}
