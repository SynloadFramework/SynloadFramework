package com.synload.talksystem;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class ConnectionType implements Serializable {
    public Class<?> clazz=null;
    public Method func;
    private String name;
    public ConnectionType(){
        
    }
    public ConnectionType(String name){
        this.name = name;
    }
    public void execute(Client client, ConnectionDocument doc){
        try {
            func.invoke(clazz.newInstance(), client, doc);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
