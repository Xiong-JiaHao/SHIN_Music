package com.gin.xjh.shin_music.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.activity.AllCommentActivity;
import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.user.UserState;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.activity.MusicPlayActivity;
import com.gin.xjh.shin_music.service.MusicService;
import com.gin.xjh.shin_music.util.DensityUtil;
import com.gin.xjh.shin_music.util.ListDataSaveUtil;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.gin.xjh.shin_music.util.NetStateUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gin on 2018/4/24.
 */

public class MusicRecyclerViewAdapter extends RecyclerView.Adapter<MusicRecyclerViewAdapter.MusicViewHolder> {
    public List<Song> list;
    private Context context;

    public MusicRecyclerViewAdapter(Context context, List<Song> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MusicRecyclerViewAdapter.MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false));
    }

    //绑定视图
    @Override
    public void onBindViewHolder(MusicRecyclerViewAdapter.MusicViewHolder holder, int position) {
        holder.init(list.get(position), context, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("ResourceAsColor")
    private void showbottomDialog(final int position) {
        final Dialog bottomDialog = new Dialog(context, R.style.BottomDialog);
        final Song song = list.get(position);
        View contentView = null;
        if(song.isOnline()){
            contentView = LayoutInflater.from(context).inflate(R.layout.content_circle_dialog, null);
            TextView ic_comment = contentView.findViewById(R.id.ic_comment);
            if (song.isOnline()) {
                ic_comment.setTextColor(context.getResources().getColor(R.color.Check));
            }
            ic_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //发消息告知弹出评论
                    if (song.isOnline()) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("song", song);
                        Intent ic_comment_intent = new Intent(context, AllCommentActivity.class);
                        ic_comment_intent.putExtra("song", bundle);
                        context.startActivity(ic_comment_intent);
                    } else {
                        Toast.makeText(context, "该歌曲不支持评论功能", Toast.LENGTH_SHORT).show();
                    }
                    bottomDialog.dismiss();
                }
            });
        } else {
            contentView = LayoutInflater.from(context).inflate(R.layout.content_circle_local_dialog, null);
            TextView ic_delete = contentView.findViewById(R.id.ic_delete);
            ic_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.getContentResolver().delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, MediaStore.Video.Media.DATA+"='" + song.getUrl() + "'", null);
                    File mf = new File(song.getUrl());
                    if (mf.exists()) {
                        mf.delete();
                        Toast.makeText(context, "删除成功", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "该文件不存在", Toast.LENGTH_LONG).show();
                    }
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                    bottomDialog.dismiss();
                }
            });
        }

        TextView ic_play = contentView.findViewById(R.id.ic_play);
        ic_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (song.isOnline()) {
                    if (NetStateUtil.getNetWorkState(context) == NetStateUtil.NO_STATE) {
                        Toast.makeText(context, "当前网络无法播放", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (NetStateUtil.getNetWorkState(context) == NetStateUtil.DATA_STATE && UserState.isUse_4G() == false) {
                        Toast.makeText(context, "请允许4G播放后尝试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (MusicUtil.getListSize() == 0) {
                    List<Song> mSong = new ArrayList<>();
                    mSong.add(song);
                    MusicUtil.changeSongList(mSong);
                    Intent startIntent = new Intent(context, MusicService.class);
                    startIntent.putExtra("action", MusicService.PLAY);
                    context.startService(startIntent);
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
                        MusicUtil.addSong(song, true);
                        ListDataSaveUtil.setSongList("songlist", MusicUtil.getSongList());
                    } else {
                        Toast.makeText(context, "该歌曲已经存在，请勿重复添加", Toast.LENGTH_SHORT).show();
                    }
                }
                bottomDialog.dismiss();
            }
        });
        TextView ic_singer = contentView.findViewById(R.id.ic_singer);
        TextView ic_album = contentView.findViewById(R.id.ic_album);
        ic_album.setText(song.getAlbumName());
        ic_singer.setText(song.getSingerName());
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

        public void init(final Song song, final Context context, final int position) {
            SongName.setText(song.getSongName());
            SingerName.setText(song.toString());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (song.isOnline()) {
                        if (NetStateUtil.getNetWorkState(context) == NetStateUtil.NO_STATE) {
                            Toast.makeText(context, "当前网络无法播放", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (NetStateUtil.getNetWorkState(context) == NetStateUtil.DATA_STATE && UserState.isUse_4G() == false) {
                            Toast.makeText(context, "请允许4G播放后尝试", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    List<Song> mList = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        mList.add(list.get(i));
                    }
                    MusicUtil.changeSongList(mList);
                    MusicUtil.setIndex(position);
                    Intent startIntent = new Intent(context, MusicService.class);
                    startIntent.putExtra("action", MusicService.PLAY);
                    context.startService(startIntent);
                    Intent intent = new Intent(context, MusicPlayActivity.class);
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
