package com.gin.xjh.shin_music.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.musicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Song;
import com.zhy.m.permission.MPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gin on 2018/4/23.
 */

public class Fragment_Local extends Fragment {
    private List<Song> mSongList;
    private RecyclerView mRecyclerView;
    private musicRecyclerViewAdapter mMusicListViewAdapter;

    private EditText mFind = null;
    private ImageView mCheck;

    private TextView mSongNum, mMusic_hint;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, null);
        initView(view);
        initData();
        initEvent();
        return view;
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.fragment_local_music_list);
        mFind = view.findViewById(R.id.find_local_name);
        mCheck = view.findViewById(R.id.find_Localmusic);
        mSongNum = view.findViewById(R.id.SongNum);
        mMusic_hint = view.findViewById(R.id.music_hint);
    }

    private void initData() {
        if (mSongList == null) {
            mSongList = new ArrayList<>();
        } else {
            mSongList.clear();
        }

        Cursor cursor = getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                String SongName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String SingerName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String AlbumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String Url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                mSongList.add(new Song(SongName, SingerName, AlbumName, Url));
            }
        }
        cursor.close();
        mSongNum.setText("歌曲数：" + mSongList.size());
    }

    private void initEvent() {
        mMusicListViewAdapter = new musicRecyclerViewAdapter(getContext(), mSongList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//默认动画
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mMusicListViewAdapter);

        mMusic_hint.setVisibility(View.GONE);

        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });
    }

    private void find() {
        Toast.makeText(getContext(), "find", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
