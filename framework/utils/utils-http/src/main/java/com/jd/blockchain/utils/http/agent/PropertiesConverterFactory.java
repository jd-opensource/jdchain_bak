package com.jd.blockchain.utils.http.agent;

import org.springframework.beans.BeanUtils;

import com.jd.blockchain.utils.http.PropertiesConverter;
import com.jd.blockchain.utils.http.StringConverter;

public class PropertiesConverterFactory {
	
	public static PropertiesConverter instantiatePropertiesConverter(Class<?> converterClazz, Class<?> argType){
		if (converterClazz == null || PropertiesConverter.class == converterClazz || PojoPropertiesConverter.class == converterClazz) {
			return new PojoPropertiesConverter(argType);
		}
		if (!PropertiesConverter.class.isAssignableFrom(converterClazz)) {
			throw new IllegalHttpServiceDefinitionException(
					"The specified converter of path param doesn't implement the interface "
							+ StringConverter.class.getName() + "!");
		}
		
		PropertiesConverter converter = (PropertiesConverter) BeanUtils.instantiate(converterClazz);
		return converter;
	}
	
}
