package com.jd.blockchain.web.converters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jd.blockchain.binaryproto.DataContractRegistry;
import com.jd.blockchain.consensus.ClientIdentification;
import com.jd.blockchain.consensus.ClientIdentifications;
import com.jd.blockchain.consensus.action.ActionRequest;
import com.jd.blockchain.consensus.action.ActionResponse;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.utils.io.BytesUtils;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.jd.blockchain.binaryproto.BinaryProtocol;

/**
 * 针对二进制对象的序列化和反序列化的 HTTP 消息转换器；
 * 
 * @author huanghaiquan
 *
 */
public class BinaryMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final String CONTENT_TYPE_VALUE = "application/bin-obj;charset=UTF-8";

    static {
        DataContractRegistry.register(TransactionContent.class);
        DataContractRegistry.register(TransactionContentBody.class);
        DataContractRegistry.register(TransactionRequest.class);
        DataContractRegistry.register(NodeRequest.class);
        DataContractRegistry.register(EndpointRequest.class);
        DataContractRegistry.register(TransactionResponse.class);
        DataContractRegistry.register(DataAccountKVSetOperation.class);
        DataContractRegistry.register(DataAccountKVSetOperation.KVWriteEntry.class);

        DataContractRegistry.register(Operation.class);
        DataContractRegistry.register(ContractCodeDeployOperation.class);
        DataContractRegistry.register(ContractEventSendOperation.class);
        DataContractRegistry.register(DataAccountRegisterOperation.class);
        DataContractRegistry.register(UserRegisterOperation.class);
        DataContractRegistry.register(ParticipantRegisterOperation.class);
        DataContractRegistry.register(ParticipantStateUpdateOperation.class);

        DataContractRegistry.register(ActionRequest.class);
        DataContractRegistry.register(ActionResponse.class);
        DataContractRegistry.register(ClientIdentifications.class);
        DataContractRegistry.register(ClientIdentification.class);
    }

	public BinaryMessageConverter() {
		super(MediaType.valueOf(CONTENT_TYPE_VALUE));
	}

	@Override
	protected boolean supports(Class<?> aClass) {
		return true;
	}

    @Override
    protected boolean canRead(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.includes(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        } else if (MediaType.ALL.equals(mediaType)) {
            return true;
        }
        for (MediaType supportedMediaType : getSupportedMediaTypes()) {
            if (supportedMediaType.isCompatibleWith(mediaType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        InputStream in = inputMessage.getBody();
        byte[] serializeBytes = BytesUtils.readBytes(in);
        Object resolvedObj = BinaryProtocol.decode(serializeBytes);
        return resolvedObj;
    }

    @Override
    public void writeInternal(Object t, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        OutputStream out = outputMessage.getBody();
        if (t instanceof TransactionResponse) {
            byte[] serializeBytes = BinaryProtocol.encode(t, TransactionResponse.class);
            out.write(serializeBytes);
            out.flush();
        }
    }
}
