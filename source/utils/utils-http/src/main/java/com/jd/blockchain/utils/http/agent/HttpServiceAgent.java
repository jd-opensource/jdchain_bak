package com.jd.blockchain.utils.http.agent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.jd.blockchain.utils.http.HttpAction;
import com.jd.blockchain.utils.http.HttpMethod;
import com.jd.blockchain.utils.http.HttpService;
import com.jd.blockchain.utils.http.HttpServiceContext;
import com.jd.blockchain.utils.http.HttpServiceException;
import com.jd.blockchain.utils.http.HttpStatusException;
import com.jd.blockchain.utils.http.NamedParam;
import com.jd.blockchain.utils.http.NamedParamMap;
import com.jd.blockchain.utils.http.PathParam;
import com.jd.blockchain.utils.http.RequestBody;
import com.jd.blockchain.utils.http.RequestBodyConverter;
import com.jd.blockchain.utils.http.RequestParam;
import com.jd.blockchain.utils.http.RequestParamFilter;
import com.jd.blockchain.utils.http.RequestParamMap;
import com.jd.blockchain.utils.http.ResponseBodyConverterFactory;
import com.jd.blockchain.utils.http.ResponseConverter;
import com.jd.blockchain.utils.http.StringConverter;
import com.jd.blockchain.utils.http.converters.NullResponseConverter;
import com.jd.blockchain.utils.http.converters.StringResponseConverter;

/**
 * http 服务代理；
 * 
 * @author haiq
 *
 */
public class HttpServiceAgent {
	
	private static final NamedParam[] EMPTY_PARAMS = {};

	private static Map<Class<?>, HttpServiceAgent> serviceAgentMap = new ConcurrentHashMap<Class<?>, HttpServiceAgent>();

	private Class<?> serviceClass;

	private RequestBodyConverter defaultRequestBodyConverter;

	private ResponseConverter defaultResponseConverter;

	private ResponseBodyConverterFactory responseConverterFactory;

	private AuthorizationHeaderResovler authorizationHeaderResolver;

	private Map<Method, ServiceActionContext> actions = new HashMap<>();

	private HttpServiceAgent(Class<?> serviceClass, 
			AuthorizationHeaderResovler authResolver) {
		this.serviceClass = serviceClass;
		this.authorizationHeaderResolver = authResolver;

		resolveService();
	}

	/**
	 * 创建映射指定服务接口的 HTTP 服务代理；
	 * 
	 * @param serviceClass
	 *            服务的接口类型；
	 * @param serviceEndpoint
	 *            连接到服务提供者服务器的相关设置；
	 * @param authorizationHeader
	 *            安全认证头部；
	 * @return
	 */
	public static <T> T createService(Class<T> serviceClass, ServiceEndpoint serviceEndpoint,
			RequestHeader... authorizationHeaders) {
		return createService(serviceClass, serviceEndpoint, null, null, authorizationHeaders);
	}

	/**
	 * 创建映射指定服务接口的 HTTP 服务代理；
	 * 
	 * @param serviceClass
	 *            服务的接口类型；
	 * @param serviceEndpoint
	 *            服务终结点；
	 * @return
	 */
	public static <T> T createService(Class<T> serviceClass, ServiceEndpoint serviceEndpoint) {
		return createService(serviceClass, serviceEndpoint, (AuthorizationHeaderResovler) null);
	}

	/**
	 * 创建映射指定服务接口的 HTTP 服务代理；
	 * 
	 * @param serviceClass
	 *            定义了服务的接口类型；
	 * @param serviceEndpoint
	 *            服务终结点；
	 * @param authorizationHeaderResolver
	 *            安全认证头部的解析器；
	 * @return
	 */
	public static <T> T createService(Class<T> serviceClass, ServiceEndpoint serviceEndpoint,
			AuthorizationHeaderResovler authorizationHeaderResolver) {
		return createService(serviceClass, serviceEndpoint, null, authorizationHeaderResolver);
	}

	/**
	 * 创建映射指定服务接口的 HTTP 服务代理；
	 * 
	 * @param serviceClass
	 *            定义了服务的接口类型；
	 * @param serviceEndpoint
	 *            服务终结点；
	 * @param connectionManager
	 *            连接管理器；
	 * @param authorizationHeaderResolver
	 *            安全认证头部的解析器；
	 * @param headers
	 *            请求头部；
	 * @return
	 */
	public static <T> T createService(Class<T> serviceClass, ServiceEndpoint serviceEndpoint,
			ServiceConnectionManager connectionManager, AuthorizationHeaderResovler authorizationHeaderResolver,
			RequestHeader... headers) {
		ServiceConnection connection = null;
		if (connectionManager == null) {
			connection = ServiceConnectionManager.connect(serviceEndpoint);
		} else {
			connection = connectionManager.create(serviceEndpoint);
		}
		return createService(serviceClass, connection, authorizationHeaderResolver, headers);
	}

	/**
	 * 创建映射指定服务接口的 HTTP 服务代理；
	 * 
	 * @param serviceClass
	 *            定义了服务的接口类型；
	 * @param serviceEndpoint
	 *            服务终结点；
	 * @param connectionManager
	 *            连接管理器；
	 * @param authorizationHeaderResolver
	 *            安全认证头部的解析器；
	 * @param headers
	 *            请求头部；
	 * @return
	 */
	public static <T> T createService(Class<T> serviceClass, ServiceConnection connection,
			AuthorizationHeaderResovler authorizationHeaderResolver, RequestHeader... headers) {
		return createService(serviceClass, connection, authorizationHeaderResolver, headers, null);
	}

	/**
	 * 创建映射指定服务接口的 HTTP 服务代理；
	 * 
	 * @param serviceClass
	 *            定义了服务的接口类型；
	 * @param serviceEndpoint
	 *            服务终结点；
	 * @param connectionManager
	 *            连接管理器；
	 * @param authorizationHeaderResolver
	 *            安全认证头部的解析器；
	 * @param headers
	 *            请求头部；
	 * @param bindingData
	 *            由调用者指定的绑定对象；<br>
	 *            该对象将被关联到 HttpServiceContext 上；调用者可以通过此对象将某些数据对象传递到调用过程的一些处理组件上，例如
	 *            {@link ResponseConverter}；
	 * @return
	 */
	public static <T> T createService(Class<T> serviceClass, ServiceConnection connection,
			AuthorizationHeaderResovler authorizationHeaderResolver, RequestHeader[] headers, Object bindingData) {
		if (serviceClass == null) {
			throw new IllegalArgumentException("Service class is null!");
		}
		if (!(connection instanceof HttpServiceConnection)) {
			throw new IllegalArgumentException(
					"Illegal service connection! It must be created by the ServiceConnectionManager!");
		}
		HttpServiceConnection httpConnection = (HttpServiceConnection) connection;
		// 避免反复解析同一个服务类型；
		HttpServiceAgent agent = serviceAgentMap.get(serviceClass);
		if (agent == null) {
			synchronized (serviceClass) {
				agent = serviceAgentMap.get(serviceClass);
				if (agent == null) {
					agent = new HttpServiceAgent(serviceClass, authorizationHeaderResolver);
					serviceAgentMap.put(serviceClass, agent);
				}
			}
		}

		// CloseableHttpClient httpClient = createHttpClient(serviceEndpoint,
		// connectionManager);
		
		ServiceInvocationHandler invocationHandler = new ServiceInvocationHandler(agent, httpConnection, headers,
				bindingData);

		@SuppressWarnings("unchecked")
		T serviceProxy = (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class<?>[] { serviceClass },
				invocationHandler);
		return serviceProxy;
	}

	private void resolveService() {
		// 处理服务路径；
		HttpService serviceAnno = serviceClass.getAnnotation(HttpService.class);
		if (serviceAnno == null) {
			throw new IllegalHttpServiceDefinitionException(
					"The specific service was not tag with HttpService annotation!");
		}
		String servicePath = serviceAnno.path();
		servicePath = StringUtils.cleanPath(servicePath);
		if (StringUtils.isEmpty(servicePath)) {
			throw new IllegalHttpServiceDefinitionException(
					"Illegal path or no path was specified for the HttpService!-- path=" + serviceAnno.path());
		}

		// 实例化服务的默认请求体参数转换器；
		Class<?> defaultRequestBodyConverterClazz = serviceAnno.defaultRequestBodyConverter();
		if (defaultRequestBodyConverterClazz != null && defaultRequestBodyConverterClazz != RequestBodyConverter.class) {
			if (RequestBodyConverter.class.isAssignableFrom(defaultRequestBodyConverterClazz)) {
				defaultRequestBodyConverter = (RequestBodyConverter) BeanUtils.instantiate(defaultRequestBodyConverterClazz);
			} else {
				throw new IllegalHttpServiceDefinitionException(
						"The specified default request body converter of service doesn't implement the interface "
								+ RequestBodyConverter.class.getName() + "!");
			}
		}
		
		// 实例化服务的默认回复转换器；
		Class<?> defaultResponseConverterClazz = serviceAnno.defaultResponseConverter();
		if (defaultResponseConverterClazz != null && defaultResponseConverterClazz != ResponseConverter.class) {
			if (ResponseConverter.class.isAssignableFrom(defaultResponseConverterClazz)) {
				defaultResponseConverter = (ResponseConverter) BeanUtils.instantiate(defaultResponseConverterClazz);
			} else {
				throw new IllegalHttpServiceDefinitionException(
						"The specified default response converter of service doesn't implement the interface "
								+ ResponseConverter.class.getName() + "!");
			}
		}
		Class<?> responseConverterFactoryClazz = serviceAnno.responseConverterFactory();
		if (responseConverterFactoryClazz != null && responseConverterFactoryClazz != ResponseBodyConverterFactory.class) {
			if (ResponseBodyConverterFactory.class.isAssignableFrom(responseConverterFactoryClazz)) {
				this.responseConverterFactory = (ResponseBodyConverterFactory) BeanUtils
						.instantiate(responseConverterFactoryClazz);
			} else {
				throw new IllegalHttpServiceDefinitionException(
						"The specified response converter factory of service doesn't implement the interface "
								+ ResponseBodyConverterFactory.class.getName() + "!");
			}

		}

		// 解析服务操作；
//		Method[] mths = ReflectionUtils.getAllDeclaredMethods(serviceClass);
		Method[] mths = serviceClass.getMethods();
		for (Method mth : mths) {
			ServiceActionContext actionContext = resolveAction( mth, servicePath);
			if (actionContext != null) {
				actions.put(mth, actionContext);
			}
		}
	}

	/**
	 * 解析指定的方法；
	 * 
	 * 如果指定的方法不符合解析策略则返回 null；
	 * 
	 * @param serviceEndpoint
	 * @param mth
	 * @param servicePath
	 * @return
	 */
	private ServiceActionContext resolveAction(Method mth, String servicePath) {
		// 生成路径模板；
		HttpAction actionAnno = mth.getAnnotation(HttpAction.class);
		if (actionAnno == null) {
			// 未声明的方法不做解析；
			return null;
		}
		String actionPath = StringUtils.cleanPath(actionAnno.path());
		if (StringUtils.isEmpty(actionPath)) {
			actionPath = mth.getName();
		}
		RequestPathTemplate pathTemplate = new RequestPathTemplate(servicePath, actionPath);

		// 校验请求你方法；
		if (actionAnno.method() == null) {
			throw new IllegalHttpServiceDefinitionException("The http method of action was not specified!");
		}

		String contentType = actionAnno.contentType();
		if (contentType != null) {
			contentType = contentType.trim();
			if (contentType.length() == 0) {
				contentType = null;
			}
		}

		RequestParamFilter reqParamFilter = createRequestParamFilter(actionAnno);
		ResponseConverter responseConverter = createResponseConverter(actionAnno, mth);

		// 获取参数定义；
		// 参数列表中， RequestBody 最多只能定义一个；
		RequestBodyResolverComposite bodyResolverComposite = new RequestBodyResolverComposite();
		Class<?>[] paramTypes = mth.getParameterTypes();
		Annotation[][] paramAnnos = mth.getParameterAnnotations();

		List<ArgDefEntry<RequestParam>> reqParamAnnos = new LinkedList<ArgDefEntry<RequestParam>>();
		List<ArgDefEntry<RequestParamMap>> reqParamMapAnnos = new LinkedList<ArgDefEntry<RequestParamMap>>();
		List<ArgDefEntry<PathParam>> pathParamAnnos = new LinkedList<ArgDefEntry<PathParam>>();
		for (int i = 0; i < paramTypes.length; i++) {
			RequestBody reqBodyAnno = findAnnotation(RequestBody.class, paramAnnos[i]);
			RequestParam reqParamAnno = findAnnotation(RequestParam.class, paramAnnos[i]);
			RequestParamMap reqParamsAnno = findAnnotation(RequestParamMap.class, paramAnnos[i]);
			PathParam pathParamAnno = findAnnotation(PathParam.class, paramAnnos[i]);
			if (hasConflictiveAnnotation(reqBodyAnno, reqParamAnno, reqParamsAnno, pathParamAnno)) {
				// 存在冲突的定义；
				throw new IllegalHttpServiceDefinitionException(
						"The argument[" + i + "] of action has conflictive definition!");
			}
//			if (bodyResolver != null && reqBodyAnno != null) {
//				throw new IllegalHttpServiceDefinitionException("Define more than one request body for the action!");
//			}
			if (reqBodyAnno != null) {
				RequestBodyResolver reqBodyResolver = createBodyResolver(new ArgDefEntry<RequestBody>(i, paramTypes[i], reqBodyAnno));
				bodyResolverComposite.addRequestBodyResolver(reqBodyResolver);
			}
			if (reqParamAnno != null) {
				reqParamAnnos.add(new ArgDefEntry<RequestParam>(i, paramTypes[i], reqParamAnno));
			}
			if (reqParamsAnno != null) {
				reqParamMapAnnos.add(new ArgDefEntry<RequestParamMap>(i, paramTypes[i], reqParamsAnno));
			}
			if (pathParamAnno != null) {
				pathParamAnnos.add(new ArgDefEntry<PathParam>(i, paramTypes[i], pathParamAnno));
			}
		}
		RequestParamResolver reqParamResolver = createRequestParamResolver(reqParamAnnos, reqParamMapAnnos);
		PathParamResolver pathParamResolver = createPathParamResolver(pathParamAnnos);
//		if (bodyResolverComposite == null) {
//			bodyResolverComposite = RequestBodyResolvers.NULL_BODY_RESOLVER;
//		}

		// 获取声明的异常列表；
		Class<?>[] thrownExceptionTypes = mth.getExceptionTypes();

		ServiceActionContext actionContext = new ServiceActionContext(mth, actionAnno.method(), contentType,
				pathTemplate, pathParamResolver, reqParamFilter, reqParamResolver, bodyResolverComposite, responseConverter,
				thrownExceptionTypes, actionAnno.resolveContentOnHttpError());
		return actionContext;
	}

	@SuppressWarnings("unchecked")
	private static <T> T findAnnotation(Class<T> clazz, Annotation[] annos) {
		for (Annotation annotation : annos) {
			if (clazz.isAssignableFrom(annotation.getClass())) {
				return (T) annotation;
			}
		}
		return null;
	}

	private RequestParamFilter createRequestParamFilter(HttpAction actionDef) {
		Class<?> reqParamFilterClass = actionDef.requestParamFilter();
		if (reqParamFilterClass == null || reqParamFilterClass == RequestParamFilter.class) {
			return NullRequestParamFilter.INSTANCE;
		}
		if (RequestParamFilter.class.isAssignableFrom(reqParamFilterClass)) {
			return (RequestParamFilter) BeanUtils.instantiate(reqParamFilterClass);
		} else {
			throw new IllegalHttpServiceDefinitionException(
					"The specified RequestParamFilter doesn't implement the interface "
							+ RequestParamFilter.class.getName() + "!");
		}
	}

	/**
	 * 创建回复结果转换器；
	 * 
	 * @param actionDef
	 * @param retnClazz
	 * @return
	 */
	private ResponseConverter createResponseConverter(HttpAction actionDef, Method mth) {
		Class<?> retnClazz = mth.getReturnType();
		if (Void.class.equals(retnClazz)) {
			return NullResponseConverter.INSTANCE;
		}
		Class<?> respConverterClass = actionDef.responseConverter();
		if (respConverterClass == null || respConverterClass == ResponseConverter.class) {
			// 未设置方法级别的回复转换器；
			if (defaultResponseConverter != null) {
				// 如果未设置方法级别的回复转换器，且设置了服务级别的默认回复转换器，则应用服务级别的默认回复转换器；
				return defaultResponseConverter;
			}
			if (responseConverterFactory != null) {
				return responseConverterFactory.createResponseConverter(actionDef, mth);
			}
		}
		if (respConverterClass != null && respConverterClass != ResponseConverter.class) {
			if (ResponseConverter.class.isAssignableFrom(respConverterClass)) {
				return (ResponseConverter) BeanUtils.instantiate(respConverterClass);
			} else {
				throw new IllegalHttpServiceDefinitionException(
						"The specified response converter doesn't implement the interface "
								+ ResponseConverter.class.getName() + "!");
			}
		}
		// create default response converter;
		return DefaultResponseConverterFactory.INSTANCE.createResponseConverter(actionDef, mth);

		// if (byte[].class == retnClazz) {
		// return ByteArrayResponseConverter.INSTANCE;
		// }
		// if (String.class == retnClazz) {
		// return StringResponseConverter.INSTANCE;
		// }
		// // TODO:未处理 基本类型、输入输出流；
		// return new JsonResponseConverter(retnClazz);
	}

	/**
	 * 创建路径参数解析器；
	 * 
	 * @param pathParamAnnos
	 * @return
	 */
	private PathParamResolver createPathParamResolver(List<ArgDefEntry<PathParam>> pathParamAnnos) {
		if (pathParamAnnos.size() == 0) {
			return PathParamResolvers.NONE_PATH_PARAM_RESOLVER;
		}
		List<ArgDefEntry<PathParamDefinition>> pathParamDefs = new LinkedList<ArgDefEntry<PathParamDefinition>>();
		for (ArgDefEntry<PathParam> entry : pathParamAnnos) {
			if (StringUtils.isEmpty(entry.getDefinition().name())) {
				throw new IllegalHttpServiceDefinitionException("The name of path parameter is empty!");
			}

			Class<?> converterClazz = entry.getDefinition().converter();
			StringConverter converter = StringConverterFactory.instantiateStringConverter(converterClazz);
			ArgDefEntry<PathParamDefinition> argDefEntry = new ArgDefEntry<PathParamDefinition>(entry.getIndex(),
					entry.getArgType(), new PathParamDefinition(entry.getDefinition().name(), converter));
			pathParamDefs.add(argDefEntry);
		}

		return PathParamResolvers.createResolver(pathParamDefs);
	}

	/**
	 * 创建请求参数解析器；
	 * 
	 * @param reqParamAnnos
	 * @return
	 */
	private RequestParamResolver createRequestParamResolver(List<ArgDefEntry<RequestParam>> reqParamAnnos,
			List<ArgDefEntry<RequestParamMap>> reqParamsAnnos) {
		List<ArgDefEntry<RequestParamDefinition>> reqDefs = RequestParamDefinition
				.resolveSingleParamDefinitions(reqParamAnnos);
		List<ArgDefEntry<RequestParamMapDefinition>> reqMapDefs = RequestParamMapDefinition
				.resolveParamMapDefinitions(reqParamsAnnos);

		return RequestParamResolvers.createParamMapResolver(reqDefs, reqMapDefs);

	}

	/**
	 * @param paramIndex
	 * @param parameter
	 * @param reqBodyAnnoEntry
	 * @return
	 */
	private RequestBodyResolver createBodyResolver(ArgDefEntry<RequestBody> reqBodyAnnoEntry) {
		Class<?> converterClazz = reqBodyAnnoEntry.getDefinition().converter();
		RequestBodyConverter converter = null;
		if (converterClazz == RequestBodyConverter.class || converterClazz == null) {
			converter = defaultRequestBodyConverter;
			if (converter == null) {
				// create default body converter;
				converter = new TypeAutoAdaptingRequestBodyConverter(reqBodyAnnoEntry.getArgType());
			}
		} else {
			if (!ClassUtils.isAssignable(RequestBodyConverter.class, converterClazz)) {
				throw new IllegalHttpServiceDefinitionException(
						"The specified body converter doesn't implement the interface "
								+ RequestBodyConverter.class.getName() + "!");
			}
			converter = (RequestBodyConverter) BeanUtils.instantiate(converterClazz);
		}

		RequestBodyDefinition reqBodyDef = new RequestBodyDefinition(reqBodyAnnoEntry.getDefinition().required(),
				converter);
		ArgDefEntry<RequestBodyDefinition> reqBodyDefEntry = new ArgDefEntry<RequestBodyDefinition>(
				reqBodyAnnoEntry.getIndex(), reqBodyAnnoEntry.getArgType(), reqBodyDef);
		return RequestBodyResolvers.createArgumentResolver(reqBodyDefEntry);
	}

	/**
	 * 检查传入的三个参数中是否有两个或两个以上为非空；
	 * 
	 * @param reqBodyAnno
	 * @param reqParamAnno
	 * @param pathParamAnno
	 * @return 有两个或两个以上为非空时返回 true；
	 * 
	 *         全部为 null 或只有一个为 null 时，返回 false；
	 */
	private static boolean hasConflictiveAnnotation(RequestBody reqBodyAnno, RequestParam reqParamAnno,
			RequestParamMap reqParamsAnno, PathParam pathParamAnno) {
		return 1 < (reqBodyAnno == null ? 0 : 1) + (reqParamAnno == null ? 0 : 1) + (reqParamsAnno == null ? 0 : 1)
				+ (pathParamAnno == null ? 0 : 1);
	}

	/**
	 * 解析被调用的方法，映射为 http 请求；
	 */
	private Object invoke(ServiceEndpoint serviceEndpoint, HttpServiceContext serviceContext, CloseableHttpClient httpClient, RequestHeader[] headers,
			Method method, Object[] args) throws Throwable {
		ServiceActionContext actionContext = actions.get(method);
		if (actionContext == null) {
			throw new UnsupportedOperationException("The invoked method was not a service action!");
		}
		try {
			HttpServiceRequest request = resolveRequest(serviceEndpoint, actionContext, args);

			HttpUriRequest httpRequest = buildRequest(request);

			// 设置预定义的头部；
			setHeaders(httpRequest, headers);
			// 设置解析请求生成的头部；
			setHeaders(httpRequest, request.getHeaders());
			if (authorizationHeaderResolver != null) {
				AuthorizationHeader auth = authorizationHeaderResolver.generateHeader(request);
				// 设置认证属性；
				buildAuthorization(httpRequest, auth);
			}

			// 设置默认的 Content-Type；
			Header[] contentTypeHeaders = httpRequest.getHeaders(HttpHeaders.CONTENT_TYPE);
			if (contentTypeHeaders == null || contentTypeHeaders.length == 0) {
				httpRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			}
			CloseableHttpClient closeableHttpClient = HttpClientPool.getHttpClient(serviceEndpoint.getHost(), serviceEndpoint.getPort());

			CloseableHttpResponse response = closeableHttpClient.execute(httpRequest);
//			CloseableHttpResponse response = httpClient.execute(httpRequest);
			try {
				// 引发 http 异常；
				if (response.getStatusLine().getStatusCode() >= 400) {
					processAndThrowHttpException(actionContext, request, response);
					// 注：上一步已抛出异常；
					return null;
				}
				InputStream respStream = response.getEntity().getContent();
				Object respObject = actionContext.getResponseConverter().getResponse(request, respStream,
						serviceContext);
				return respObject;
			} finally {
				response.close();
			}
		} catch (Exception e) {
			if (isCustomThownException(e, actionContext)) {
				throw e;
			}
			if (e instanceof HttpServiceException) {
				throw (HttpServiceException) e;
			}
			throw new HttpServiceException(e.getMessage(), e);
		}
	}

	private void setHeaders(HttpUriRequest httpRequest, RequestHeader[] headers) {
		if (headers == null) {
			return;
		}
		for (RequestHeader header : headers) {
			httpRequest.setHeader(header.getName(), header.getValue());
		}
	}

	private void setHeaders(HttpUriRequest httpRequest, Properties customHeaders) {
		Set<String> names = customHeaders.stringPropertyNames();
		for (String name : names) {
			httpRequest.setHeader(name, customHeaders.getProperty(name));
		}
	}

	/**
	 * 判断指定的异常是否属于指定服务操作的接口方法通过 throws 声明的异常；
	 * 
	 * @param e
	 * @param actionContext
	 * @return
	 */
	private boolean isCustomThownException(Exception e, ServiceActionContext actionContext) {
		Class<?> exType = e.getClass();
		Class<?>[] thrownExTypes = actionContext.getThrownExceptionTypes();
		for (Class<?> thrExType : thrownExTypes) {
			if (thrExType.isAssignableFrom(exType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 处理 HTTP 错误，并抛出 HttpStatusException 异常；
	 * 
	 * @param actionContext
	 * @param response
	 */
	private void processAndThrowHttpException(ServiceActionContext actionContext, ServiceRequest request,
			CloseableHttpResponse response) throws HttpStatusException {
		String content = null;
		if (actionContext.isResolveContentOnHttpError()) {
			try {
				InputStream respStream = response.getEntity().getContent();
				content = (String) StringResponseConverter.INSTANCE.getResponse(request, respStream, null);
			} catch (UnsupportedOperationException e) {
				throw new HttpServiceException(e.getMessage(), e);
			} catch (IOException e) {
				throw new HttpServiceException(e.getMessage(), e);
			} catch (Exception e) {
				if (e instanceof HttpServiceException) {
					throw (HttpServiceException) e;
				}
				throw new HttpServiceException(e.getMessage(), e);
			}
		}
		String errMsg = String.format("[status=%s] %s", response.getStatusLine().getStatusCode(), content);
		throw new HttpStatusException(response.getStatusLine().getStatusCode(), errMsg);
	}

	private HttpServiceRequest resolveRequest(ServiceEndpoint serviceEndpoint, ServiceActionContext actionContext, Object[] args) throws IOException {
		HttpServiceRequest request;
		switch (actionContext.getRequestMethod()) {
		case GET:
			request = resolveGetRequest(serviceEndpoint, actionContext, args);
			break;
		case POST:
		case PUT:
			request = resolvePostOrPutRequest(serviceEndpoint, actionContext, args);
			break;
		case DELETE:
			request = resolveDeleteRequest(serviceEndpoint, actionContext, args);
			break;
		default:
			throw new UnsupportedOperationException(
					"Unsupported http method '" + actionContext.getRequestMethod() + "'!");
		}
		if (actionContext.getContentType() != null) {
			request.setHeader(HttpHeaders.CONTENT_TYPE, actionContext.getContentType());
		}
		return request;
	}

	/**
	 * 创建请求；
	 * 
	 * @param actionContext
	 * @param args
	 * @return
	 */
	private HttpUriRequest buildRequest(ServiceRequest request) {
		ByteBuffer bodyBytes = null;
		if (request.getBody() != null) {
			// bodyStream = new ByteArrayInputStream(request.getBody().array());

			bodyBytes = request.getBody();
		}
		NamedParamMap reqParams = request.getRequestParams();
		switch (request.getHttpMethod()) {
		case GET:
			return new HttpGet(request.getUri());
		case POST:
			HttpPost httppost = new HttpPost(request.getUri());

			if (reqParams != null) {
				// 以 form 表单提交；
				NamedParam[] propNames = reqParams.getParams();
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for (NamedParam param : propNames) {
					formParams.add(new BasicNameValuePair(param.getName(), param.getValue()));
				}
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
				httppost.setEntity(formEntity);
				// 设置默认的 Content-Type；
				httppost.setHeader(formEntity.getContentType());
			}
			if (bodyBytes != null) {
				// 查询参数以 Stream body 方式提交；
				ByteArrayEntity entity = new ByteArrayEntity(bodyBytes.array());
				// HttpEntity streamEntity = new InputStreamEntity(bodyStream);
				httppost.setEntity(entity);
				// 设置默认的 Content-Type；
				httppost.setHeader(entity.getContentType());
			}
			return httppost;
		case PUT:
			HttpPut httpput = new HttpPut(request.getUri());
			if (reqParams != null) {
				// 以 form 表单提交；
				NamedParam[] paramValues = reqParams.getParams();
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for (NamedParam param : paramValues) {
					formParams.add(new BasicNameValuePair(param.getName(), param.getValue()));
				}
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
				httpput.setEntity(formEntity);
			}
			if (bodyBytes != null) {
				// 查询参数以 Stream body 方式提交；
				ByteArrayEntity entity = new ByteArrayEntity(bodyBytes.array());
				// HttpEntity streamEntity = new InputStreamEntity(bodyStream);
				httpput.setEntity(entity);
			}
			return httpput;
		case DELETE:
			// HttpDelete httpDelete = new HttpDelete(uri);
			LocalHttpDelete httpDelete = new LocalHttpDelete(request.getUri());
			// 查询参数以 delete body 方式提交
			if (bodyBytes != null) {
				ByteArrayEntity entity = new ByteArrayEntity(bodyBytes.array());
				// HttpEntity entity = new InputStreamEntity(bodyStream);
				httpDelete.setEntity(entity);
			}
			// HttpEntity entity = new InputStreamEntity(bodyStream);
			// httpDelete.setEntity(entity);
			return httpDelete;
		default:
			throw new UnsupportedOperationException("Unsupported http method '" + request.getHttpMethod() + "'!");
		}
	}

	/**
	 * 设置http请求头的Authorization属性
	 * 
	 * @param request
	 * @param setting
	 */
	private void buildAuthorization(HttpUriRequest request, RequestHeader setting) {
		request.addHeader(setting.getName(), setting.getValue());
	}

	/**
	 * 创建 http post 请求；
	 * 
	 * @param actionContext
	 * @param args
	 * @return
	 * @throws IOException
	 */
	// private HttpServiceRequest resolvePostRequest(ServiceActionContext
	// actionContext, Object[] args)
	// throws IOException {
	// Map<String, String> pathParams =
	// actionContext.getPathParamResolver().resolve(args);
	// Properties reqParams =
	// actionContext.getRequestParamResolver().resolve(args);
	// InputStream inputStream =
	// actionContext.getRequestBodyResolver().resolve(args);
	// URI uri = actionContext.getPathTemplate().generateRequestURI(pathParams,
	// reqParams,
	// ServiceActionContext.DEFAULT_CHARSET);
	// byte[] bytes = BytesUtils.copyToBytes(inputStream);
	// return new HttpServiceRequest(HttpMethod.POST, uri,
	// ByteBuffer.wrap(bytes));
	// }

	/**
	 * 创建http put请求
	 * 
	 * @param actionContext
	 * @param args
	 * @return
	 * @throws IOException
	 */
	// private HttpServiceRequest resolvePutRequest(ServiceActionContext
	// actionContext, Object[] args) throws IOException {
	// Map<String, String> pathParams =
	// actionContext.getPathParamResolver().resolve(args);
	// Properties reqParams =
	// actionContext.getRequestParamResolver().resolve(args);
	// InputStream inputStream =
	// actionContext.getRequestBodyResolver().resolve(args);
	// URI uri = actionContext.getPathTemplate().generateRequestURI(pathParams,
	// reqParams,
	// ServiceActionContext.DEFAULT_CHARSET);
	// byte[] bytes = BytesUtils.copyToBytes(inputStream);
	// return new HttpServiceRequest(HttpMethod.PUT, uri,
	// ByteBuffer.wrap(bytes));
	// }

	private HttpServiceRequest resolvePostOrPutRequest(ServiceEndpoint serviceEndpoint, ServiceActionContext actionContext, Object[] args)
			throws IOException {
		// 解析路径参数；
		Map<String, String> pathParams = actionContext.getPathParamResolver().resolve(args);
		HttpMethod httpMethod = actionContext.getRequestMethod();

		// 解析 RequestBody；
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		actionContext.getRequestBodyResolver().resolve(args, out);
		byte[] bodyBytes = out.toByteArray();

		boolean noBody = bodyBytes.length == 0;

		// 解析 RequestParam；
		NamedParamMap reqParams = actionContext.getRequestParamResolver().resolve(args);
		boolean noReqParams = reqParams.isEmpty();
		actionContext.getRequestParamFilter().filter(httpMethod, reqParams);

		// 如果只有 RequestBody 标注的参数，则以 RequestBody 参数的序列化输出作为请求体；
		if ((!noBody) && noReqParams) {
			URI uri = actionContext.getPathTemplate().generateRequestURI(serviceEndpoint, pathParams, new NamedParamMap(),
					ServiceActionContext.DEFAULT_CHARSET);
			ByteBuffer body = ByteBuffer.wrap(bodyBytes);
			return new HttpServiceRequest(httpMethod, uri, null, body, args);
		}
		// 如果没有 RequestBody 标注的参数，只有 RequestParam ，则 RequestParam 通过请求体以表单格式提交；
		if (noBody && (!noReqParams)) {
			URI uri = actionContext.getPathTemplate().generateRequestURI(serviceEndpoint, pathParams, new NamedParamMap(),
					ServiceActionContext.DEFAULT_CHARSET);
			return new HttpServiceRequest(httpMethod, uri, reqParams, null, args);
		}

		// 如果同时有 RequestBody 标注的参数和 RequestParam 标注的参数，则以 RequestBody
		// 参数的序列化输出作为请求体，RequestParam 作为 URL 参数；
		if ((!noBody) && (!noReqParams)) {
			URI uri = actionContext.getPathTemplate().generateRequestURI(serviceEndpoint, pathParams, reqParams,
					ServiceActionContext.DEFAULT_CHARSET);
			ByteBuffer body = ByteBuffer.wrap(bodyBytes);
			return new HttpServiceRequest(httpMethod, uri, null, body, args);
		}

		// 既没有 RequestBody，也没有 RequestParam；
		URI uri = actionContext.getPathTemplate().generateRequestURI(serviceEndpoint, pathParams, new NamedParamMap(),
				ServiceActionContext.DEFAULT_CHARSET);
		return new HttpServiceRequest(httpMethod, uri, null, null, args);
	}

	/**
	 * 创建http get请求
	 * 
	 * @param actionContext
	 * @param args
	 * @return
	 */
	private HttpServiceRequest resolveGetRequest(ServiceEndpoint serviceEndpoint, ServiceActionContext actionContext, Object[] args) {
		Map<String, String> pathParams = actionContext.getPathParamResolver().resolve(args);
		NamedParamMap reqParams = actionContext.getRequestParamResolver().resolve(args);
		URI uri = actionContext.getPathTemplate().generateRequestURI(serviceEndpoint, pathParams, reqParams,
				ServiceActionContext.DEFAULT_CHARSET);
		// 对于 get 请求，请求参数已经编码到 URI 中，所以不必再传递出去进行处理；
		return new HttpServiceRequest(HttpMethod.GET, uri, null, null, args);
	}

	/**
	 * 创建http delete请求
	 * 
	 * @param actionContext
	 * @param args
	 * @return
	 * @throws IOException
	 */
	private HttpServiceRequest resolveDeleteRequest(ServiceEndpoint serviceEndpoint, ServiceActionContext actionContext, Object[] args)
			throws IOException {
		Map<String, String> pathParams = actionContext.getPathParamResolver().resolve(args);

		NamedParamMap reqParams = actionContext.getRequestParamResolver().resolve(args);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		actionContext.getRequestBodyResolver().resolve(args, out);
		byte[] bodyBytes = out.toByteArray();

		URI uri = actionContext.getPathTemplate().generateRequestURI(serviceEndpoint, pathParams, reqParams,
				ServiceActionContext.DEFAULT_CHARSET);

		ByteBuffer body = bodyBytes.length == 0 ? null : ByteBuffer.wrap(bodyBytes);
		return new HttpServiceRequest(HttpMethod.DELETE, uri, null, body, args);
	}

	private static class ServiceInvocationHandler implements InvocationHandler, HttpServiceContext {
		
		private HttpServiceAgent serviceAgent;

		private HttpServiceConnection connection;

		private RequestHeader[] headers;

		private Object bindingData;

		public ServiceInvocationHandler(HttpServiceAgent serviceAgent, HttpServiceConnection connection,
				RequestHeader[] headers, Object bindingData) {
			this.serviceAgent = serviceAgent;
			this.connection = connection;
			this.headers = headers;
			this.bindingData = bindingData;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return serviceAgent.invoke(connection.getEndpoint(), this, connection.getHttpClient(), headers, method, args);
		}

		@Override
		public Class<?> getServiceClasss() {
			return serviceAgent.serviceClass;
		}

		@Override
		public Object getProxyBindingData() {
			return bindingData;
		}

	}

	private static class HttpServiceContextImpl implements HttpServiceContext {

		private Class<?> serviceClass;

		private Object proxyBindingData;

		public HttpServiceContextImpl(Class<?> serviceClass, Object proxyBindingData) {
			this.serviceClass = serviceClass;
			this.proxyBindingData = proxyBindingData;
		}

		@Override
		public Class<?> getServiceClasss() {
			return serviceClass;
		}

		@Override
		public Object getProxyBindingData() {
			return proxyBindingData;
		}

	}

	/**
	 * HttpServiceRequest 是对一次实际的服务调用转换生成的HTTP请求的模型；
	 * 
	 * @author haiq
	 *
	 */
	private static class HttpServiceRequest implements ServiceRequest {

		private HttpMethod method;

		private URI uri;

		private ByteBuffer body;

		private Properties headers = new Properties();

		private NamedParamMap requestParams;

		private Object[] args;

		public HttpServiceRequest(HttpMethod method, URI uri, NamedParamMap requestParams, ByteBuffer body,
				Object[] args) {
			this.method = method;
			this.uri = uri;
			this.requestParams = requestParams;
			this.body = body;
			this.args = args;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see my.utils.http.agent.Request#getMethod()
		 */
		@Override
		public HttpMethod getHttpMethod() {
			return method;
		}

		@Override
		public NamedParamMap getRequestParams() {
			return requestParams;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see my.utils.http.agent.Request#getUri()
		 */
		@Override
		public URI getUri() {
			return uri;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see my.utils.http.agent.Request#getBody()
		 */
		@Override
		public ByteBuffer getBody() {
			return body;
		}

		@SuppressWarnings("unused")
		public void setHeader(String name, String value) {
			headers.setProperty(name, value);
		}

		public Properties getHeaders() {
			return headers;
		}

		@Override
		public Object[] getArgs() {
			return args;
		}

	}
}
