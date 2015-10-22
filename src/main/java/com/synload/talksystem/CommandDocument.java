package com.synload.talksystem;

import java.util.UUID;

public class CommandDocument extends ConnectionDocument{
    private String command;
    private String[] args;
    public CommandDocument(String command, String[] args){
        super("command",UUID.randomUUID());
        this.command = command;
        this.args = args;
    }
}
