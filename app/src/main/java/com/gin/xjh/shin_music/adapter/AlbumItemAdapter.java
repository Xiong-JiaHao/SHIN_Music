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
import com.gin.xjh.shin_music.user.UserState;
import com.gin.xjh.shin_music.activity.AlbumDetailsActivity;
import com.gin.xjh.shin_music.bean.Album;
import com.gin.xjh.shin_music.util.NetStateUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AlbumItemAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Album> mList;

    public AlbumItemAdapter(Context context, List<Album> list) {
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
            convertView = mInflater.inflate(R.layout.item_fragment_shin, null);
            holder = new ViewHolder();
            holder.mShinImg = convertView.findViewById(R.id.shin_img);
            holder.mShinText = convertView.findViewById(R.id.shin_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Picasso.get().load(mList.get(position).getAlbumUrl())
                .placeholder(R.drawable.def_album)
                .error(R.drawable.def_album)
                .into(holder.mShinImg);
        holder.mShinText.setText(mList.get(position).getAlbumName());
        holder.mShinImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetStateUtil.getNetWorkState(mContext) == NetStateUtil.NO_STATE) {
                    Toast.makeText(mContext, "当前无网络...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (UserState.getState() || position != 0) {
                    Intent intent = new Intent(mContext, AlbumDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("def_album", mList.get(position));
                    intent.putExtra("def_album", bundle);
                    intent.putExtra("isAlbum", true);
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "当前未登录，无法享受该功能", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    public class ViewHolder {
        private TextView mShinText;
        private ImageView mShinImg;
    }

}
