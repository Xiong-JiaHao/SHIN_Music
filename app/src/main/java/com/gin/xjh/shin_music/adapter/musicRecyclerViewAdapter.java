package com.gin.xjh.shin_music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Song;

import java.util.List;

/**
 * Created by Gin on 2018/4/24.
 */

public class musicRecyclerViewAdapter extends RecyclerView.Adapter<musicRecyclerViewAdapter.MusicViewHolder> {
    public List<Song> list;
    private Context context;

    @Override
    public musicRecyclerViewAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item,parent,false));
    }

    //绑定视图
    @Override
    public void onBindViewHolder(musicRecyclerViewAdapter.MusicViewHolder holder, int position) {
        holder.load(list.get(position),context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public musicRecyclerViewAdapter(Context context, List<Song> list) {
        this.list = list;
        this.context=context;
    }


    public class MusicViewHolder extends RecyclerView.ViewHolder{
        private TextView SongName,SingerName;

        public MusicViewHolder(View itemView){
            super(itemView);
            SongName=itemView.findViewById(R.id.itemSongName);
            SingerName=itemView.findViewById(R.id.itemSingerName);
        }

        public void load(Song song, final Context context) {
            SongName.setText(song.getSongName());
            SingerName.setText(song.toString());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"check it",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
