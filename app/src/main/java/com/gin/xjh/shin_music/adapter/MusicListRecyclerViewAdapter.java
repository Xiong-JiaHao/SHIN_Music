package com.gin.xjh.shin_music.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gin.xjh.shin_music.activity.MainActivity;
import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.service.MusicService;
import com.gin.xjh.shin_music.util.MusicUtil;

import java.util.List;

/**
 * Created by Gin on 2018/4/24.
 */

public class MusicListRecyclerViewAdapter extends RecyclerView.Adapter<MusicListRecyclerViewAdapter.MusicViewHolder> {
    public List<Song> list;
    private Context context;
    private TextView Songnum;

    public MusicListRecyclerViewAdapter(Context context, List<Song> list, TextView Songnum) {
        this.list = list;
        this.context = context;
        this.Songnum = Songnum;
    }

    @Override
    public MusicListRecyclerViewAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.musiclist_item, parent, false));
    }

    //绑定视图
    @Override
    public void onBindViewHolder(MusicListRecyclerViewAdapter.MusicViewHolder holder, int position) {
        holder.init(position);
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

        public void init(final int position) {
            Song song = list.get(position);
            SongName.setText(song.getSongName() + " - " + song.getSingerName());
            SongName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicUtil.setIndex(position);
                    Intent startIntent = new Intent(context, MusicService.class);
                    startIntent.putExtra("action", MusicService.PLAY);
                    context.startService(startIntent);

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
        int size = MusicUtil.getListSize() - 1;
        if (size == 0) {
            list.clear();
            MusicUtil.playorpause();
            MusicUtil.removeSong(position);
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        } else {
            int num = MusicUtil.getIndex();
            if (position == num) {
                MusicUtil.removeSong(num);
                Intent startIntent1 = new Intent(context, MusicService.class);
                startIntent1.putExtra("action", MusicService.AUTONEXTMUSIC);
                context.startService(startIntent1);
                list = MusicUtil.getSongList();
                notifyItemRemoved(position);
            } else {
                MusicUtil.removeSong(position);
            }
            notifyDataSetChanged();
            Songnum.setText(size + "");
        }
    }
}
