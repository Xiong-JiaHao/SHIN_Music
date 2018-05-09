package com.gin.xjh.shin_music.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.album_details_Activity;
import com.gin.xjh.shin_music.bean.Album;
import com.gin.xjh.shin_music.util.DownloadBitmapUtil;

import java.io.IOException;
import java.util.List;

public class albumItemAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Album> mList;

    public albumItemAdapter(Context context, List<Album> list) {
        mContext = context;
        mList = list;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setmList(List<Album> mList) {
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Album getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_shin_item, null);
            holder = new ViewHolder();
            holder.shin_Img = convertView.findViewById(R.id.shin_img);
            holder.shin_Text = convertView.findViewById(R.id.shin_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Bitmap[] bitmap = {null};
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    bitmap[0] = DownloadBitmapUtil.getHttpBitmap(mList.get(position).getAlbumUrl());
                    Activity activity = (Activity) holder.shin_Img.getContext();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.shin_Img.setImageBitmap(bitmap[0]);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        holder.shin_Text.setText(mList.get(position).getAlbumName());
        holder.shin_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, album_details_Activity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("album",mList.get(position));
                intent.putExtra("album",bundle);
                intent.putExtra("isAlbum", true);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        private TextView shin_Text;
        private ImageView shin_Img;
    }

}
