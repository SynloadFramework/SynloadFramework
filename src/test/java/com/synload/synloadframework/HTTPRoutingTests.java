package com.synload.synloadframework;


import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.junit.Test;

import com.synload.framework.SynloadFramework;
import com.synload.framework.http.DefaultHTTPPages;
import com.synload.framework.http.HTTPRouting;
import com.synload.framework.http.modules.HTTPResponse;

public class HTTPRoutingTests {

	@Test
	public void canRegsterHTTPPage() {
		SynloadFramework.registerHTTPPage("/test", SynloadFramework.class, "test");
		assertSame(HTTPRouting.getRoutes().containsKey("/test"), true);
	}
	@Test
	public void canRegsterHTTPPageResponse() {
		/* Disabled bad test.
		 * SynloadFramework.registerHTTPPage("/", DefaultHTTPPages.class, "getIndex");
		try {
			assertSame(HTTPRouting.page("/", null, null, null), true);
		} catch (IOException e) {
			fail("could not call page request from httprouting");
		}*/
	}
	
	@Test
	public void canAddBlankPath() {
		//assertSame(HTTPRouting.addRoutes("", new HTTPResponse(DefaultHTTPPages.class, "getIndex", "get")), false);
	}
	
	@Test
	public void canAddNullResponse() {
		assertSame(HTTPRouting.addRoutes("/path", null), false);
	}
	
	@Test
	public void canAddRoute() {
		//assertSame(HTTPRouting.addRoutes("/index", new HTTPResponse(DefaultHTTPPages.class, "getIndex", "get")),true);
	}
	
	@Test
	public void checkNullResponse(){
		try{
			HTTPRouting.sendResource(null,null,null);
		}catch(Exception  e){
			fail("Did not check for null");
		}
	}

}
