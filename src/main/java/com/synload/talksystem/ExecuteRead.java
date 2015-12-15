package com.synload.talksystem;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.synload.framework.Log;
import com.synload.framework.modules.ModuleLoader;
import com.synload.talksystem.systemMessages.ClassNotFoundMessage;
import com.synload.talksystem.systemMessages.UnrecognizedMessage;

public class ExecuteRead implements Runnable{
    public DataInputStream dIn;
    private Client client;
    private boolean keepRunning=true;
    public List<Object> queue = new ArrayList<Object>(); 
    public ExecuteRead(DataInputStream dIn, Client client){
        this.setClient(client);
        this.setdIn(dIn);
    }
    public Object read(int length) throws IOException{
        byte[] allData = new byte[0];
        int reading = 128*1024;
        int readAllData = 0;
        while(length>0){
            reading = 128*1024;
            if(reading>length){
                reading=length;
            }
            
            byte[] message = new byte[reading];
            dIn.readFully(message);
            readAllData+=reading;
            length -= reading;
            allData = ArrayUtils.addAll(allData, message);
        }
        
        /*byte [] m = null;
        String i = (new String(allData));
        String [] data = i.split(":");
        if(data.length<3){
            System.out.println(i);
        }
        try {
            m = decrypt(
                data[0],
                data[1],
                data[2],
                this.key
            );
        } catch (InvalidAlgorithmParameterException
                | Base64DecodingException | 
                InvalidKeyException | NoSuchAlgorithmException | 
                InvalidKeySpecException | InvalidParameterSpecException | 
                IllegalBlockSizeException | BadPaddingException | 
                NoSuchPaddingException e) {
            e.printStackTrace();
        }*/
        ByteArrayInputStream bas = new ByteArrayInputStream(allData);
        ConnectDocumentLoader in = new ConnectDocumentLoader(new ModuleLoader(Thread.currentThread().getContextClassLoader()), bas);
        try {
            Object obj = in.readObject();
            return obj;
        } catch (ClassNotFoundException e) {
            
        }
        return null;
    }

    @Override
    public void run() {
        
        try {
            while(this.isKeepRunning()){
                if(dIn.available()>0){
                    int length = dIn.readInt();
                    if(length>0){
                        Object data = read(length);
                        //Log.debug(data.getClass().getName(), Client.class);
                        if(ConnectionDocument.class.isInstance(data)){
                            if(UnrecognizedMessage.class.isInstance(data)){
                                Log.error("Unrecognized Connection Type", Client.class);
                            }else if(ClassNotFoundMessage.class.isInstance(data)){
                                Log.error("Unrecognized Connection Type", Client.class);
                            }else{
                                ConnectionType type = null;
                                ConnectionDocument doc = (ConnectionDocument) data;
                                List<ConnectionType> types = new ArrayList<ConnectionType>(ServerTalk.types);
                                for(ConnectionType t : types){
                                    if(t.getName().equals(doc.getTypeName())){
                                           type = t;
                                           break;
                                    }
                                }
                                if(type!=null){
                                    type.execute(this.getClient(), (ConnectionDocument) data);
                                }else{
                                    this.getClient().write(new UnrecognizedMessage());
                                }
                            }
                        }
                    }
                }
                Thread.sleep(1L);
            }
            this.getClient().getSocket().close();
            ServerTalk.getConnected().remove(this);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            ServerTalk.getConnected().remove(this);
        }
        Thread.currentThread().interrupt();
        return;
    }
    public DataInputStream getdIn() {
        return dIn;
    }
    public void setdIn(DataInputStream dIn) {
        this.dIn = dIn;
    }
    public Client getClient() {
        return client;
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public boolean isKeepRunning() {
        return keepRunning;
    }
    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }
    public List<Object> getQueue() {
        return queue;
    }
    public void setQueue(List<Object> queue) {
        this.queue = queue;
    }

}
