package com.jd.blockchain.utils.http.agent;

import java.util.LinkedList;
import java.util.List;

import org.springframework.util.StringUtils;

import com.jd.blockchain.utils.http.PropertiesConverter;
import com.jd.blockchain.utils.http.RequestParamMap;

class RequestParamMapDefinition {
	
	private String prefix;
	
	private boolean required;
	
	private PropertiesConverter converter;
	
	public RequestParamMapDefinition(String prefix, String seperator, boolean required, PropertiesConverter converter) {
		if (prefix == null || prefix.length() ==0) {
			this.prefix = "";
		}else{
			this.prefix = prefix + seperator;
		}
		this.required = required;
		this.converter = converter;
	}

	public boolean isRequired() {
		return required;
	}

	public PropertiesConverter getConverter() {
		return converter;
	}

	public String getPrefix() {
		return prefix;
	}
	
	
	public static List<ArgDefEntry<RequestParamMapDefinition>>  resolveParamMapDefinitions(List<ArgDefEntry<RequestParamMap>> reqParamAnnos){
		List<ArgDefEntry<RequestParamMapDefinition>> reqDefs = new LinkedList<ArgDefEntry<RequestParamMapDefinition>>();
		for (ArgDefEntry<RequestParamMap> entry : reqParamAnnos) {
			RequestParamMap reqParamAnno = entry.getDefinition();
			String prefix = StringUtils.trimWhitespace(reqParamAnno.prefix());
			String seperator = StringUtils.trimWhitespace(reqParamAnno.seperator());
			
			Class<?> converterClazz = reqParamAnno.converter();
			PropertiesConverter converter = PropertiesConverterFactory.instantiatePropertiesConverter(converterClazz, entry.getArgType());
			RequestParamMapDefinition reqDef = new RequestParamMapDefinition(prefix, seperator, reqParamAnno.required(),converter);
			reqDefs.add(new ArgDefEntry<RequestParamMapDefinition>(entry.getIndex(), entry.getArgType(), reqDef));
		}
		return reqDefs;
	}
	
}
