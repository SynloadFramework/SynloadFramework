package com.synload.talksystem;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.io.IOUtils;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.synload.framework.Log;

class ExecuteWrite implements Runnable{
    public DataOutputStream dOut;
    private Client client;
    private boolean keepRunning=true;
    private boolean closeAfterSend = false;
    public List<Object> queue = new ArrayList<Object>(); 
    public ExecuteWrite(DataOutputStream dOut, Client client, boolean closeAfterSend){
        this.setClient(client);
        this.setdOut(dOut);
    }
    @Override
    public void run() {
        while(this.isKeepRunning()){
            if(this.queue.size()>0){
                if(this.queue.size()>10){
                    int senders = (int) Math.ceil(this.queue.size()/10);
                    Log.info("Created "+senders+" data connections to the file bridge", ExecuteWrite.class);
                    for(int g=0;g<senders;g++){
                        try {
                            Client tempFileBridge = Client.createConnection(client.getAddress(), client.getPort(), true, client.getKey());
                            for(int x=0;x<10;x++){
                                if(this.queue.size()>0){
                                    Object item = this.queue.get(0);
                                    this.queue.remove(0);
                                    tempFileBridge.getEw().queue.add(item);
                                    
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(this.getQueue().size()==0 && this.isCloseAfterSend()){
                        this.setKeepRunning(false);
                        try {
                            this.getClient().close();
                            Thread.currentThread().stop();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }else{
                    Object data = this.queue.get(0);
                    this.queue.remove(0);
                    ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
                    ObjectOutputStream out;
                    try {
                        out = new ObjectOutputStream(arrayOut);
                        out.writeObject(data);
                        IOUtils.closeQuietly(out);
                        IOUtils.closeQuietly(arrayOut);
                        byte[] bytes = arrayOut.toByteArray();
                        try {
                            bytes = Client.encrypt(client.key, bytes).getBytes();
                            dOut.writeInt(bytes.length);
                            dOut.write(bytes);
                        } catch (InvalidKeyException | NoSuchAlgorithmException
                                | InvalidKeySpecException
                                | InvalidParameterSpecException
                                | IllegalBlockSizeException
                                | BadPaddingException
                                | NoSuchPaddingException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(this.getQueue().size()==0 && this.isCloseAfterSend()){
                        this.setKeepRunning(false);
                        try {
                            
                            this.getClient().close();
                            Thread.currentThread().stop();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }else{
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Thread.currentThread().interrupt();
        return;
    }
    public boolean isCloseAfterSend() {
        return closeAfterSend;
    }
    public void setCloseAfterSend(boolean closeAfterSend) {
        this.closeAfterSend = closeAfterSend;
    }
    public boolean isKeepRunning() {
        return keepRunning;
    }
    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }
    public DataOutputStream getdOut() {
        return dOut;
    }
    public void setdOut(DataOutputStream dOut) {
        this.dOut = dOut;
    }
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public List<Object> getQueue() {
        return queue;
    }
    public void setQueue(List<Object> queue) {
        this.queue = queue;
    }
    
}