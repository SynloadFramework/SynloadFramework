package com.synload.talksystem;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import com.synload.framework.Log;

class ExecuteWrite implements Runnable{
    public DataOutputStream dOut;
    private Client client;
    private boolean keepRunning=true;
    private boolean readData = false;
    private boolean connectError = false;
    private boolean closeAfterSend = false; 
    public ExecuteWrite(DataOutputStream dOut, Client client, boolean closeAfterSend){
        this.setClient(client);
        this.setdOut(dOut);
        this.closeAfterSend = closeAfterSend;
    }
    public void run() {
        Log.debug("New Execute Write "+Thread.currentThread().getName(), this.getClass());
        while(this.isKeepRunning() && !this.getClient().getSocket().isOutputShutdown()){
            if(this.getClient().queue.size()>0){
                if(this.getClient().queue.size()>10 && connectError==false){
                    int senders = (int) Math.ceil(this.getClient().queue.size()/10);
                    //Log.info("Created "+senders+" data connections to the file bridge", ExecuteWrite.class);
                    for(int g=0;g<senders;g++){
                        try {
                            Client tempFileBridge = Client.createConnection(client.getAddress(), client.getPort(), true, client.getKey());
                            for(int x=0;x<10;x++){
                                if(this.getClient().queue.size()>0){
                                    Object item = this.getClient().queue.get(0);
                                    this.getClient().queue.remove(0);
                                    tempFileBridge.queue.add(item);
                                }
                            }
                        } catch (IOException e) {
                            //e.printStackTrace();
                            connectError=true;
                            Log.debug("Connection error, sending on same client connection", this.getClass());
                        }
                    }
                    if(this.getQueue().size()==0 && this.isCloseAfterSend()){
                        //this.setKeepRunning(false);
                        this.getClient().close();
                    }
                }else{
                    if(connectError){
                        connectError=false; 
                    }
                    Object data = this.getClient().queue.get(0);
                    this.getClient().queue.remove(0);
                    ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
                    ObjectOutputStream out;
                    try {
                        out = new ObjectOutputStream(arrayOut);
                        out.writeObject(data);
                        IOUtils.closeQuietly(out);
                        IOUtils.closeQuietly(arrayOut);
                        byte[] bytes = arrayOut.toByteArray();
                        /*try {
                            bytes = Client.encrypt(client.key, bytes).getBytes();
                        } catch (InvalidKeyException | NoSuchAlgorithmException
                                | InvalidKeySpecException
                                | InvalidParameterSpecException
                                | IllegalBlockSizeException
                                | BadPaddingException
                                | NoSuchPaddingException e) {
                            e.printStackTrace();
                        }*/
                        dOut.writeInt(bytes.length);
                        //Log.debug("wrote "+bytes.length+" bytes", this.getClass());
                        dOut.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(this.getQueue().size()==0 && this.isCloseAfterSend()){
                        //this.setKeepRunning(false);
                        this.getClient().close();
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
        this.getClient().close();
        Log.debug("Error could not close thread", this.getClass());
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
        return getClient().queue;
    }
    public void setQueue(List<Object> queue) {
        this.getClient().queue = queue;
    }
    
}