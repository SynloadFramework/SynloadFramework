package com.synload.framework.http;

import java.lang.reflect.Method;

import com.synload.framework.SynloadFramework;
import com.synload.framework.http.annotations.Get;
import com.synload.framework.http.annotations.Http;
import com.synload.framework.http.annotations.OnlyIf;
import com.synload.framework.http.annotations.Post;
import com.synload.framework.http.modules.HTTPResponse;

public class HTTPRegistry {
	public static void moduleLoad(Class<?> clazz, Method m){
		if(m.isAnnotationPresent(OnlyIf.class)){
			OnlyIf oi = m.getAnnotation(OnlyIf.class);
			if(Boolean.valueOf(SynloadFramework.getProp().getProperty(oi.property()))!=oi.is()){
				return; // do not register this http response, disabled
			}
		}
		if(m.isAnnotationPresent(Get.class)){
			Get get = m.getAnnotation(Get.class);
			HTTPRouting.addRoutes(get.path(), new HTTPResponse(clazz, m.getName(), "get"));
		}else if(m.isAnnotationPresent(Post.class)){
			Post post = m.getAnnotation(Post.class);
			HTTPRouting.addRoutes(post.path(), new HTTPResponse(clazz, m.getName(), "post"));
		}else if(m.isAnnotationPresent(Http.class)){
			Http http = m.getAnnotation(Http.class);
			HTTPRouting.addRoutes(http.path(), new HTTPResponse(clazz, m.getName(), http.method()));
		}
		// go back to rest of module loading
	}
}
