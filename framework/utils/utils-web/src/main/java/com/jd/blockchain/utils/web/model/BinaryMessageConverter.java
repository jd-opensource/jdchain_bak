package com.jd.blockchain.utils.web.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

/**
 * 针对二进制对象的序列化和反序列化的 HTTP 消息转换器；
 * 
 * @author huanghaiquan
 *
 */
public class BinaryMessageConverter implements HttpMessageConverter<Object> {

	public static final String CONTENT_TYPE_VALUE = "application/bin-obj";

	public static final MediaType CONTENT_TYPE = MediaType.valueOf(CONTENT_TYPE_VALUE);

	private static final List<MediaType> SUPPORTED_MEDIA_TYPES = Collections.singletonList(CONTENT_TYPE);

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return CONTENT_TYPE.includes(mediaType)
				&& (clazz.isPrimitive() || Serializable.class.isAssignableFrom(clazz) || Externalizable.class.isAssignableFrom(clazz));
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return CONTENT_TYPE.includes(mediaType)
				&& (clazz.isPrimitive() || Serializable.class.isAssignableFrom(clazz) || Externalizable.class.isAssignableFrom(clazz));
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return SUPPORTED_MEDIA_TYPES;
	}

	@Override
	public Object read(Class<? extends Object> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return BinarySerializeUtils.deserialize(inputMessage.getBody());
	}

	@Override
	public void write(Object t, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		BinarySerializeUtils.serialize(t, outputMessage.getBody());
	}

}
