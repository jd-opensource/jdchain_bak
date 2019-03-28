package com.jd.blockchain.utils.http.agent;

import java.io.IOException;
import java.io.OutputStream;

import com.jd.blockchain.utils.http.HttpServiceException;
import com.jd.blockchain.utils.io.RuntimeIOException;

class RequestBodyResolvers {

	public static final RequestBodyResolver NULL_BODY_RESOLVER = new NullBodyResolver();

	/**
	 * 创建基于参数列表的解析器；
	 * 
	 * @param argIndex
	 *            要作为 body 输出的参数的位置；
	 * @param converter
	 *            参数值转换器；
	 * @return
	 */
	public static RequestBodyResolver createArgumentResolver(ArgDefEntry<RequestBodyDefinition> defEntry) {
		return new ArgurmentResolver(defEntry);
	}
	
	private static final class ArgurmentResolver implements RequestBodyResolver {

		private ArgDefEntry<RequestBodyDefinition> defEntry;

		public ArgurmentResolver(ArgDefEntry<RequestBodyDefinition> defEntry) {
			this.defEntry = defEntry;
		}

		@Override
		public void resolve(Object[] args, OutputStream out) {
			Object arg = args[defEntry.getIndex()];
			if (arg == null && defEntry.getDefinition().isRequired()) {
				throw new HttpServiceException("The required body argument is null!");
			}
			try {
				defEntry.getDefinition().getConverter().write(arg, out);
			} catch (IOException e) {
				throw new RuntimeIOException(e.getMessage(), e);
			}
		}

	}

	/**
	 * 空的 body 解析器；
	 * 
	 * @author haiq
	 *
	 */
	private static final class NullBodyResolver implements RequestBodyResolver {

		@Override
		public void resolve(Object[] args, OutputStream out) {
			//Do nothing;
		}

	}
}
