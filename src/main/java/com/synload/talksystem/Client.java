package com.synload.talksystem;

import java.io.ByteArrayInputStream;
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

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.ArrayUtils;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.synload.framework.Log;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.ws.AesUtil;
import com.synload.talksystem.systemMessages.ClassNotFoundMessage;
import com.synload.talksystem.systemMessages.UnrecognizedMessage;

public class Client implements Runnable {
    private String address;
    private boolean authenticated;
    public String key;
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
    private Socket socket;
    private ExecuteWrite ew;
    private ExecuteRead er;
    private DataInputStream dIn = null;
    public void close() throws IOException{
        ServerTalk.getConnected().remove(this);
        socket.close();
        Thread.currentThread().interrupt();
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

    public void setdIn(DataInputStream dIn) {
        this.dIn = dIn;
    }

    public Client(Socket socket, String key){
        this.socket = socket;
        this.setKey(key);
        try {
            ew = new ExecuteWrite(new DataOutputStream(socket.getOutputStream()), this, this.isCloseAfterSend());
            new Thread(ew).start();
            
            er = new ExecuteRead(new DataInputStream(socket.getInputStream()), this);
            new Thread(er).start();
            authenticated=false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void write(Object data) throws IOException{
        ew.queue.add(data);
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
            InvalidAlgorithmParameterException, Base64DecodingException,
            IOException {
        AesUtil aesUtil = new AesUtil(128, 1000);
        return aesUtil.decryptByte(salt, iv, key, data);
    }

    @Override
    public void run() {
        
    }
    
}
