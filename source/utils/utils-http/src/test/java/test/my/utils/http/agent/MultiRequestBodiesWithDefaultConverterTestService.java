package test.my.utils.http.agent;

import com.jd.blockchain.utils.http.HttpAction;
import com.jd.blockchain.utils.http.HttpMethod;
import com.jd.blockchain.utils.http.HttpService;
import com.jd.blockchain.utils.http.PathParam;
import com.jd.blockchain.utils.http.RequestBody;
import com.jd.blockchain.utils.http.RequestParam;
import com.jd.blockchain.utils.http.converters.BinarySerializeRequestBodyConverter;
import com.jd.blockchain.utils.http.converters.BinarySerializeResponseConverter;

@HttpService(path = "/test", defaultRequestBodyConverter = BinarySerializeRequestBodyConverter.class, defaultResponseConverter = BinarySerializeResponseConverter.class)
public interface MultiRequestBodiesWithDefaultConverterTestService {
	@HttpAction(path = "/content/{id}/multi", method = HttpMethod.POST)
	public TestData multiContentBodies(@PathParam(name = "id") String id, @RequestParam(name = "name") String name,
			@RequestBody TestData data1, @RequestBody TestData data2);

}
