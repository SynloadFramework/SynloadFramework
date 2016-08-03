package com.synload.talksystem.eventShare;

import com.synload.framework.Log;
import com.synload.talksystem.Client;

/**
 * Created by Nathaniel on 8/3/2016.
 */
public class ReconnectThread implements Runnable{
    public EventShare es = null;
    public String address;
    public int port;
    public boolean closeAfterSend;
    public String key;
    public boolean reconnect;
    public ReconnectThread(EventShare es , String address, int port, boolean closeAfterSend, String key, boolean reconnect){
        this.es = es;
        this.address = address;
        this.port = port;
        this.closeAfterSend = closeAfterSend;
        this.key = key;
        this.reconnect = reconnect;
    }
    public void run(){
        if(reconnect){
            Log.info("Reconnecting", Client.class);
            try {
                Thread.sleep(5000); // wait 5 seconds
                Client c = Client.createConnection(address, port, closeAfterSend, key, reconnect);
                c.setEs(es);
                es.setEventBusServer(c);
                es.onConnect();
            }catch(Exception e){
                e.printStackTrace();
                Client.reconnect(es, address, port, closeAfterSend, key, reconnect);
            }
        }else{
            Log.info("Error, reconnect lost value "+reconnect, Client.class);
        }
    }
}