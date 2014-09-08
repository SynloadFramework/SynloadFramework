package com.synload.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;

public class OOnPage {
	
	public static Map<String, Map<String, List<WSHandler>>> ObjectsClients = new HashMap<String,Map<String,List<WSHandler>>>();
	public static Map<WSHandler, Map<String, List<String>>> clientsObjects  = new HashMap<WSHandler,Map<String,List<String>>>();
	
	public static void add(WSHandler client, String reference, String id){
		if(SynloadFramework.debug){
			System.out.println(client.session.getRemoteAddress().getAddress().getHostAddress()+" "+reference+" "+id);
		}
		if(ObjectsClients.containsKey(reference)){
			if(ObjectsClients.get(reference).containsKey(id)){
				if(!ObjectsClients.get(reference).get(id).contains(id)){
					ObjectsClients.get(reference).get(id).add(client);
				}
			}else{
				List<WSHandler> i = new ArrayList<WSHandler>();
				i.add(client);
				ObjectsClients.get(reference).put(id, i);
			}
		}else{
			HashMap<String,List<WSHandler>> g = new HashMap<String,List<WSHandler>>();
			List<WSHandler> i = new ArrayList<WSHandler>();
			i.add(client);
			g.put(id, i);
			ObjectsClients.put(reference, g);
		}
		if(clientsObjects.containsKey(client)){
			if(clientsObjects.get(client).containsKey(reference)){
				if(!clientsObjects.get(client).get(reference).contains(id)){
					clientsObjects.get(client).get(reference).add(id);
				}
			}else{
				List<String> i = new ArrayList<String>();
				i.add(id);
				clientsObjects.get(client).put(reference, i);
			}
		}else{
			Map<String, List<String>> g = new HashMap<String, List<String>>();
			List<String> i = new ArrayList<String>();
			i.add(id);
			g.put(reference, i);
			clientsObjects.put(client, g);
		}
	}
	
	public static void remove(WSHandler client, String reference, String id){
		if(SynloadFramework.debug){
			System.out.println("Remove: "+client.session.getRemoteAddress().getAddress().getHostAddress()+" "+reference+" "+id);
		}
		if(ObjectsClients.containsKey(reference)){
			if(ObjectsClients.get(reference).containsKey(id)){
				ObjectsClients.get(reference).get(id).remove(client);
				if(ObjectsClients.get(reference).get(id).size()==0){
					ObjectsClients.get(reference).remove(id);
				}
			}
		}
		if(clientsObjects.containsKey(client)){
			if(clientsObjects.get(client).containsKey(reference)){
				clientsObjects.get(client).get(reference).remove(id);
				if(clientsObjects.get(client).get(reference).size()==0){
					clientsObjects.get(client).remove(reference);
				}
			}
		}
	}
	
	public static List<WSHandler> getClients(String reference, String id){
		List<WSHandler> g = new ArrayList<WSHandler>();
		if(ObjectsClients.containsKey(reference)){
			if(ObjectsClients.get(reference).containsKey(id)){
				g = new ArrayList<WSHandler>(ObjectsClients.get(reference).get(id));
			}
		}
		return g;
	}
	
	public static void newPage(WSHandler client, Response r){
		if(r.getObjects().size()>0){
			OOnPage.removeClient(client);
			for(Entry<String, List<String>> o:r.getObjects().entrySet()){
				for(String g:o.getValue()){
					OOnPage.add(client, o.getKey(), g);
				}
			}
		}
	}
	
	public static void removeClient(WSHandler client){
		if(clientsObjects.containsKey(client)){
			Map<String, List<String>> m = new HashMap<String, List<String>>(clientsObjects.get(client));
			for(Entry<String, List<String>> reference:m.entrySet()){
				List<String> h = new ArrayList<String>(reference.getValue());
				for(String id:h){
					OOnPage.remove(client, String.valueOf(reference.getKey()), String.valueOf(id));
				}
			}
		}
	}
	
}
