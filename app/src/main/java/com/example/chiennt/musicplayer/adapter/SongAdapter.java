package com.example.chiennt.musicplayer.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chiennt.musicplayer.R;
import com.example.chiennt.musicplayer.model.Song;
import com.example.chiennt.musicplayer.model.event.SongEvent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Nguyen on 11/07/2015.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> implements View.OnTouchListener {

    private ArrayList<Song> songList;
    private Context mContext;

    public SongAdapter(Context mContext, ArrayList<Song> songs) {
        this.mContext = mContext;
        this.songList = songs;
    }

    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        SongHolder songHolder = new SongHolder(view);
        return songHolder;
    }

    @Override
    public void onBindViewHolder(final SongHolder holder, int position) {
        final Song song = songList.get(position);
        if (song != null) {
            holder.itemView.setTag(song);
            holder.txtSongTitle.setText(song.getName());
            holder.songArtist.setText(song.getArtist());
//            holder.imgSongThumb.setImageDrawable(mContext.getDrawable(R.drawable.default_song_thumb));
        }


        final Bundle bundle = new Bundle();
        bundle.putParcelable(Song.KEY, song);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SongEvent(SongEvent.TYPE.OPEN, bundle));
            }
        });

    }



    public int getItemPosition(Song song){
        return songList.indexOf(song);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public class SongHolder extends RecyclerView.ViewHolder {

        public ImageView imgSongThumb;
        public TextView txtSongTitle;
        public TextView songArtist;

        public SongHolder(View itemView) {
            super(itemView);
            txtSongTitle = (TextView) itemView.findViewById(R.id.song_title);
            songArtist = (TextView) itemView.findViewById(R.id.song_artist);
            imgSongThumb = (ImageView) itemView.findViewById(R.id.imgSongThumb);
        }
    }
}
