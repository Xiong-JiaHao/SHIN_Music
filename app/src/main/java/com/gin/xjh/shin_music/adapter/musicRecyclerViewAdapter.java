package com.gin.xjh.shin_music.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.music_play_Activity;
import com.gin.xjh.shin_music.util.DensityUtil;
import com.gin.xjh.shin_music.util.MusicUtil;

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

    private void showbottomDialog(final int position) {
        Dialog bottomDialog = new Dialog(context, R.style.BottomDialog);
        final View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_content_circle, null);
        bottomDialog.setCanceledOnTouchOutside(true);
        TextView ic_comment = contentView.findViewById(R.id.ic_comment);
        ic_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发消息告知弹出评论
                Toast.makeText(context, "comment", Toast.LENGTH_SHORT).show();
            }
        });
        TextView ic_play = contentView.findViewById(R.id.ic_play);
        ic_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
