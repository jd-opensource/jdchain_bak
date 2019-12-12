package com.jd.blockchain.utils.test;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebBoot {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebBoot.class);

	private static final AtomicInteger PORT_POOL;

	static {
		int seed = 10000;
		int time = (int) (System.nanoTime() & 0xFFF);
		PORT_POOL = new AtomicInteger(seed + time);
	}

	private Server server;

	private InetSocketAddress hostAddr;

	public int getPort() {
		return hostAddr.getPort();
	}

	public WebBoot(InetSocketAddress hostAddr) {
		this(hostAddr, null);
	}

	public WebBoot(InetSocketAddress hostAddr, ServletContextListener contextListener) {
		this.server = new Server(hostAddr);
		this.hostAddr = hostAddr;
		init(contextListener);
	}

	/**
	 * 以本地IP和随机端口创建服务器；
	 */
	public WebBoot() {
		this(new InetSocketAddress(PORT_POOL.getAndIncrement()), null);
	}

	/**
	 * 以本地IP和随机端口创建服务器；
	 */
	public WebBoot(ServletContextListener contextListener) {
		this(new InetSocketAddress(PORT_POOL.getAndIncrement()), contextListener);
	}

	public static WebBoot startWithRandomPort(ServletContextListener contextListener) {
		WebBoot server = null;
		RuntimeException error = null;
		for (int i = 0; i < 100; i++) {
			WebBoot serverTemp = new WebBoot(contextListener);
			try {
				serverTemp.start();
				server = serverTemp;
				break;
			} catch (RuntimeException e) {
				// retry;
				error = e;
				LOGGER.warn("Server starting exception! And retry again! --[" + e.getClass().toGenericString() + "] "
						+ e.getMessage());
			}
		}
		if (server != null) {
			return server;
		}
		throw error;
	}

	public WebBoot(int port) {
		this(new InetSocketAddress(port), null);
	}

	public WebBoot(int port, ServletContextListener contextListener) {
		this(new InetSocketAddress(port), contextListener);
	}

	public WebBoot(String host, int port, ServletContextListener contextListener) {
		this(host == null ? new InetSocketAddress(port) : new InetSocketAddress(host, port), contextListener);
	}

	private void init(ServletContextListener contextListener) {
		ServletContextHandler contextHandler = new ServletContextHandler();
		contextHandler.addEventListener(contextListener);

		// Create the SessionHandler (wrapper) to handle the sessions
//		HashSessionManager manager = new HashSessionManager();
//		SessionHandler sessions = new SessionHandler(manager);
		SessionHandler sessions = new SessionHandler();
		contextHandler.setHandler(sessions);

		server.setHandler(contextHandler);
	}

	public void start() {
		try {
			server.start();
		} catch (Exception e) {
			LOGGER.error("Server start error! ---[" + e.getClass().toString() + "] " + e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			LOGGER.error("Server stop error! ---[" + e.getClass().toString() + "] " + e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
