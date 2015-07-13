package com.example.chiennt.musicplayer.ui.activities;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.MediaController;

import com.example.chiennt.musicplayer.R;
import com.example.chiennt.musicplayer.adapter.SongAdapter;
import com.example.chiennt.musicplayer.controllers.MusicController;
import com.example.chiennt.musicplayer.model.Song;
import com.example.chiennt.musicplayer.model.event.SongEvent;
import com.example.chiennt.musicplayer.services.MusicServiceBackground;
import com.example.chiennt.musicplayer.services.MusicServiceBackground.MusicBinder;
import com.example.chiennt.musicplayer.ui.wigets.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;



public class BindServiceActivity extends BaseActivity implements MediaController.MediaPlayerControl, View.OnClickListener {

    public static final String TAG = BindServiceActivity.class.getName();

    private RecyclerView rvSongList;
    private Intent playIntent;
    private MusicServiceBackground musicService;
    private boolean musicBound = false;
    private ArrayList<Song> songList;
    private ServiceConnection serviceConnection;
    private SongAdapter songAdapter;
    private MusicController musicController;
    private boolean paused = false, playbackPaused = false;

    @Override
    int getLayoutSource(Bundle savedInstanceState) {
        return R.layout.activity_bind_service;
    }

    @Override
    void initVaribles(Bundle saveInstanceState) {
        rvSongList = (RecyclerView) findViewById(R.id.rvSongList);
        rvSongList.setLayoutManager(new LinearLayoutManager(this));
        rvSongList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider), false, true));
        songList = new ArrayList<Song>();
        musicService = new MusicServiceBackground();
        setController();
    }

    @Override
    void initData(Bundle saveInstanceState) {

        songAdapter = new SongAdapter(this, getSongList());
        rvSongList.setAdapter(songAdapter);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicBinder binder = (MusicBinder) service;
                //get service
                musicService = binder.getService();
                //pass list
                musicService.setList(songList);
                musicBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };
    }

    private void setController() {
        //set the controller up
        musicController = new MusicController(this);
        musicController.setAnchorView(findViewById(R.id.rvSongList));
        musicController.setMediaPlayer(this);
        musicController.setEnabled(true);
        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
    }

    //play next
    private void playNext() {
        musicService.playNext();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        musicController.show(0);
    }

    //play previous
    private void playPrev() {
        musicService.playPrev();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        musicController.show(0);
    }

    public void onEvent(SongEvent event) {
        Song song = event.bundle.getParcelable(Song.KEY);
        switch (event.type) {
            case OPEN:
                songPicked(song);
                break;
        }
    }

    /**
     * Get songs list from Media Store
     *
     * @return
     */
    public ArrayList<Song> getSongList() {
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
//        Cursor albumCursor = musicResolver.query(albumUri, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
//            int thumbColumn = albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
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

//            if(albumCursor != null && albumCursor.moveToFirst()){
//                while (albumCursor.moveToNext()){
//                    String coverPath = albumCursor.getString(thumbColumn);
//                    Drawable thisThumb = Drawable.createFromPath(coverPath);
//                    song.setThumb(thisThumb);
//                }
//            }
        }
        Log.i(TAG, "Song List: " + songList.size() + "Detail: " + songList.get(1).getName());
        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getName().compareTo(b.getName());
            }
        });
        return songList;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicServiceBackground.class);
            bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            Log.i(TAG, "Service has started !");
        }
    }


    public void songPicked(Song song) {
        int songPosn = songList.indexOf(song);
        musicService.setSong(songPosn);
        musicService.playSong();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        musicController.show(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            setController();
            paused = false;
        }
    }

    @Override
    protected void onStop() {
        musicController.hide();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                //shuffle
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicService = null;
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService = null;
        super.onDestroy();
    }

    @Override
    public void start() {
        musicService.go();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (musicService != null && musicBound && musicService.isPng()) {
            return musicService.getDur();
        } else {
            return 0;
        }
    }

    @Override
    public int getCurrentPosition() {
        if (musicService != null && musicBound && musicService.isPng()) {
            return musicService.getPosn();
        } else {
            return 0;
        }
    }

    @Override
    public void seekTo(int pos) {
        musicService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (musicService != null && musicBound) {
            return musicService.isPng();
        } else {
            return false;
        }
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
