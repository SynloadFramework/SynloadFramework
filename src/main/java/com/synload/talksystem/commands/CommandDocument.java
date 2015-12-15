package com.synload.talksystem.commands;

import java.util.UUID;

import com.synload.talksystem.ConnectionDocument;

@SuppressWarnings("serial")
public class CommandDocument extends ConnectionDocument{
    private String command;
    private String[] args;
    public CommandDocument(String command, String[] args){
        super("syn-cmd", UUID.randomUUID());
        this.command = command;
        this.args = args;
    }
    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public String[] getArgs() {
        return args;
    }
    public void setArgs(String[] args) {
        this.args = args;
    }
}
