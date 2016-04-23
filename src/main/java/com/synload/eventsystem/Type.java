package com.synload.eventsystem;

public enum Type {
    HTTP(1), WEBSOCKET(2), OTHER(3);
    int val;

    Type(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

}