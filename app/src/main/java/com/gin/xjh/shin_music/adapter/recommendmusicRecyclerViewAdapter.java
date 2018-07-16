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
import com.gin.xjh.shin_music.User.User_state;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.music_play_Activity;
import com.gin.xjh.shin_music.service.MusicService;
import com.gin.xjh.shin_music.util.DensityUtil;
import com.gin.xjh.shin_music.util.ListDataSaveUtil;
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
        private ImageView cover, sz;

        public MusicViewHolder(View itemView) {
            super(itemView);
            SongName = itemView.findViewById(R.id.itemSongName);
            SingerName = itemView.findViewById(R.id.itemSingerName);
            cover = itemView.findViewById(R.id.cover);
            sz = itemView.findViewById(R.id.music_sz);
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
                        Toast.makeText(context, "请允许4G播放后尝试", Toast.LENGTH_SHORT).show();
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
            sz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showbottomDialog(position);
                }
            });
        }
    }

    @SuppressLint("ResourceAsColor")
    private void showbottomDialog(final int position) {
        final Dialog bottomDialog = new Dialog(mContext, R.style.BottomDialog);
        final Song song = list.get(position);
        View contentView = null;
        contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_content_circle, null);
        TextView ic_comment = contentView.findViewById(R.id.ic_comment);
        if (song.isOnline()) {
            ic_comment.setTextColor(mContext.getResources().getColor(R.color.Check));
        }
        ic_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发消息告知弹出评论
                if (song.isOnline()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("song", song);
                    Intent ic_comment_intent = new Intent(mContext, All_comment.class);
                    ic_comment_intent.putExtra("song", bundle);
                    mContext.startActivity(ic_comment_intent);
                } else {
                    Toast.makeText(mContext, "该歌曲不支持评论功能", Toast.LENGTH_SHORT).show();
                }
                bottomDialog.dismiss();
            }
        });

        TextView ic_play = contentView.findViewById(R.id.ic_play);
        ic_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (song.isOnline()) {
                    if (NetStateUtil.getNetWorkState(mContext) == NetStateUtil.NO_STATE) {
                        Toast.makeText(mContext, "当前网络无法播放", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (NetStateUtil.getNetWorkState(mContext) == NetStateUtil.DATA_STATE && User_state.isUse_4G() == false) {
                        Toast.makeText(mContext, "请允许4G播放后尝试", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (MusicUtil.getListSize() == 0) {
                    List<Song> mSong = new ArrayList<>();
                    mSong.add(song);
                    MusicUtil.changeSongList(mSong);
                    Intent startIntent = new Intent(mContext, MusicService.class);
                    startIntent.putExtra("action", MusicService.PLAY);
                    mContext.startService(startIntent);
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
                        Toast.makeText(mContext, "该歌曲已经存在，请勿重复添加", Toast.LENGTH_SHORT).show();
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
        params.width = mContext.getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(mContext, 16f);
        params.bottomMargin = DensityUtil.dp2px(mContext, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
    }
}
