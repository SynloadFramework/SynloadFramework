package com.synload.talksystem.filetransfer;

import java.util.UUID;

import com.synload.talksystem.ConnectionDocument;

@SuppressWarnings("serial")
public class FileParts extends ConnectionDocument{
    public byte[] part;
    public String name;
    public int partNumber = 0;
    public int totalParts = 0;
    public FileParts(byte[] part, String name, UUID chain, int partNumber, int totalParts){
        super("syn-fp", chain);
        this.part = part;
        this.setTotalParts(totalParts);
        this.setPartNumber(partNumber);
        this.name = name;
    }
    public byte[] getPart() {
        return part;
    }
    public void setPart(byte[] part) {
        this.part = part;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getPartNumber() {
        return partNumber;
    }
    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }
    public int getTotalParts() {
        return totalParts;
    }
    public void setTotalParts(int totalParts) {
        this.totalParts = totalParts;
    }
    
}
