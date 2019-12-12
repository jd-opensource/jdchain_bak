package com.jd.blockchain.utils.test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;

public class ServletRegister implements ServletContextListener {
	
	private Map<String, Servlet> servlets = new LinkedHashMap<String, Servlet>();
	
	public ServletRegister() {
	}
	
	public void addServlet(String mapping, Servlet servlet){
		if (servlets.containsKey(mapping)) {
			throw new IllegalArgumentException("The same mapping already exist! --[mapping="+mapping+"]");
		}
		servlets.put(mapping, servlet);
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		int i=1;
		for (Entry<String, Servlet> entry : servlets.entrySet()) {
			ServletRegistration.Dynamic serviceDispatcherServlet = servletContext.addServlet("servlet-" + i, entry.getValue());
			serviceDispatcherServlet.addMapping(entry.getKey());
			serviceDispatcherServlet.setLoadOnStartup(1);
			serviceDispatcherServlet.setAsyncSupported(true);
			i++;
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
