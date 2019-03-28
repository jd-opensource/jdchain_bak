package com.jd.blockchain.utils.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class WebTest {
	
	protected static WebBoot server;
	
	@Autowired
	protected WebApplicationContext wac;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		WebBoot booter = server;
		server = null;
		if (booter != null) {
			booter.stop();
		}
	}

	@Before
	public void setUp() throws Exception {
		// 启动 JETTY;
		if (server == null) {
			ServletRegister servletRegister = new ServletRegister();
			DispatcherServlet dispachterServlet = new DispatcherServlet(wac);
			servletRegister.addServlet("/service/*", dispachterServlet);
			WebBoot booter = WebBoot.startWithRandomPort(servletRegister);
			
			server = booter;
			afterWebStarted(booter.getPort());
		}
	}
	
	protected void afterWebStarted(int port){
	}
	
	@After
	public void tearDown() throws Exception {
	}
}
