package com.synload.talksystem.connectionCheck;

import com.synload.talksystem.Client;

/**
 * Created by Nathaniel on 8/2/2016.
 */
public class ConnectionStatus implements Runnable{
    public ConnectionStatus(Client client){
        this.client = client;
    }
    public boolean responded=false;
    public void response(int rId){
        if(rId==currentId){
            responded=true;
        }else{
            responded=false;
        }
    }
    public int currentId=0;
    public Client client;
    public void run(){
        while(client.isConnected()) {
            try {
                client.write(new Ping(currentId));
                responded=false;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(500);
            }catch(Exception e){
                e.printStackTrace();
            }
            if(responded==false){
                client.setConnected(false);
            }
        }
    }
}
