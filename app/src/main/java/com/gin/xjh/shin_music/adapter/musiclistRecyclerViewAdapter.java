package com.gin.xjh.shin_music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Song;

import java.util.List;

/**
 * Created by Gin on 2018/4/24.
 */

public class musiclistRecyclerViewAdapter extends RecyclerView.Adapter<musiclistRecyclerViewAdapter.MusicViewHolder> {
    public List<Song> list;
    private Context context;

    public musiclistRecyclerViewAdapter(Context context, List<Song> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public musiclistRecyclerViewAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.musiclist_item, parent, false));
    }

    //绑定视图
    @Override
    public void onBindViewHolder(musiclistRecyclerViewAdapter.MusicViewHolder holder, int position) {
        holder.load(list.get(position), context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView SongName;
        private ImageView removeSong;

        public MusicViewHolder(View itemView) {
            super(itemView);
            SongName = itemView.findViewById(R.id.itemSong);
            removeSong = itemView.findViewById(R.id.removeSong);
        }

        public void load(Song song, final Context context) {
            SongName.setText(song.getSongName() + " - " + song.getSingerName());
            SongName.setOnClickListener(this);
            removeSong.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.itemSong:
                    Toast.makeText(context, "itemSong", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.removeSong:
                    Toast.makeText(context, "removeSong", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
