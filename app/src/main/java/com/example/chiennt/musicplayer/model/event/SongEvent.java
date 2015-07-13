package com.example.chiennt.musicplayer.model.event;

import android.os.Bundle;

/**
 * Created by Nguyen on 11/07/2015.
 */
public class SongEvent {
    public enum TYPE {OPEN}

    public TYPE type;
    public Bundle bundle;

    public SongEvent(TYPE type, Bundle bundle){
        this.bundle = bundle;
        this.type = type;
    }
}
