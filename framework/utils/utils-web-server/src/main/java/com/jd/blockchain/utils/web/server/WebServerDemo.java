package com.jd.blockchain.utils.web.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebServerDemo {

	public static void main(String[] args) {
		WebServer server = new WebServer("127.0.0.1", 8899, new File("./").getAbsolutePath());
		server.setContextPath("/test");
		
		server.registServlet("ts", new TestServlet(), "/a");
		
//		server.addListener(new ServletContextListener() {
//			@Override
//			public void contextInitialized(ServletContextEvent sce) {
//				ServletRegistration.Dynamic registration = sce.getServletContext().addServlet("ts", new TestServlet());
//				registration.addMapping("/a");
//				registration.setAsyncSupported(true);
//				registration.setLoadOnStartup(1);;
//			}
//			
//			@Override
//			public void contextDestroyed(ServletContextEvent sce) {
//			}
//		});
		
		server.start();
	}
	
	
	private static class TestServlet extends HttpServlet{

		private static final long serialVersionUID = 6723790617689867229L;
		
		@Override
		protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			System.out.println("service...");
			super.service(req, resp);
		}
		
		@Override
		protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			System.out.println("do get...");
			resp.getWriter().write("test response "+ System.currentTimeMillis());
		}
		
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			System.out.println("do post...");
			resp.getWriter().write("test response "+ System.currentTimeMillis());
		}
		
	}

}
