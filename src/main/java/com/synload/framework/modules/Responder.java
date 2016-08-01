package com.synload.framework.modules;

import com.synload.framework.handlers.Data;
import com.synload.framework.handlers.Response;

import java.io.Serializable;

/**
 * Created by Nathaniel on 7/27/2016.
 */
public abstract class Responder implements Serializable {
    public void send(String data){}
    public void send(Response r){}
    public void send(Data r){}
}
