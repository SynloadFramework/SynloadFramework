package com.synload.framework.http;

import java.lang.reflect.Method;

import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.http.annotations.Get;
import com.synload.framework.http.annotations.Http;
import com.synload.framework.http.annotations.MimeType;
import com.synload.framework.http.annotations.OnlyIf;
import com.synload.framework.http.annotations.Post;
import com.synload.framework.http.modules.HTTPResponse;

public class HTTPRegistry {
	public static void moduleLoad(Class<?> clazz, Method m){
		if(m.isAnnotationPresent(Get.class) || m.isAnnotationPresent(Post.class) || m.isAnnotationPresent(Http.class)){
			if(m.isAnnotationPresent(OnlyIf.class)){
				OnlyIf oi = m.getAnnotation(OnlyIf.class);
				if(SynloadFramework.getProp().containsKey(oi.property()) && Boolean.valueOf(SynloadFramework.getProp().getProperty(oi.property()))!=oi.is()){
					return; // do not register this http response, disabled
				}
			}
			String mimetype=null;
			if(m.isAnnotationPresent(MimeType.class)){
				Log.debug("Detected MimeType ", HTTPRegistry.class);
				MimeType mt = m.getAnnotation(MimeType.class);
				mimetype = mt.value();
				Log.debug("MimeType found "+mt.value(), HTTPRegistry.class);
			}else{
				Log.debug("Did NOT Detected MimeType ", HTTPRegistry.class);
			}
			if(m.isAnnotationPresent(Get.class)){
				Get get = m.getAnnotation(Get.class);
				if(HTTPRouting.getRoutes().containsKey(get.value())){
					HTTPRouting.getRoutes().remove(get.value());
				}
				HTTPRouting.addRoutes(get.value(), new HTTPResponse(clazz, m.getName(), "get", mimetype));
				Log.debug("Registered path "+get.value()+" as get", HTTPRegistry.class);
			}
			if(m.isAnnotationPresent(Post.class)){
				Post post = m.getAnnotation(Post.class);
				if(HTTPRouting.getRoutes().containsKey(post.value())){
					HTTPRouting.getRoutes().remove(post.value());
				}
				HTTPRouting.addRoutes(post.value(), new HTTPResponse(clazz, m.getName(), "post", mimetype));
				Log.debug("Registered path "+post.value()+" as post", HTTPRegistry.class);
			}
			if(m.isAnnotationPresent(Http.class)){
				Http http = m.getAnnotation(Http.class);
				if(HTTPRouting.getRoutes().containsKey(http.path())){
					HTTPRouting.getRoutes().remove(http.path());
				}
				HTTPRouting.addRoutes(http.path(), new HTTPResponse(clazz, m.getName(), http.method(), mimetype));
				Log.debug("Registered path "+http.path()+" as "+http.method(), HTTPRegistry.class);
			}
		}
		// go back to rest of module loading
	}
}
