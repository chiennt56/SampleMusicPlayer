package com.example.chiennt.musicplayer.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Nguyen on 11/07/2015.
 */
public class Song implements Parcelable {

    public static final String KEY = Song.class.getName();

    private String name;
    private Long id;
    private String lyrics;
    private long timeCount;
    private String artist;
    private String singer;
    private Drawable thumb;

    public Song(long songID, String songTitle, String songArtist,  Drawable thumb) {
        id = songID;
        name = songTitle;
        artist = songArtist;
        this.thumb = thumb;
    }

    public Song(){};

    public Drawable getThumb() {
        return thumb;
    }

    public void setThumb(Drawable thumb) {
        this.thumb = thumb;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public long getTimeCount() {
        return timeCount;
    }

    public void setTimeCount(long timeCount) {
        this.timeCount = timeCount;
    }

    public String toString() {
        return "Name: " + getName() + ", "
                + "ID: " + getId() + ", "
                + "Lyric: " + getId() + ", "
                + "Time Count: " + getId();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.artist);
    }


}
