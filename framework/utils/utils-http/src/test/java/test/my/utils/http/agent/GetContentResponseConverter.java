package test.my.utils.http.agent;

import java.io.InputStream;

import com.jd.blockchain.utils.http.HttpServiceContext;
import com.jd.blockchain.utils.http.ResponseConverter;
import com.jd.blockchain.utils.http.agent.ServiceRequest;
import com.jd.blockchain.utils.http.converters.JsonResponseConverter;

public class GetContentResponseConverter implements ResponseConverter {

	private JsonResponseConverter jsonResponseConverter = new JsonResponseConverter(DataResponse.class);

	@Override
	public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) throws Exception {
		DataResponse data = (DataResponse) jsonResponseConverter.getResponse(request, responseStream, null);
		return data.getContent();
	}

}
