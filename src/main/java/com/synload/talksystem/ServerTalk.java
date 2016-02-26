package com.synload.talksystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.synload.framework.SynloadFramework;
import com.synload.talksystem.commands.CommandControl;
import com.synload.talksystem.filetransfer.FileControl;
import com.synload.talksystem.info.InformationControl;

public class ServerTalk implements Runnable {
    public static List<ConnectionType> types = new ArrayList<ConnectionType>();
    public static List<Client> connected = new ArrayList<Client>();
    public static void registerClass(ConnectionType clazz){
        types.add(clazz);
    }
    @SuppressWarnings("resource")
    @Override
    public void run() {
        
        try {
            ServerSocket server = new ServerSocket(SynloadFramework.serverTalkPort);
            Socket socket;
            while( (socket = server.accept()) != null ){
                Client c = new Client( socket, SynloadFramework.serverTalkKey, true );
                (new Thread(c)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void defaultTypes(){
        
        ConnectionType fileTransfer = new ConnectionType();
        fileTransfer.setName("syn-fp");
        fileTransfer.setClazz(FileControl.class);
        try {
			fileTransfer.setFunc(
			    FileControl.class.getMethod(
			        "receiveFile",
			        Client.class,
			        ConnectionDocument.class
			    )
			);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}
        types.add(fileTransfer);
        
        ConnectionType commandType = new ConnectionType();
        commandType.setName("syn-cmd");
        commandType.setClazz(CommandControl.class);
        try {
            commandType.setFunc(
                CommandControl.class.getMethod(
                    "command",
                    Client.class,
                    ConnectionDocument.class
                )
            );
        } catch ( SecurityException e) {
        } catch (NoSuchMethodException  e) {
            e.printStackTrace();
        }
        types.add(commandType);
        
        
        ConnectionType infoType = new ConnectionType();
        infoType.setName("syn-info");
        infoType.setClazz(InformationControl.class);
        try {
			infoType.setFunc(
			    InformationControl.class.getMethod(
			        "infoReceived",
			        Client.class,
			        ConnectionDocument.class
			    )
			);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
        types.add(infoType);
    }
    public static List<Client> getConnected() {
        return connected;
    }
    public static void setConnected(List<Client> connected) {
        ServerTalk.connected = connected;
    }
    public static List<ConnectionType> getTypes() {
        return types;
    }
    public static void setTypes(List<ConnectionType> types) {
        ServerTalk.types = types;
    }
   
}
