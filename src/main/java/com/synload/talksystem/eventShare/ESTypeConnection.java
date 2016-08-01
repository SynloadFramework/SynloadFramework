package com.synload.talksystem.eventShare;

import java.io.Serializable;

/**
 * Created by Nathaniel on 7/29/2016.
 */

public class ESTypeConnection implements Serializable {
    public boolean shareEvents = false;
    public ESTypeConnection(boolean shareEvents){
        this.shareEvents = shareEvents;
    }
    public boolean isShareEvents() {
        return shareEvents;
    }
    public void setShareEvents(boolean shareEvents) {
        this.shareEvents = shareEvents;
    }
}
