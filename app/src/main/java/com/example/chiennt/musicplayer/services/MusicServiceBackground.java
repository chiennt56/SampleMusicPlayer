package com.example.chiennt.musicplayer.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;

import com.example.chiennt.musicplayer.R;
import com.example.chiennt.musicplayer.model.Song;
import com.example.chiennt.musicplayer.ui.activities.BindServiceActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nguyen on 11/07/2015.
 */
public class MusicServiceBackground extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private List<Song> listSongs;
    private MediaPlayer player;
    private int songPosition;
    private final IBinder musicBind = new MusicBinder();
    private String songTitle = "";
    private static final int NOTIFY_ID = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }


    /**
     * Create Service
     */
    @Override
    public void onCreate() {
        super.onCreate();
        songPosition = 0;

        //create Player
        player = new MediaPlayer();
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        listSongs = theSongs;
    }

    public void setSong(int songPosition) {
        this.songPosition = songPosition;
    }

    public class MusicBinder extends Binder {
        public MusicServiceBackground getService() {
            return MusicServiceBackground.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    /**
     * Play Song
     */
    public void playSong() {
        player.reset();
        Song playSong = listSongs.get(songPosition);
        Long currSong = playSong.getId();
        songTitle = playSong.getName();

        //Set URI for Song
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (player.getCurrentPosition() >= 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Intent notIntent = new Intent(this, BindServiceActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification notify = builder.build();

        startForeground(NOTIFY_ID, notify);
    }

    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
    }

    public void playPrev() {
        songPosition--;
        if (songPosition == 0) songPosition = listSongs.size() - 1;
        playSong();
    }

    //skip to next
    public void playNext() {
        songPosition++;
        if (songPosition == listSongs.size()) songPosition = 0;
        playSong();
    }
}
