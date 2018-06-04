package com.gin.xjh.shin_music.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.music_play_Activity;
import com.gin.xjh.shin_music.util.MusicUtil;

import java.util.List;

/**
 * Created by Gin on 2018/4/24.
 */

public class musiclistRecyclerViewAdapter extends RecyclerView.Adapter<musiclistRecyclerViewAdapter.MusicViewHolder> {
    public List<Song> list;
    private Context context;
    private TextView Songnum;

    public musiclistRecyclerViewAdapter(Context context, List<Song> list, TextView Songnum) {
        this.list = list;
        this.context = context;
        this.Songnum = Songnum;
    }

    @Override
    public musiclistRecyclerViewAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.musiclist_item, parent, false));
    }

    //绑定视图
    @Override
    public void onBindViewHolder(musiclistRecyclerViewAdapter.MusicViewHolder holder, int position) {
        holder.load(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        private TextView SongName;
        private ImageView removeSong;

        public MusicViewHolder(View itemView) {
            super(itemView);
            SongName = itemView.findViewById(R.id.itemSong);
            removeSong = itemView.findViewById(R.id.removeSong);
        }

        public void load(final int position) {
            Song song = list.get(position);
            SongName.setText(song.getSongName() + " - " + song.getSingerName());
            SongName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicUtil.setIndex(position);
                    MusicUtil.play();
                    Intent playintent = new Intent(music_play_Activity.MUSIC_ACTION_CHANGE);
                    android.support.v4.content.LocalBroadcastManager.getInstance(context).sendBroadcast(playintent);
                }
            });
            removeSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeData(position);
                }
            });
        }
    }

    public void removeData(int position) {
        int num = MusicUtil.getListSize() - 1;
        list.remove(position);
        MusicUtil.removeSong(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
        Songnum.setText(num + "");
    }
}
