package com.jd.blockchain.utils.http.agent;

import java.beans.PropertyDescriptor;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.TypeDescriptor;

import com.jd.blockchain.utils.http.NamedParamMap;
import com.jd.blockchain.utils.http.PropertiesConverter;
import com.jd.blockchain.utils.http.RequestParam;

/**
 * 对 POJO 属性的转换器；
 * 
 * @author haiq
 *
 */
public class PojoPropertiesConverter implements PropertiesConverter {

	private List<String> propNames = new LinkedList<String>();

	private RequestParamResolver paramResolver;

	private Class<?> argType;

	public PojoPropertiesConverter(Class<?> argType) {
		this.argType = argType;
		resolveParamProperties();
	}

	private void resolveParamProperties() {
		BeanWrapperImpl beanWrapper = new BeanWrapperImpl(argType);
		List<ArgDefEntry<RequestParamDefinition>> reqParamDefs = new LinkedList<ArgDefEntry<RequestParamDefinition>>();
		PropertyDescriptor[] propDescs = beanWrapper.getPropertyDescriptors();
		TypeDescriptor propTypeDesc;
		for (PropertyDescriptor propDesc : propDescs) {
			propTypeDesc = beanWrapper.getPropertyTypeDescriptor(propDesc.getName());

			RequestParam reqParamAnno = propTypeDesc.getAnnotation(RequestParam.class);
			if (reqParamAnno == null) {
				// 忽略未标注 RequestParam 的属性；
				continue;
			}
			RequestParamDefinition reqParamDef = RequestParamDefinition.resolveDefinition(reqParamAnno);
			ArgDefEntry<RequestParamDefinition> defEntry = new ArgDefEntry<RequestParamDefinition>(reqParamDefs.size(), propTypeDesc.getType(),
					reqParamDef);
			reqParamDefs.add(defEntry);
			propNames.add(propDesc.getName());
		}
		paramResolver = RequestParamResolvers.createParamResolver(reqParamDefs);
	}

	@Override
	public NamedParamMap toProperties(Object arg) {
		if (propNames.size() == 0) {
			return new NamedParamMap();
		}
		BeanWrapper beanWrapper = new BeanWrapperImpl(arg);
		Object[] propValues = new Object[propNames.size()];
		int i = 0;
		for (String propName : propNames) {
			propValues[i] = beanWrapper.getPropertyValue(propName);
			i++;
		}
		NamedParamMap params = paramResolver.resolve(propValues);
		return params;
	}

}
