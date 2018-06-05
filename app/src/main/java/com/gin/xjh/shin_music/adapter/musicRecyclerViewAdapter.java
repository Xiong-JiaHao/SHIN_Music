package com.gin.xjh.shin_music.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.All_comment;
import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.music_play_Activity;
import com.gin.xjh.shin_music.util.DensityUtil;
import com.gin.xjh.shin_music.util.MusicUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gin on 2018/4/24.
 */

public class musicRecyclerViewAdapter extends RecyclerView.Adapter<musicRecyclerViewAdapter.MusicViewHolder> {
    public List<Song> list;
    private Context context;

    public musicRecyclerViewAdapter(Context context, List<Song> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public musicRecyclerViewAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false));
    }

    //绑定视图
    @Override
    public void onBindViewHolder(musicRecyclerViewAdapter.MusicViewHolder holder, int position) {
        holder.load(list.get(position), context, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("ResourceAsColor")
    private void showbottomDialog(final int position) {
        final Dialog bottomDialog = new Dialog(context, R.style.BottomDialog);
        final Song song = list.get(position);
        View contentView=null;
        if(song.isOnline()){
            contentView = LayoutInflater.from(context).inflate(R.layout.dialog_content_circle, null);
            TextView ic_comment = contentView.findViewById(R.id.ic_comment);
            if (song.isOnline()) {
                ic_comment.setTextColor(R.color.Check);
            }
            ic_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //发消息告知弹出评论
                    if (song.isOnline()) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("song", song);
                        Intent ic_comment_intent = new Intent(context, All_comment.class);
                        ic_comment_intent.putExtra("song", bundle);
                        context.startActivity(ic_comment_intent);
                    } else {
                        Toast.makeText(context, "该歌曲不支持评论功能", Toast.LENGTH_SHORT).show();
                    }
                    bottomDialog.dismiss();
                }
            });
        } else {
            contentView = LayoutInflater.from(context).inflate(R.layout.dialog_content_circle_local, null);
            TextView ic_delete = contentView.findViewById(R.id.ic_delete);
            ic_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File mf = new File(list.get(position).getUrl());
                    if (mf.exists()) {
                        mf.delete();
                        list.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "删除成功", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "该文件不存在", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        TextView ic_play = contentView.findViewById(R.id.ic_play);
        ic_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicUtil.getListSize() == 0) {
                    ArrayList<Song> mSong = new ArrayList<>();
                    mSong.add(song);
                    MusicUtil.changeSongList(mSong);
                    MusicUtil.play();
                } else {
                    List<Song> mlist = MusicUtil.getSongList();
                    boolean isFlag = true;
                    //判断是不是存在歌曲
                    for (Song nowsong : mlist) {
                        if (nowsong.equals(song)) {
                            isFlag = false;
                            break;
                        }
                    }
                    if (isFlag) {
                        MusicUtil.addSong(song);
                    } else {
                        Toast.makeText(context, "该歌曲已经存在，请勿重复添加", Toast.LENGTH_SHORT).show();
                    }
                    bottomDialog.dismiss();
                }
            }
        });
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = context.getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(context, 16f);
        params.bottomMargin = DensityUtil.dp2px(context, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        private TextView SongName, SingerName;
        private ImageView sz;

        public MusicViewHolder(View itemView) {
            super(itemView);
            SongName = itemView.findViewById(R.id.itemSongName);
            SingerName = itemView.findViewById(R.id.itemSingerName);
            sz = itemView.findViewById(R.id.music_sz);
        }

        public void load(Song song, final Context context, final int position) {
            SongName.setText(song.getSongName());
            SingerName.setText(song.toString());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, music_play_Activity.class);
                    MusicUtil.changeSongList(list);
                    MusicUtil.setIndex(position);
                    MusicUtil.play();
                    context.startActivity(intent);
                }
            });

            sz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showbottomDialog(position);
                }
            });
        }
    }
}
