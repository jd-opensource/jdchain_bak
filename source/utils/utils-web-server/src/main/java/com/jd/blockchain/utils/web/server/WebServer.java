package com.jd.blockchain.utils.web.server;

import java.io.File;
import java.net.InetSocketAddress;

import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
//import org.eclipse.jetty.util.resource.EmptyResource;
import org.eclipse.jetty.util.resource.EmptyResource;

/**
 * WebServer 实现为一个可以通过内嵌方式启动的 web 容器；
 * 
 * @author haiq
 *
 */
public class WebServer {
	private Server server;


	protected String resourceRootDir;

	private ServletContextHandler contextHandler;

//	private ServletRegisterManager servletRegisterManager = new ServletRegisterManager();

	/**
	 * 创建一个 WebServer 实例；
	 * 
	 * @param hostAddress
	 * @param resourceRootDir
	 *            资源根目录；
	 */
	public WebServer(InetSocketAddress hostAddress, String resourceRootDir) {
		this.resourceRootDir = resourceRootDir;
		this.server = new Server(hostAddress);
		init();
	}

	/**
	 * 创建一个 WebServer 实例；
	 * 
	 * @param host
	 *            主机地址；
	 * @param port
	 *            端口；
	 * @param resourceRootDir
	 *            资源根目录；
	 */
	public WebServer(String host, int port, String resourceRootDir) {
		this(new InetSocketAddress(host, port), resourceRootDir);
	}


	/**
	 * 创建一个 WebServer 实例；
	 * 
	 * @param port
	 *            端口
	 */
	public WebServer(String host, int port) {
		this(new InetSocketAddress(host, port), null);
	}
	
	/**
	 * 创建一个 WebServer 实例；
	 * 
	 * @param port
	 *            端口
	 * @param resourceRootDir
	 *            资源根目录；
	 */
	public WebServer(int port, String resourceRootDir) {
		this(new InetSocketAddress(port), resourceRootDir);
	}
	
	
	/**
	 * 创建一个 WebServer 实例；
	 * 
	 * @param port
	 *            端口
	 */
	public WebServer(int port) {
		this(new InetSocketAddress(port), null);
	}
	
	public void setContextPath(String contextPath){
		this.contextHandler.setContextPath(contextPath);
	}

	private void init() {
		contextHandler = new ServletContextHandler();
		
		if (resourceRootDir != null) {
			File resRootDir = new File(resourceRootDir);
			if (!resRootDir.isDirectory()) {
				throw new IllegalArgumentException(
						"The path specified as the resource root directory does not exist or isn't a directory!");
			}
			contextHandler.setResourceBase(resourceRootDir);
		}else{
			contextHandler.setBaseResource(EmptyResource.INSTANCE);
		}

		server.setHandler(contextHandler);
	}

	/**
	 * 增加 ServletContextListener;
	 * 
	 * @param listener
	 */
	public void addListener(ServletContextListener listener) {
		contextHandler.addEventListener(listener);
	}

	/**
	 * 增加 ServletContextAttributeListener;
	 * 
	 * @param listener
	 */
	public void addListener(ServletContextAttributeListener listener) {
		contextHandler.addEventListener(listener);
	}

	/**
	 * 增加 ServletRequestListener;
	 * 
	 * @param listener
	 */
	public void addListener(ServletRequestListener listener) {
		contextHandler.addEventListener(listener);
	}

	/**
	 * 增加 ServletRequestListener;
	 * 
	 * @param listener
	 */
	public void addListener(ServletRequestAttributeListener listener) {
		contextHandler.addEventListener(listener);
	}

	/**
	 * 注册 Servlet；
	 * 
	 * @param name
	 * @param servlet
	 * @param mapping
	 */
//	public void registServlet(String name, HttpServlet servlet, String mapping) {
//		ServletSetting setting = new ServletSetting();
//		setting.addMapping(mapping);
//		registServlet(name, servlet, setting);
//	}

	public void registServlet(String name, HttpServlet servlet, String... mappings) {
		ServletHolder servletHolder = new ServletHolder(name, servlet);
		contextHandler.getServletHandler().addServlet(servletHolder);
		ServletMapping servletMapping = new ServletMapping();
		servletMapping.setServletName(name);
		servletMapping.setPathSpecs(mappings);
		
		contextHandler.getServletHandler().addServletMapping(servletMapping);
//		for (String mapping : setting.getMappings()) {
//			contextHandler.addServlet(servletHolder, mapping);
//		}
	}

	public void start() {
		try {
			server.start();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void stop() {
		try {
			server.stop();
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e.getMessage(), e);
		}
	}

//	private static class ServletRegisterManager implements ServletContextListener {
//		private Map<String, ServletRegisterInfo> servletRegisterMap = new LinkedHashMap<String, ServletRegisterInfo>();
//
//		public void register(String name, Servlet servlet, ServletSetting setting) {
//			servletRegisterMap.put(name, new ServletRegisterInfo(name, servlet, setting));
//		}
//
//		@Override
//		public void contextInitialized(ServletContextEvent sce) {
//			ServletContext context = sce.getServletContext();
//			for (ServletRegisterInfo regInfo : servletRegisterMap.values()) {
//				ServletRegistration.Dynamic registration = context.addServlet(regInfo.getName(), regInfo.getServlet());
//				registration.addMapping(regInfo.getSetting().getMappings().toArray(new String[0]));
//				if (regInfo.getSetting().getLoadOnStartup() != null) {
//					registration.setLoadOnStartup(regInfo.getSetting().getLoadOnStartup());
//				}
//				if (regInfo.getSetting().getAsyncSupported() != null) {
//					registration.setAsyncSupported(regInfo.getSetting().getAsyncSupported());
//				}
//			}
//		}
//
//		@Override
//		public void contextDestroyed(ServletContextEvent sce) {
//		}
//
//	}

//	private static class ServletRegisterInfo {
//
//		private String name;
//
//		private Servlet servlet;
//
//		private ServletSetting setting;
//
//		public ServletRegisterInfo(String name, Servlet servlet, ServletSetting setting) {
//			this.name = name;
//			this.servlet = servlet;
//			this.setting = setting;
//		}
//
//		public String getName() {
//			return name;
//		}
//
//		public Servlet getServlet() {
//			return servlet;
//		}
//
//		public ServletSetting getSetting() {
//			return setting;
//		}
//
//	}
}
