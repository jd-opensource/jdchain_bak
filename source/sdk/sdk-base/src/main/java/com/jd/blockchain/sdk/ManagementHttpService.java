package com.jd.blockchain.sdk;

import com.jd.blockchain.consensus.ClientIdentifications;
import com.jd.blockchain.sdk.converters.BinarySerializeRequestConverter;
import com.jd.blockchain.sdk.converters.BinarySerializeResponseConverter;
import com.jd.blockchain.setting.GatewayIncomingSetting;
import com.jd.blockchain.utils.http.HttpAction;
import com.jd.blockchain.utils.http.HttpMethod;
import com.jd.blockchain.utils.http.HttpService;
import com.jd.blockchain.utils.http.RequestBody;
import com.jd.blockchain.utils.web.client.WebResponseConverterFactory;


@HttpService(path="/management", defaultRequestBodyConverter = BinarySerializeRequestConverter.class, responseConverterFactory=WebResponseConverterFactory.class)
public interface ManagementHttpService {
	
	@HttpAction(method=HttpMethod.POST, path="/gateway/auth", contentType = BinarySerializeRequestConverter.CONTENT_TYPE_VALUE)
	public GatewayIncomingSetting authenticateGateway(@RequestBody ClientIdentifications clientIdentifications) ;
	
}
