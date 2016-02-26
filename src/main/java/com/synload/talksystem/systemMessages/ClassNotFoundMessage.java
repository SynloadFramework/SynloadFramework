package com.synload.talksystem.systemMessages;

import java.util.UUID;

import com.synload.talksystem.ConnectionDocument;

@SuppressWarnings("serial")
public class ClassNotFoundMessage extends ConnectionDocument {

    public ClassNotFoundMessage() {
        super("Unknown", UUID.randomUUID());
    }
    
}
