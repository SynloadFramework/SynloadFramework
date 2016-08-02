package com.synload.talksystem.connectionCheck;

import java.io.Serializable;

/**
 * Created by Nathaniel on 8/2/2016.
 */
public class Ping implements Serializable {
    public Ping(int id){
        this.id = id;
    }
    public int id = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
