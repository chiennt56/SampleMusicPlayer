package com.example.chiennt.musicplayer.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.chiennt.musicplayer.R;
import com.example.chiennt.musicplayer.model.Song;
import com.example.chiennt.musicplayer.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Nguyen on 12/07/2015.
 */
public class ForegroundService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private final static String LOG_TAG = ForegroundService.class.getName();

    private List<Song> songList;
    private MediaPlayer player;
    private int songPosition;
    private Song currentSong;
    private String songTitle;
    private Notification notification;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Create", "Create");
        songPosition = 0;
        songList = new ArrayList<Song>();
        //create Player
        player = new MediaPlayer();
        getSongList();
        initMusicPlayer();
        currentSong = songList.get(songPosition);
    }

    public List<Song> getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                Song song = new Song();
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                song.setId(thisId);
                song.setArtist(thisArtist);
                song.setName(thisTitle);
                songList.add(song);
            }
            while (musicCursor.moveToNext());
        }
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getName().compareTo(b.getName());
            }
        });
        return songList;
    }

    private void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    /**
     * Play Song
     */
    public void playSong() {
        player.reset();
        Song playSong = songList.get(songPosition);
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            playSong();
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);


            /**
             * Intents for actions in notification
             */
            Intent previousIntent = new Intent(this, ForegroundService.class);
            previousIntent.setAction(Constants.ACTION.PREV_ACTION);
            PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                    previousIntent, 0);
            Intent playIntent = new Intent(this, ForegroundService.class);
            playIntent.setAction(Constants.ACTION.PLAY_ACTION);
            PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                    playIntent, 0);

            Intent nextIntent = new Intent(this, ForegroundService.class);
            nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
            PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                    nextIntent, 0);

            Intent stopIntent = new Intent(this, ForegroundService.class);
            stopIntent.setAction(Constants.ACTION.STOP_ACTION);
            PendingIntent pstopIntent = PendingIntent.getService(this, 0,
                    stopIntent, 0);

            /**
             * Create Notification
             */
            Bitmap icon = BitmapFactory.decodeResource(getResources(),
                    R.drawable.default_song_thumb);
            notification = new NotificationCompat.Builder(this)
                    .setContentTitle(currentSong.getName())
                    .setTicker("Music Player")
                    .setContentText(currentSong.getArtist())
                    .setSmallIcon(R.drawable.ic_library_music_white_48dp)
                    .setLargeIcon(
                            Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setDeleteIntent(pstopIntent)
                    .addAction(android.R.drawable.ic_media_previous, "Prev",
                            ppreviousIntent)
                    .addAction(android.R.drawable.ic_media_play, "Play",
                            pplayIntent)
                    .addAction(android.R.drawable.ic_media_next, "Next",
                            pnextIntent)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, null,
                            pstopIntent).build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification);

        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            Log.i(LOG_TAG, "Clicked Previous");
            playPrev();
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            Log.i(LOG_TAG, "Clicked Play");
            playPasue();
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            Log.i(LOG_TAG, "Clicked Next");
            playNext();
        } else if (intent.getAction().equals(Constants.ACTION.STOP_ACTION)) {
            Log.i(LOG_TAG, "Clicked Stop");
        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            player.stop();
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void playNext() {
        songPosition++;
        if (songPosition == songList.size()) songPosition = 0;
        playSong();
    }

    private void playPasue() {
        if(player.isPlaying()){
            player.pause();
        } else {
            player.start();
        }
    }

    private void playPrev() {
        songPosition--;
        if (songPosition == 0) songPosition = songList.size() - 1;
        playSong();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
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
    }
}
