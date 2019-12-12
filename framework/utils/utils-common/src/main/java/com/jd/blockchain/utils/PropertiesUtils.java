package com.jd.blockchain.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * PropertiesUtils 定义了从 properties 文件到 pojo 对象的转换方法；
 * 
 * 用于对充当配置文件的 properties 文件到定义了配置信息的 POJO 的转换；
 * 
 * 支持 properties 的 key 到 POJO 的字段转换，支持层级的 key 的转换，例如： "user.name" 到 user 字段的对象的
 * name 属性；
 * 
 * @author haiq
 *
 */
public abstract class PropertiesUtils {

	private PropertiesUtils() {
	}

	/**
	 * 创建配置对象的实例，并且从指定的属性表中初始化对应的实例字段；
	 * 
	 * @param configClass configClass
	 * @param properties  properties
	 * @param <T>         T
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createInstance(Class<T> configClass, Properties properties) {
		BeanWrapper confBean = new BeanWrapperImpl(configClass);
		confBean.setAutoGrowNestedPaths(true);

		MutablePropertyValues values = new MutablePropertyValues(properties);
		confBean.setPropertyValues(values, true);

		return (T) confBean.getWrappedInstance();
	}

	/**
	 * 创建配置对象的实例，并且从指定的属性表中初始化对应的实例字段；
	 * 
	 * @param configClass 配置对象的类型；
	 * @param properties  属性表；
	 * @param propsPrefix 在属性表中与配置对象相关的属性的key的前缀；
	 * @param <T>         T
	 * @return T
	 */
	public static <T> T createInstance(Class<T> configClass, Properties properties, String propsPrefix) {
		if (propsPrefix == null || propsPrefix.trim().length() == 0) {
			return createInstance(configClass, properties);
		}
		propsPrefix = propsPrefix.trim();
		Properties configProperties = subset(properties, propsPrefix, true);
		return createInstance(configClass, configProperties);
	}

	/**
	 * 设置配置值；
	 * 
	 * @param obj          配置对象；配置值将设置到此对象匹配的属性；
	 * @param configValues 配置值；
	 * @param propPrefix   自动加入的属性前缀；
	 */
	public static void setValues(Object obj, Properties configValues, String propPrefix) {
		Properties values = new Properties();
		setValues(obj, values);
		mergeFrom(configValues, values, propPrefix);
	}

	/**
	 * 设置配置值；
	 * 
	 * @param obj          配置对象；配置值将设置到此对象匹配的属性；
	 * @param configValues 配置值；
	 */
	public static void setValues(Object obj, Properties configValues) {
		BeanWrapper confBean = new BeanWrapperImpl(obj);
		confBean.setAutoGrowNestedPaths(true);

		MutablePropertyValues values = new MutablePropertyValues(configValues);
		confBean.setPropertyValues(values, true);
	}

	/**
	 * 从指定的路径加载配置；
	 * 
	 * @param configClass           配置对象的类型；
	 * @param configFilePathPattern properties配置文件的路径；可以指定 spring 资源路径表达式；
	 * @param charset               字符集；
	 * @param <T>                   class
	 * @return T
	 * @throws IOException exception
	 */
	public static <T> T load(Class<T> configClass, String configFilePathPattern, String charset) throws IOException {
		Properties props = loadProperties(configFilePathPattern, charset);
		return createInstance(configClass, props);
	}

	/**
	 * 从指定的路径加载配置；
	 * 
	 * @param obj                   配置对象；配置文件的值将设置到此对象匹配的属性；
	 * @param configFilePathPattern properties配置文件的路径；可以指定 spring 资源路径表达式；
	 * @param charset               字符集；
	 * @throws IOException exception
	 */
	public static void load(Object obj, String configFilePathPattern, String charset) throws IOException {
		Properties props = loadProperties(configFilePathPattern, charset);
		setValues(obj, props);
	}

	public static Properties loadProperties(String configFilePathPattern, String charset) throws IOException {
		ResourcePatternResolver resResolver = new PathMatchingResourcePatternResolver();
		Resource configResource = resResolver.getResource(configFilePathPattern);
		InputStream in = configResource.getInputStream();
		try {
			return load(in, charset);
		} finally {
			in.close();
		}
	}

	public static Properties loadProperties(File configFile, String charset) throws IOException {
		FileSystemResource resource = new FileSystemResource(configFile);
		InputStream in = resource.getInputStream();
		try {
			return load(in, charset);
		} finally {
			in.close();
		}
	}

	public static Properties load(InputStream in, String charset) throws IOException {
		Properties props = new Properties();
		InputStreamReader reader = new InputStreamReader(in, charset);
		try {
			props.load(reader);
		} finally {
			reader.close();
		}
		return props;
	}

	public static Properties load(byte[] bytes, String charset) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		return load(in, charset);
	}

	/**
	 * 合并两个 properties ；
	 * 
	 * @param props 要将其它值合并进来的属性集合；操作将对其产生修改；
	 * @param from  属性值将要合并进入其它属性集合；操作不对其产生修改；
	 */
	public static void mergeFrom(Properties props, Properties from) {
		mergeFrom(props, from, null);
	}

	/**
	 * 合并两个 properties ；
	 * 
	 * @param props              要将其它值合并进来的属性集合；操作将对其产生修改；
	 * @param from               属性值将要合并进入其它属性集合；操作不对其产生修改；
	 * @param propertyNamePrefix 属性名称前缀；
	 */
	public static void mergeFrom(Properties props, Properties from, String propertyNamePrefix) {
		if (propertyNamePrefix == null || propertyNamePrefix.length() == 0) {
			for (String name : from.stringPropertyNames()) {
				props.setProperty(name, from.getProperty(name));
			}
		} else {
			for (String name : from.stringPropertyNames()) {
				props.setProperty(propertyNamePrefix + name, from.getProperty(name));
			}
		}
	}

	/**
	 * 获取指定 properties 中以指定的前缀开头的子集；
	 * 
	 * @param props              要抽取的属性集合；
	 * @param propertyNamePrefix 属性名称前缀；
	 * @param trimPrefix         是否在复制的新的属性集合去掉指定的前缀；
	 * @return properties
	 */
	public static Properties subset(Properties props, String propertyNamePrefix, boolean trimPrefix) {
		Properties subProperties = new Properties();
		Set<String> names = props.stringPropertyNames();
		String newName;
		for (String name : names) {
			if (name.startsWith(propertyNamePrefix)) {
				newName = name;
				if (trimPrefix) {
					newName = name.substring(propertyNamePrefix.length());
				}
				subProperties.setProperty(newName, props.getProperty(name));
			}
		}
		return subProperties;
	}

	public static Properties cloneFrom(Properties props) {
		Properties newProps = new Properties();
		Set<String> names = props.stringPropertyNames();
		for (String name : names) {
			newProps.setProperty(name, props.getProperty(name));
		}
		return newProps;
	}

	public static byte[] toBytes(Properties props, String charsetName) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(out, charsetName);
			try {
				props.store(writer, null);
				writer.flush();
			} finally {
				writer.close();
			}
			return out.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static int getInt(Properties props, String key) {
		String value = getRequiredProperty(props, key);
		return Integer.parseInt(value);
	}

	public static boolean getBoolean(Properties props, String key) {
		String value = getRequiredProperty(props, key);
		return Boolean.parseBoolean(value);
	}
	
	public static boolean getBooleanOptional(Properties props, String key, boolean defaultValue) {
		String value = getProperty(props, key, false);
		if (value == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	/**
	 * 返回指定的属性； <br>
	 * 如果不存在，或者返回值为空（null 或 空白字符），则抛出 {@link IllegalArgumentException} 异常；
	 * 
	 * @param props props
	 * @param key   key
	 * @return String
	 */
	public static String getRequiredProperty(Properties props, String key) {
		return getProperty(props, key, true);
	}
	
	public static String getOptionalProperty(Properties props, String key) {
		return getProperty(props, key, false);
	}
	
	public static String getOptionalProperty(Properties props, String key, String defaultValue) {
		String value = getProperty(props, key, false);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * 返回指定的属性； <br>
	 * 
	 * @param props    属性表；
	 * @param key      要查找的 key；
	 * @param required 值为 false 时，如果不存在则返回 null；值为 true 时，如果不存在，或者返回值为空（null 或
	 *                 空白字符），则抛出 {@link IllegalArgumentException} 异常；
	 * @return
	 */
	public static String getProperty(Properties props, String key, boolean required) {
		String value = props.getProperty(key);
		if (value == null) {
			if (required) {
				throw new IllegalArgumentException("Miss property[" + key + "]!");
			}
			return null;
		}
		value = value.trim();
		if (value.length() == 0) {
			if (required) {
				throw new IllegalArgumentException("Miss property[" + key + "]!");
			}
			return null;
		}
		return value;

	}

	public static Property[] getOrderedValues(Properties props) {
		Property[] values = new Property[props.size()];
		String[] propNames = props.stringPropertyNames().toArray(new String[props.size()]);
		Arrays.sort(propNames, (n1, n2) -> n1.compareTo(n2));
		for (int i = 0; i < propNames.length; i++) {
			values[i] = new Property(propNames[i], props.getProperty(propNames[i]));
		}
		return values;
	}

	public static Properties createProperties(Property[] propValues) {
		Properties props = new Properties();
		setValues(props, propValues);
		return props;
	}

	public static Properties setValues(Properties props, Property[] propValues) {
		for (Property p : propValues) {
			props.setProperty(p.getName(), p.getValue());
		}
		return props;
	}
}
