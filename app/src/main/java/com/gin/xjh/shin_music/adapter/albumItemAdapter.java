package com.gin.xjh.shin_music.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.album_details_Activity;
import com.gin.xjh.shin_music.bean.Album;
import com.gin.xjh.shin_music.util.NetStateUtil;
import com.squareup.picasso.Picasso;

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
        Picasso.get().load(mList.get(position).getAlbumUrl())
                .placeholder(R.drawable.album)
                .error(R.drawable.album)
                .into(holder.shin_Img);
        holder.shin_Text.setText(mList.get(position).getAlbumName());
        holder.shin_Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetStateUtil.getNetWorkState(mContext) == NetStateUtil.NO_STATE) {
                    Toast.makeText(mContext, "当前无网络...", Toast.LENGTH_SHORT).show();
                    return;
                }
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
