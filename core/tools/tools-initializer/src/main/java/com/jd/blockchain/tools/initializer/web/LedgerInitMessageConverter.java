package com.jd.blockchain.tools.initializer.web;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.crypto.SignatureDigest;
import com.jd.blockchain.ledger.core.LedgerInitDecision;
import com.jd.blockchain.ledger.core.LedgerInitProposal;
import com.jd.blockchain.ledger.core.LedgerInitProposalData;
import com.jd.blockchain.utils.io.BytesUtils;

/**
 * 针对二进制对象的序列化和反序列化的 HTTP 消息转换器；
 * 
 * @author huanghaiquan
 *
 */
public class LedgerInitMessageConverter implements HttpMessageConverter<Object> {

	public static final String CONTENT_TYPE_VALUE = "application/bin-obj";

	public static final MediaType CONTENT_TYPE = MediaType.valueOf(CONTENT_TYPE_VALUE);

	private static final List<MediaType> SUPPORTED_MEDIA_TYPES = Collections.singletonList(CONTENT_TYPE);

	private static final Map<Class<?>, Class<?>> SUPPORTED_CONTRACT_TYPES = new HashMap<>();

	static {
		DataContractRegistry.register(LedgerInitProposal.class);
		DataContractRegistry.register(LedgerInitDecision.class);
		
		SUPPORTED_CONTRACT_TYPES.put(LedgerInitProposal.class, LedgerInitProposalData.class);
		SUPPORTED_CONTRACT_TYPES.put(LedgerInitDecision.class, LedgerInitDecisionData.class);

		// SUPPORTED_CONTRACT_TYPES.add(LedgerInitResponse.class);
		// DataContractRegistry.register(LedgerInitResponse.class);
	}

	private boolean isSupported(Class<?> clazz) {
		return getContractType(clazz) != null;
	}

	private Class<?> getContractType(Class<?> clazz) {
		for (Class<?> itf : SUPPORTED_CONTRACT_TYPES.keySet()) {
			if (itf.isAssignableFrom(clazz)) {
				return itf;
			}
		}
		return null;
	}

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return CONTENT_TYPE.includes(mediaType)
				&& (clazz.isPrimitive() || SignatureDigest.class == clazz || isSupported(clazz));
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return CONTENT_TYPE.includes(mediaType) && (clazz.isPrimitive() || LedgerInitResponse.class.isAssignableFrom(clazz) || isSupported(clazz));
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return SUPPORTED_MEDIA_TYPES;
	}

	@Override
	public Object read(Class<? extends Object> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		if (SignatureDigest.class == clazz) {
			byte[] signDigestBytes = BytesUtils.copyToBytes(inputMessage.getBody());
			return new SignatureDigest(signDigestBytes);
		}
		
		Class<?> contractType = getContractType(clazz);
		Class<?> implType = SUPPORTED_CONTRACT_TYPES.get(contractType);
		return BinaryProtocol.decode(inputMessage.getBody());
	}

	@Override
	public void write(Object t, MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		LedgerInitResponse resp;
		if (t == null) {
			resp = LedgerInitResponse.success(null);
		} else if (t instanceof LedgerInitResponse) {
			resp = (LedgerInitResponse) t;
			outputMessage.getBody().write(resp.toBytes());
		} else {
			Class<?> contractType = getContractType(t.getClass());
			if (contractType == null) {
				throw new IllegalStateException("Unsupported type[" + t.getClass().getName() + "]!");
			}
			byte[] data = BinaryProtocol.encode(t, contractType);
			resp = LedgerInitResponse.success(data);
			outputMessage.getBody().write(resp.toBytes());
		}
	}

}
