package com.gin.xjh.shin_music.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.User.User_state;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.music_play_Activity;
import com.gin.xjh.shin_music.service.MusicService;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.gin.xjh.shin_music.util.NetStateUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gin on 2018/4/24.
 */

public class recommendmusicRecyclerViewAdapter extends RecyclerView.Adapter<recommendmusicRecyclerViewAdapter.MusicViewHolder> {
    public List<Song> list;
    private Context mContext;

    public recommendmusicRecyclerViewAdapter(Context context, List<Song> list) {
        this.list = list;
        this.mContext = context;
    }

    @Override
    public recommendmusicRecyclerViewAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_music_item, parent, false));
    }

    //绑定视图
    @Override
    public void onBindViewHolder(recommendmusicRecyclerViewAdapter.MusicViewHolder holder, int position) {
        holder.load(list.get(position), mContext, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        private TextView SongName, SingerName;
        private ImageView cover;

        public MusicViewHolder(View itemView) {
            super(itemView);
            SongName = itemView.findViewById(R.id.itemSongName);
            SingerName = itemView.findViewById(R.id.itemSingerName);
            cover = itemView.findViewById(R.id.cover);
        }

        public void load(Song song, final Context context, final int position) {
            SongName.setText(song.getSongName());
            SingerName.setText(song.toString());
            Picasso.get().load(song.getAlbumUrl())
                    .placeholder(R.drawable.album)
                    .error(R.drawable.album)
                    .into(cover);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetStateUtil.getNetWorkState(context) == NetStateUtil.NO_STATE) {
                        Toast.makeText(context, "当前网络无法播放", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (NetStateUtil.getNetWorkState(context) == NetStateUtil.DATA_STATE && User_state.isUse_4G() == false) {
                        Toast.makeText(context, "请打开4G开关后再进行播放", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<Song> mList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        mList.add(list.get(i));
                    }
                    MusicUtil.changeSongList(mList);
                    MusicUtil.setIndex(position);
                    Intent startIntent1 = new Intent(context, MusicService.class);
                    startIntent1.putExtra("action", MusicService.PLAY);
                    Intent intent = new Intent(context, music_play_Activity.class);
                    context.startService(startIntent1);
                    context.startActivity(intent);
                }
            });
        }
    }
}
