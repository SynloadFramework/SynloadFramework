package com.synload.framework.handlers;

public class DelayedRequest extends Request {
    public int delay = 0;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}