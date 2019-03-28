package com.jd.blockchain.utils.http.agent;

import java.io.Closeable;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class ServiceConnectionManager implements Closeable {

	private PoolingHttpClientConnectionManager connectionMananeger;

	public ServiceConnectionManager() {
		this.connectionMananeger = new PoolingHttpClientConnectionManager();
		setMaxTotal(100).setDefaultMaxPerRoute(20);
	}

	public ServiceConnectionManager setMaxTotal(int maxConn) {
		connectionMananeger.setMaxTotal(maxConn);
		return this;
	}

	public ServiceConnectionManager setDefaultMaxPerRoute(int maxConnPerRoute) {
		connectionMananeger.setDefaultMaxPerRoute(maxConnPerRoute);
		return this;
	}

	HttpClientConnectionManager getHttpConnectionManager() {
		return connectionMananeger;
	}

	/**
	 * 创建一个受此连接管理器管理的连接；
	 * 
	 * @param endpoint
	 * @return
	 */
	public ServiceConnection create(ServiceEndpoint serviceEndpoint) {
		CloseableHttpClient httpClient = createHttpClient(serviceEndpoint, this);
		return new HttpServiceConnection(serviceEndpoint, httpClient);
	}

	/**
	 * 创建一个不受管理的连接；
	 * 
	 * @param serviceEndpoint
	 * @return
	 */
	public static ServiceConnection connect(ServiceEndpoint serviceEndpoint) {
		CloseableHttpClient httpClient = createHttpClient(serviceEndpoint, null);
		return new HttpServiceConnection(serviceEndpoint, httpClient);
	}

	@Override
	public void close() {
		PoolingHttpClientConnectionManager cm = connectionMananeger;
		if (cm != null) {
			connectionMananeger = null;
			cm.close();
		}
	}

	private static CloseableHttpClient createHttpClient(ServiceEndpoint serviceEndpoint,
			ServiceConnectionManager connectionManager) {
		try {
			HttpClientBuilder httpClientBuilder = HttpClients.custom();

			if (connectionManager != null) {
				HttpClientConnectionManager httpConnMng = connectionManager.getHttpConnectionManager();
				httpClientBuilder.setConnectionManager(httpConnMng).setConnectionManagerShared(true);
			}

			if (serviceEndpoint.isSecure()) {
				httpClientBuilder.setSSLSocketFactory(createSSLConnectionSocketFactory());
			}

			return httpClientBuilder.build();
		} catch (KeyManagementException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * 创建SSL安全连接
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	private static SSLConnectionSocketFactory createSSLConnectionSocketFactory()
			throws NoSuchAlgorithmException, KeyManagementException {
		SSLConnectionSocketFactory sslsf = null;
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, new TrustManager[] { trustManager }, null);
		sslsf = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
		return sslsf;
	}

	/**
	 * 重新验证方法，取消SSL验证（信任所有证书）
	 */
	private static TrustManager trustManager = new X509TrustManager() {

		@Override
		public void checkClientTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	};

}
