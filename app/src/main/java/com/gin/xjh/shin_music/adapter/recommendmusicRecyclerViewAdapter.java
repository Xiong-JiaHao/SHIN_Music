package com.gin.xjh.shin_music.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

public class recommendmusicRecyclerViewAdapter extends RecyclerView.Adapter<recommendmusicRecyclerViewAdapter.MusicViewHolder> {
    public List<Song> list;
    private Context context;

    @Override
    public recommendmusicRecyclerViewAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_music_item,parent,false));
    }

    //绑定视图
    @Override
    public void onBindViewHolder(recommendmusicRecyclerViewAdapter.MusicViewHolder holder, int position) {
        holder.load(list.get(position),context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public recommendmusicRecyclerViewAdapter(Context context, List<Song> list) {
        this.list = list;
        this.context=context;
    }


    public class MusicViewHolder extends RecyclerView.ViewHolder{
        private TextView SongName,SingerName;
        private ImageView cover;

        public MusicViewHolder(View itemView){
            super(itemView);
            SongName=itemView.findViewById(R.id.itemSongName);
            SingerName=itemView.findViewById(R.id.itemSingerName);
            cover=itemView.findViewById(R.id.cover);
        }

        public void load(Song song, final Context context) {
            SongName.setText(song.getSongName());
            SingerName.setText(song.toString());
            Drawable drawable = context.getResources().getDrawable(R.drawable.dayemen);
            BitmapDrawable bd = (BitmapDrawable) drawable;
            Bitmap bitmap = bd.getBitmap();
            cover.setImageBitmap(bitmap);//测试
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"check it",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
