package com.gin.xjh.shin_music;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.Net_Request.getNetAlbumList;
import com.gin.xjh.shin_music.Net_Request.getNetMusicList;
import com.gin.xjh.shin_music.User.User_state;
import com.gin.xjh.shin_music.adapter.musicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Album;
import com.gin.xjh.shin_music.bean.LikeSong;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.util.ListDataSaveUtil;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.gin.xjh.shin_music.util.NetStateUtil;
import com.gin.xjh.shin_music.util.TimesUtil;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class album_details_Activity extends BaseActivity implements View.OnClickListener {

    private ImageView go_back,album_img;
    private RecyclerView album_rv;
    private TextView album_name, album_singer, album_times, album_hint;
    private LinearLayout addAll;

    private List<Song> mSongList;
    private musicRecyclerViewAdapter mMusicRecyclerViewAdapter;
    private Album album;
    private String name,url;
    private int id;
    private boolean isAlbum;
    private volatile boolean isPublic = false;

    private Context mContext;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_details);
        Intent intent = getIntent();
        isAlbum = intent.getBooleanExtra("isAlbum", true);
        if (isAlbum) {
            album = (Album) intent.getBundleExtra("album").get("album");
        } else {
            id = intent.getIntExtra("id",0);
            name = intent.getStringExtra("name");
            url=intent.getStringExtra("url");
        }
        initView();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        album_img = findViewById(R.id.album_img);
        album_name = findViewById(R.id.album_name);
        album_singer = findViewById(R.id.album_singer);
        album_times = findViewById(R.id.album_times);
        addAll = findViewById(R.id.addAll);
        album_rv = findViewById(R.id.album_rv);
        album_hint = findViewById(R.id.album_hint);
    }

    private void initEvent() {
        go_back.setOnClickListener(this);
        addAll.setOnClickListener(this);
        mContext = this;
        if (isAlbum) {
            if (album.getAlbumId() == -1) {
                String str = User_state.getLoginUser().getLikeSongListName();
                if (str != null) {
                    album_name.setText(str);
                } else {
                    album_name.setText(album.getAlbumName());
                }
                album_singer.setText("创建者：" + User_state.getLoginUser().getUserName());
                album_times.setText("编辑歌单信息");
                album_times.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editSongList();
                    }
                });
                mSongList = User_state.getLikeSongList();
                if (mSongList == null || mSongList.size() == 0) {
                    updateBmobLikeEvent();
                } else {
                    updateUI();
                }
            } else {
                album_singer.setText("歌手：" + album.getSinger());
                try {
                    album_times.setText("发行时间：" + TimesUtil.longToString(album.getTimes(), "yyyy-MM-dd"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                album_name.setText(album.getAlbumName());
                updateBmobEvent();
            }
            Picasso.get().load(album.getAlbumUrl())
                    .placeholder(R.drawable.album)
                    .error(R.drawable.album)
                    .into(album_img);
        } else {
            Picasso.get().load(url)
                .placeholder(R.drawable.album)
                .error(R.drawable.album)
                .into(album_img);
            album_name.setTextSize(25);
            album_name.setText(name);
            updateOnlineEvent();
        }
    }

    private void editSongList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(album_details_Activity.this);
        LayoutInflater inflater = LayoutInflater.from(album_details_Activity.this);
        View viewDialog = inflater.inflate(R.layout.editlikesong_layout, null);
        final EditText likeSongName = viewDialog.findViewById(R.id.likeSongName);
        final Switch is_Public = viewDialog.findViewById(R.id.is_Public);
        likeSongName.setHint(album_name.getText());
        is_Public.setChecked(User_state.getLoginUser().isPublic_song());
        is_Public.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                synchronized (album_details_Activity.class) {
                    isPublic = isChecked;
                }
            }
        });
        builder.setView(viewDialog);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = likeSongName.getText().toString();
                if (name == null) {
                    if (isPublic != User_state.getLoginUser().isPublic_song()) {
                        User user = new User();
                        if (isPublic) {
                            user.changPublic_song();
                        }
                        final boolean ispublic = isPublic;
                        user.update(User_state.getLoginUser().getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    User_state.getLoginUser().changPublic_song();
                                    editor.putBoolean("public_song", ispublic);
                                    editor.commit();
                                } else {
                                    Toast.makeText(album_details_Activity.this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    User user = new User();
                    if (isPublic) {
                        user.changPublic_song();
                    }
                    user.setLikeSongListName(name);
                    final boolean ispublic = isPublic;
                    user.update(User_state.getLoginUser().getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                if (ispublic != User_state.getLoginUser().isPublic_song()) {
                                    User_state.getLoginUser().changPublic_song();
                                    editor.putBoolean("public_song", ispublic);
                                }
                                User_state.getLoginUser().setLikeSongListName(name);
                                editor.putString("likesonglistname", name);
                                editor.commit();
                                album_name.setText(name);
                            } else {
                                Toast.makeText(album_details_Activity.this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create();
        builder.show();
    }

    private void updateBmobLikeEvent() {
        BmobQuery<LikeSong> query = new BmobQuery<>();
        query.addWhereEqualTo("UserId", User_state.getLoginUser().getUserId());//按当前登录的ID进行查找
        query.findObjects(new FindListener<LikeSong>() {
            @Override
            public void done(List<LikeSong> list, BmobException e) {
                if (list != null && list.size() > 0) {
                    Song song;
                    LikeSong likeSong;
                    for (int i = 0; i < list.size(); i++) {
                        likeSong = list.get(i);
                        song = likeSong.getSong();
                        song.setObjectId(likeSong.getObjectId());
                        mSongList.add(song);
                    }
                    ListDataSaveUtil.setSongList("likesong", mSongList);
                    updateUI();
                } else {
                    album_hint.setText("无喜欢歌曲，请添加后查看");
                }
            }
        });

    }

    private void updateBmobEvent() {
        BmobQuery<Song> query = new BmobQuery<>();
        query.addWhereEqualTo("AlbumName", album.getAlbumName());
        query.findObjects(new FindListener<Song>() {
            @Override
            public void done(List<Song> list, BmobException e) {
                if (list != null && list.size() != 0) {
                    mSongList = list;
                    for (int i = 0; i < mSongList.size(); i++) {
                        mSongList.get(i).setAlbumId(album.getAlbumId());
                        mSongList.get(i).setAlbumTime(album.getTimes());
                    }
                    updateUI();
                } else {
                    new getNetAlbumList().getJson(album.getAlbumId(), album_rv, album_hint, mContext);
                }
            }
        });

    }

    private void updateOnlineEvent() {
        mSongList = new ArrayList<>();
        new getNetMusicList().getJson(id, findViewById(R.id.album_rv), findViewById(R.id.album_hint), mContext, mSongList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                finish();
                break;
            case R.id.addAll:
                addAllSong();
        }
    }

    private void addAllSong() {
        if (mSongList == null || mSongList.size() == 0) {
            return;
        }
        if (NetStateUtil.getNetWorkState(this) == NetStateUtil.NO_STATE) {
            Toast.makeText(this, "当前网络无法播放", Toast.LENGTH_SHORT).show();
            return;
        } else if (NetStateUtil.getNetWorkState(this) == NetStateUtil.DATA_STATE && User_state.isUse_4G() == false) {
            Toast.makeText(this, "请允许4G播放后尝试", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean flag = false;
        if (MusicUtil.getListSize() == 0) {
            List<Song> mList = new ArrayList<>();
            for (int i = 0; i < mSongList.size(); i++) {
                mList.add(mSongList.get(i));
            }
            MusicUtil.changeSongList(mList);
            MusicUtil.play();
            flag = true;
        } else {
            List<Song> nowSongList = MusicUtil.getSongList();
            for (Song song : mSongList) {
                boolean isFlag = true;
                for (Song nowsong : nowSongList) {
                    if (nowsong.equals(song)) {
                        isFlag = false;
                        break;
                    }
                }
                if (isFlag) {
                    flag = true;
                    MusicUtil.addSong(song, false);
                }
            }
            ListDataSaveUtil.setSongList("songlist", MusicUtil.getSongList());
        }
        if (flag) {
            Toast.makeText(mContext, "添加完成", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        album_hint.setVisibility(View.GONE);
        if (mSongList != null && mSongList.size() > 0) {
            mMusicRecyclerViewAdapter = new musicRecyclerViewAdapter(mContext, mSongList);
            album_rv.setLayoutManager(new LinearLayoutManager(mContext));
            album_rv.setItemAnimator(new DefaultItemAnimator());//默认动画
            album_rv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            album_rv.setAdapter(mMusicRecyclerViewAdapter);
        } else {
            album_hint.setText("无喜欢歌曲，请添加后查看");
        }
    }
}
