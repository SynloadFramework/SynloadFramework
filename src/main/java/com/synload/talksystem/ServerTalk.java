package com.synload.talksystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.synload.framework.SynloadFramework;
import com.synload.talksystem.filetransfer.FileControl;
import com.synload.talksystem.filetransfer.FileParts;

public class ServerTalk implements Runnable {
    public static HashMap<Client, ConnectionType> clientToType = new HashMap<Client, ConnectionType>();
    public static List<ConnectionType> types = new ArrayList<ConnectionType>();
    public static void registerClass(ConnectionType clazz){
        types.add(clazz);
    }
    @SuppressWarnings("resource")
    @Override
    public void run() {
        ConnectionType fileTransfer = new ConnectionType();
        fileTransfer.setName("filePart");
        fileTransfer.clazz=FileControl.class;
        try {
            fileTransfer.func = FileControl.class.getMethod("receiveFile", Client.class, ConnectionDocument.class);
        } catch (NoSuchMethodException e1) {
            e1.printStackTrace();
        } catch (SecurityException e1) {
            e1.printStackTrace();
        }
        types.add(fileTransfer);
        try {
            ServerSocket server = new ServerSocket(SynloadFramework.serverTalkPort);
            Socket socket;
            while( (socket = server.accept()) != null ){
                (new Thread(new Client( socket, SynloadFramework.serverTalkKey ))).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
}
