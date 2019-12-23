package test.my.utils.http.agent;

import com.jd.blockchain.utils.http.HttpAction;
import com.jd.blockchain.utils.http.HttpMethod;
import com.jd.blockchain.utils.http.HttpService;
import com.jd.blockchain.utils.http.PathParam;
import com.jd.blockchain.utils.http.RequestBody;
import com.jd.blockchain.utils.http.RequestParam;
import com.jd.blockchain.utils.http.RequestParamMap;
import com.jd.blockchain.utils.http.converters.BinarySerializeRequestBodyConverter;
import com.jd.blockchain.utils.http.converters.BinarySerializeResponseConverter;

@HttpService(path = "/test")
public interface HttpTestService {

	/**
	 * 通过 HTTP GET 方法获取文本内容；
	 *
	 * @param id
	 * @param permited
	 * @param male
	 *            性别；作为请求参数，设置了自定义的转换器将布尔值转换为 0/1 ；
	 * @param value
	 *            值；定义为非必须的请求参数，如果值为 -1 时被忽略；
	 * @return 返回结果是包含了请求处理状态、错误信息、成功的数据内容在内的 JSON 字符串；
	 */
	@HttpAction(method = HttpMethod.GET)
	public String content(@RequestParam(name = "id") String id, @RequestParam(name = "permited") boolean permited,
			@RequestParam(name = "male", converter = CustomBooleanConverter.class) boolean male,
			@RequestParam(name = "value", required = false, ignoreValue = "-1") int value);

	/**
	 * 通过 HTTP GET 方法获取文本内容；
	 *
	 * @param setting
	 *            请求参数
	 * @return
	 */
	@HttpAction(method = HttpMethod.GET)
	public String content(@RequestParamMap ContentRequestSetting setting);

	/**
	 * 通过 HTTP GET 方法获取文本内容；
	 *
	 * @param id
	 * @param permited
	 * @param male
	 * @param value
	 * @param otherArg
	 *            定义一个额外的方法参数，但该参数的内容不会包含在 http 请求中；
	 * @return
	 */
	@HttpAction(path = "/content", method = HttpMethod.GET)
	public DataResponse getContent(@RequestParam(name = "id") String id,
			@RequestParam(name = "permited") boolean permited,
			@RequestParam(name = "male", converter = CustomBooleanConverter.class) boolean male,
			@RequestParam(name = "value", required = false, ignoreValue = "-1") int value, String otherArg);

	/**
	 * 以 HTTP POST 方式获取内容；
	 *
	 * @param id
	 * @param permited
	 * @param male
	 * @param value
	 * @return 指定了自定义的返回结果转换器，使返回结果仅包含数据内容，而不包含成功状态及错误信息；
	 */
	@HttpAction(path = "/content", method = HttpMethod.POST, responseConverter = GetContentResponseConverter.class)
	public String fetchContent(@RequestParam(name = "id") String id, @RequestParam(name = "permited") boolean permited,
			@RequestParam(name = "male", converter = CustomBooleanConverter.class) boolean male,
			@RequestParam(name = "value", required = false, ignoreValue = "-1") int value);

	/**
	 * 以 HTTP POST 方式获取内容；
	 *
	 * @param id
	 * @param permited
	 * @param male
	 * @param value
	 * @return 指定了自定义的返回结果转换器，使返回结果仅包含成功时数据内容，而不包含成功状态及错误信息，并且失败时抛出声明的异常；
	 * @throws GetContentException
	 */
	@HttpAction(path = "/content", method = HttpMethod.POST, responseConverter = GetContentResponseConverterWithException.class)
	public String fetchContentWithError(@RequestParam(name = "id") String id,
			@RequestParam(name = "permited") boolean permited,
			@RequestParam(name = "male", converter = CustomBooleanConverter.class) boolean male,
			@RequestParam(name = "value", required = false, ignoreValue = "-1") int value) throws GetContentException;

	@HttpAction(path = "/content/update", method = HttpMethod.GET)
	public String updateAndGetContent(@RequestParam(name = "content") String content);

	@HttpAction(path = "/content/{id}", method = HttpMethod.POST)
	public String contentById(@PathParam(name = "id") String id);
	
	@HttpAction(path = "/content/ids", method = HttpMethod.POST)
	public String contentByIds(@RequestParam(name = "ids", array=true) String[] ids);

	/**
	 * 以Http put 请求
	 *
	 * @param id
	 * @return
	 */
	@HttpAction(path = "/content/{id}", method = HttpMethod.PUT)
	public String contentByPut(@PathParam(name = "id") String id, @RequestParam(name = "content") String content);

	/**
	 * http delete 请求
	 *
	 * @param id
	 * @return
	 */
	@HttpAction(path = "/content/{id}", method = HttpMethod.DELETE)
	public String contentByDelete(@PathParam(name = "id") String id);

	@HttpAction(path = "/content/{id}", method = HttpMethod.POST)
	public String contentBody(@PathParam(name = "id") String id, @RequestParam(name = "name") String name,
			@RequestBody RequestContent requestContent);

	@HttpAction(path = "/content/{id}/multi", method = HttpMethod.POST, responseConverter = BinarySerializeResponseConverter.class)
	public TestData multiContentBodies(@PathParam(name = "id") String id, @RequestParam(name = "name") String name,
			@RequestBody(converter = BinarySerializeRequestBodyConverter.class) TestData data1,
			@RequestBody(converter = BinarySerializeRequestBodyConverter.class) TestData data2);

	/**
	 * 测试https请求
	 */
	@HttpAction(path = "/", method = HttpMethod.GET)
	public String testHttps();

}
