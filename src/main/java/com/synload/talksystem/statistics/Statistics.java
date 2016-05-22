package com.synload.talksystem.statistics;

import com.synload.framework.SynloadFramework;

import java.util.UUID;

/**
 * Created by Nathaniel on 5/20/2016.
 */
public class Statistics implements Runnable {
    public void run() {
        while(true) {
            if (SynloadFramework.masterControl.getSocket().isConnected()) {
                try {
                    SynloadFramework.masterControl.write(
                        new StatisticDocument(
                            UUID.randomUUID()
                        )
                    );
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            try{
                Thread.sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
