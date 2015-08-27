package com.synload.talksystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.synload.framework.Log;
import com.synload.framework.modules.ModuleLoader;
import com.synload.talksystem.systemMessages.ClassNotFoundMessage;
import com.synload.talksystem.systemMessages.UnrecognizedMessage;

public class Client implements Runnable {
    private String address;
    private int port;
    private boolean keepRunning=true;
    private boolean closeAfterSend = false;
    /**
     * @param address
     * @param port
     * @param closeAfterSend
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    public static Client createConnection(String address, int port, boolean closeAfterSend) throws UnknownHostException, IOException{
        Socket clientSocket = new Socket(address, port);
        Client c = new Client(clientSocket);
        c.setAddress(address);
        c.setPort(port);
        c.setCloseAfterSend(closeAfterSend);
        (new Thread(c)).start();
        return c;
    }
    private Socket socket;
    private ExecuteWrite ew;
    private DataOutputStream dOut = null;
    private DataInputStream dIn = null;
    public void close() throws IOException{
        socket.close();
        Thread.currentThread().stop();
    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    public boolean isCloseAfterSend() {
        return closeAfterSend;
    }

    public void setCloseAfterSend(boolean closeAfterSend) {
        this.closeAfterSend = closeAfterSend;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ExecuteWrite getEw() {
        return ew;
    }

    public void setEw(ExecuteWrite ew) {
        this.ew = ew;
    }

    public DataOutputStream getdOut() {
        return dOut;
    }

    public void setdOut(DataOutputStream dOut) {
        this.dOut = dOut;
    }

    public DataInputStream getdIn() {
        return dIn;
    }

    public void setdIn(DataInputStream dIn) {
        this.dIn = dIn;
    }

    public Client(Socket socket){
        this.socket = socket;
        try {
            dOut = new DataOutputStream(socket.getOutputStream());
            ew = new ExecuteWrite(dOut, this, this.isCloseAfterSend());
            new Thread(ew).start();
            dIn = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void write(Object data) throws IOException{
        ew.queue.add(data);
    }
    @SuppressWarnings("resource")
    public Object read(int length) throws IOException{
        byte[] message = new byte[length];
        dIn.readFully(message, 0, message.length);
        ByteArrayInputStream bas = new ByteArrayInputStream(message);
        ConnectDocumentLoader in = new ConnectDocumentLoader(new ModuleLoader(Thread.currentThread().getContextClassLoader()), bas);
        try {
            Object obj = in.readObject();
            return obj;
        } catch (ClassNotFoundException e) {
            write(new ClassNotFoundMessage());
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
                                    }
                                }
                                if(type!=null){
                                    type.execute(this, (ConnectionDocument) data);
                                }else{
                                    write(new UnrecognizedMessage());
                                }
                            }
                        }
                    }
                }
                Thread.sleep(1L);
            }
            this.socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        Thread.currentThread().interrupt();
        return;
    }
}
