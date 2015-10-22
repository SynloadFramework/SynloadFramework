package com.synload.talksystem.systemMessages;

import java.util.UUID;

import com.synload.talksystem.ConnectionDocument;

@SuppressWarnings("serial")
public class UnrecognizedMessage extends ConnectionDocument {

    public UnrecognizedMessage() {
        super("Unrecognized", UUID.randomUUID());
    }
}
