package com.example.chiennt.musicplayer.model.event;

import android.os.Bundle;

/**
 * Created by Nguyen on 12/07/2015.
 */
public class ServiceEvent {

    public enum TYPE {START, STOP}

    public TYPE type;
    public Bundle bundle;

    public ServiceEvent(TYPE type, Bundle bundle){
        this.bundle = bundle;
        this.type = type;
    }

    public ServiceEvent(TYPE type){
        this.type = type;
    }
}
