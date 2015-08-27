package com.synload.talksystem.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.synload.eventsystem.EventPublisher;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.talksystem.Client;
import com.synload.talksystem.ConnectionDocument;

public class FileControl {
    public int chunkSizes = 1024*1024*8;
    public static Map<UUID, ArrayList<Integer>> partProgress = new HashMap<UUID, ArrayList<Integer>>();
    public void receiveFile(Client c, ConnectionDocument doc){
        FileParts ft = (FileParts) doc;
        long pos = ft.getPartNumber()*chunkSizes;
        String tmpName = doc.getChain().toString().replaceAll("-", "")+".tmp";
        File f = new File(SynloadFramework.uploadPath+tmpName);
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(f, "rw");
            file.seek(pos);
            file.write(ft.part);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Integer> pProgress = FileControl.partProgress.get(ft.getChain());
        if(pProgress==null){
            FileControl.partProgress.put(ft.getChain(), new ArrayList<Integer>());
        }
        FileControl.partProgress.get(ft.getChain()).add(ft.getPartNumber());
        if(FileControl.partProgress.get(ft.getChain()).size()==(ft.getTotalParts()+1)){
            FileReceiveEvent ev = new FileReceiveEvent( c, ft.getName(), tmpName, SynloadFramework.uploadPath+tmpName, ft.getChain() );
            EventPublisher.raiseEvent(ev, true, "");
            FileControl.partProgress.remove(ft.getChain());
        }
    }
    public void requestFile(Client c, ConnectionDocument doc){
        
    }
    public void sendFile(Client c, File f, UUID chain) throws IOException{
        FileInputStream file = new FileInputStream(f);
        byte[] buffer;
        long byteRead = 0;
        long fileLength = f.length();
        int filePart = 0;
        int totalParts = (int) Math.ceil(fileLength/chunkSizes);
        while(byteRead<fileLength){
            int readSize = (fileLength-(byteRead+chunkSizes)<0)?((int) fileLength%chunkSizes):chunkSizes;
            buffer = new byte[readSize];
            file.read(buffer);
            byteRead+=readSize;
            c.write(new FileParts( buffer, f.getName(), chain, filePart, totalParts));
            filePart++;
        }
    }
}
