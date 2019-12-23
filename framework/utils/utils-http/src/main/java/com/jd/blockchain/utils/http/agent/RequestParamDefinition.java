package com.jd.blockchain.utils.http.agent;

import java.util.LinkedList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.jd.blockchain.utils.http.RequestParam;
import com.jd.blockchain.utils.http.StringConverter;

class RequestParamDefinition {

	private String name;

	private boolean required;

	private boolean array;

	private String ignoreValue;

	private StringConverter converter;

	public RequestParamDefinition(String name, boolean required, boolean array, String ignoreValue,
			StringConverter converter) {
		this.name = name;
		this.required = required;
		this.array = array;
		this.ignoreValue = ignoreValue;
		this.converter = converter;
	}

	public String getName() {
		return name;
	}

	public boolean isRequired() {
		return required;
	}
	
	public boolean isArray() {
		return array;
	}

	public String getIgnoreValue() {
		return ignoreValue;
	}

	public StringConverter getConverter() {
		return converter;
	}

	public static List<ArgDefEntry<RequestParamDefinition>> resolveSingleParamDefinitions(
			List<ArgDefEntry<RequestParam>> reqParamAnnos) {
		List<ArgDefEntry<RequestParamDefinition>> reqDefs = new LinkedList<ArgDefEntry<RequestParamDefinition>>();
		for (ArgDefEntry<RequestParam> entry : reqParamAnnos) {
			RequestParam reqParamAnno = entry.getDefinition();
			RequestParamDefinition reqDef = resolveDefinition(reqParamAnno);
			reqDefs.add(new ArgDefEntry<RequestParamDefinition>(entry.getIndex(), entry.getArgType(), reqDef));
		}
		return reqDefs;
	}

	public static RequestParamDefinition resolveDefinition(RequestParam reqParamAnno) {
		if (StringUtils.isEmpty(reqParamAnno.name())) {
			throw new IllegalHttpServiceDefinitionException("The name of request parameter is empty!");
		}

		Class<?> converterClazz = reqParamAnno.converter();
		StringConverter converter = StringConverterFactory.instantiateStringConverter(converterClazz);
		RequestParamDefinition reqDef = new RequestParamDefinition(reqParamAnno.name(), reqParamAnno.required(),
				reqParamAnno.array(), reqParamAnno.ignoreValue(), converter);
		return reqDef;
	}
}
