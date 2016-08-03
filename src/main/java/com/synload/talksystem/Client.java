package com.synload.talksystem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import com.synload.framework.Log;
import com.synload.framework.ws.AesUtil;
import com.synload.talksystem.connectionCheck.ConnectionStatus;
import com.synload.talksystem.eventShare.EventShare;

public class Client implements Runnable {
    private String address;
    private boolean authenticated;
    public String key;
    private int port;
    private Thread writer = null;
    private Thread reader = null;
    private ConnectionStatus connectionStatus;
    private boolean reconnect = false;
    private EventShare es = null;
    private boolean keepRunning=true;
    public List<Object> queue = new ArrayList<Object>();
    private boolean closeAfterSend = false;
    private boolean connected = true;
    /**
     * @param address
     * @param port
     * @param closeAfterSend
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    public static Client createConnection(String address, int port, boolean closeAfterSend, String key) throws UnknownHostException, IOException{
        Socket clientSocket = new Socket(address, port);
        Client c = new Client(clientSocket,key);
        c.setAddress(address);
        c.setPort(port);
        c.setCloseAfterSend(closeAfterSend);
        c.setKey(key);
        (new Thread(c)).start();
        return c;
    }
    public static Client createConnection(String address, int port, boolean closeAfterSend, String key, boolean reconnect) throws UnknownHostException, IOException{
        Socket clientSocket = new Socket(address, port);
        Client c = new Client(clientSocket,key);
        c.setAddress(address);
        c.setPort(port);
        c.setConnected(reconnect);
        c.setCloseAfterSend(closeAfterSend);
        c.setKey(key);
        (new Thread(c)).start();
        return c;
    }
    private Socket socket;
    private ExecuteWrite ew;
    private ExecuteRead er;
    private DataInputStream dIn = null;
    private DataOutputStream dOut = null;
    private boolean incoming=false;
    public void closeDown(){
        if(es!=null){
           es.onClose();
        }
        ServerTalk.getConnected().remove(this);
        Log.info("CLOSED client "+Thread.currentThread().getName(), this.getClass());
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        reader.interrupt();
        writer.interrupt();
        //Thread.currentThread().interrupt();

        if(es!=null) {
            reconnect(es, address, port, closeAfterSend, key, reconnect);
        }else{
            reconnect(address, port, closeAfterSend, key, reconnect);
        }

    }

    public static void reconnect( final EventShare es , final String address, final int port, final boolean closeAfterSend, final String key, final boolean reconnect){
        new Thread(){
            public void run(){
                if(reconnect){
                    try {
                        Thread.sleep(5000); // wait 5 seconds
                        Client c = createConnection(address, port, closeAfterSend, key, reconnect);
                        c.setEs(es);
                        es.setEventBusServer(c);
                        es.onConnect();
                    }catch(Exception e){
                        e.printStackTrace();
                        reconnect(es, address, port, closeAfterSend, key, reconnect);
                    }
                }
            }
        }.start();

    }
    public void reconnect(String address, int port, boolean closeAfterSend, String key, boolean reconnect){
        if(reconnect){
            try {
                Thread.sleep(5000); // wait 2 seconds
                Socket clientSocket = new Socket(address, port);
                this.setSocket(clientSocket);
                (new Thread(this)).start();
            }catch(Exception e){
                e.printStackTrace();
                reconnect(address, port, closeAfterSend, key, reconnect);
            }
        }
    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
    
    public DataInputStream getdIn() {
        return dIn;
    }

    public Thread getWriter() {
        return writer;
    }

    public void setWriter(Thread writer) {
        this.writer = writer;
    }

    public Thread getReader() {
        return reader;
    }

    public void setReader(Thread reader) {
        this.reader = reader;
    }

    public ExecuteRead getEr() {
        return er;
    }

    public void setEr(ExecuteRead er) {
        this.er = er;
    }

    public DataOutputStream getdOut() {
        return dOut;
    }

    public void setdOut(DataOutputStream dOut) {
        this.dOut = dOut;
    }

    public void setdIn(DataInputStream dIn) {
        this.dIn = dIn;
    }

    public EventShare getEs() {
        return es;
    }

    public void setEs(EventShare es) {
        this.es = es;
    }

    public List<Object> getQueue() {
        return queue;
    }

    public void setQueue(List<Object> queue) {
        this.queue = queue;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isReconnect() {
        return reconnect;
    }

    public void setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
    }

    public Client(Socket socket, String key){
        this.socket = socket;
        this.setKey(key);
        authenticated=false;
        connectionStatus = new ConnectionStatus(this);
        (new Thread(connectionStatus)).start();
    }
    public Client(Socket socket, String key, boolean incoming){
        this.socket = socket;
        this.setKey(key);
        this.authenticated=false;
        this.incoming = incoming;
        connectionStatus = new ConnectionStatus(this);
        (new Thread(connectionStatus)).start();
    }
    public void reading(){
        Log.debug("Socket "+Thread.currentThread().getName()+" saved as a ", this.getClass());
        ServerTalk.getConnected().add(this);
    }
    public void write(Object data) throws IOException{
        queue.add(data);
    }
    /*
     * COPIED CODE FROM
     * http://stackoverflow.com/questions/14622622/generating-a-
     * random-hex-string-of-length-50-in-java-me-j2me
     */
    public static String getRandomHexString(int numchars) {
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while (sb.length() < numchars) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }
    public static String encrypt(String key, byte[] data)
            throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidParameterSpecException,
            IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, NoSuchPaddingException {
        AesUtil aesUtil = new AesUtil(128, 1000);
        String salt = getRandomHexString(64);
        String iv = getRandomHexString(32);
        String eData = aesUtil.encrypt(salt, iv, key, data);
        String enDat = eData + ":" + salt + ":" + iv;
        return enDat;
    }

    public static byte[] decrypt(String data, String salt, String iv, String key)
            throws NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidParameterSpecException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, NoSuchPaddingException,
            InvalidAlgorithmParameterException,
            IOException {
        AesUtil aesUtil = new AesUtil(128, 1000);
        return aesUtil.decryptByte(salt, iv, key, data);
    }

    public void run() {
        try {
            ew = new ExecuteWrite(new DataOutputStream(socket.getOutputStream()), this, this.isCloseAfterSend());
            writer = new Thread(ew);
            writer.start();
            
            er = new ExecuteRead(new DataInputStream(socket.getInputStream()), this, Thread.currentThread().getContextClassLoader());
            reader = new Thread(er);
            reader.start();
            
            if(!this.closeAfterSend && !incoming) {
                Log.debug("A new main communication socket created! "+Thread.currentThread().getName(), this.getClass());
                queue.add(new ConnectionTypeDocument("communicationSocket", UUID.randomUUID()));
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while(isConnected()){
            //Log.info("Still connected? "+this.getAddress()+":"+this.getPort(), Client.class);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.closeDown();
    }
    
}
