package com.gin.xjh.shin_music.activities;

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

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.MusicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Album;
import com.gin.xjh.shin_music.bean.LikeSong;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.netrequest.GetNetAlbumList;
import com.gin.xjh.shin_music.netrequest.GetNetMusicList;
import com.gin.xjh.shin_music.user.UserState;
import com.gin.xjh.shin_music.utils.ListDataSaveUtil;
import com.gin.xjh.shin_music.utils.MusicUtil;
import com.gin.xjh.shin_music.utils.NetStateUtil;
import com.gin.xjh.shin_music.utils.TimesUtil;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class AlbumDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mGoBack, mAlbumImg;
    private RecyclerView mAlbumRv;
    private TextView mAlbumName, mAlbumSinger, mAlbumTimes, mAlbumHint;
    private LinearLayout mAddAll;

    private List<Song> mSongList;
    private MusicRecyclerViewAdapter mMusicRecyclerViewAdapter;
    private Album mAlbum;
    private String mName, mUrl;
    private int mID;
    private boolean isAlbum;
    private volatile boolean isPublic = false;

    private Context mContext;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        Intent intent = getIntent();
        isAlbum = intent.getBooleanExtra(getString(R.string.IS_ALBUM), true);
        if (isAlbum) {
            mAlbum = (Album) intent.getBundleExtra(getString(R.string.ALBUM)).get(getString(R.string.ALBUM));
        } else {
            mID = intent.getIntExtra(getString(R.string.ID), 0);
            mName = intent.getStringExtra(getString(R.string.NAME));
            mUrl = intent.getStringExtra(getString(R.string.URL));
        }
        initView();
        initEvent();
    }

    private void initView() {
        mGoBack = findViewById(R.id.go_back);
        mAlbumImg = findViewById(R.id.album_img);
        mAlbumName = findViewById(R.id.album_name);
        mAlbumSinger = findViewById(R.id.album_singer);
        mAlbumTimes = findViewById(R.id.album_times);
        mAddAll = findViewById(R.id.addAll);
        mAlbumRv = findViewById(R.id.album_rv);
        mAlbumHint = findViewById(R.id.album_hint);
    }

    private void initEvent() {
        mGoBack.setOnClickListener(this);
        mAddAll.setOnClickListener(this);
        mContext = this;
        if (isAlbum) {
            if (mAlbum.getAlbumId() == -1) {
                String str = UserState.getLoginUser().getLikeSongListName();
                if (str != null) {
                    mAlbumName.setText(str);
                } else {
                    mAlbumName.setText(mAlbum.getAlbumName());
                }
                mAlbumSinger.setText("创建者：" + UserState.getLoginUser().getUserName());
                mAlbumTimes.setText("编辑歌单信息");
                mAlbumTimes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editSongList();
                    }
                });
                mSongList = UserState.getLikeSongList();
                if (mSongList == null || mSongList.size() == 0) {
                    updateBmobLikeEvent();
                } else {
                    updateUI();
                }
            } else {
                mAlbumSinger.setText("歌手：" + mAlbum.getSinger());
                try {
                    mAlbumTimes.setText("发行时间：" + TimesUtil.longToString(mAlbum.getTimes(), "yyyy-MM-dd"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mAlbumName.setText(mAlbum.getAlbumName());
                updateBmobEvent();
            }
            Picasso.get().load(mAlbum.getAlbumUrl())
                    .placeholder(R.drawable.def_album)
                    .error(R.drawable.def_album)
                    .into(mAlbumImg);
        } else {
            Picasso.get().load(mUrl)
                .placeholder(R.drawable.def_album)
                .error(R.drawable.def_album)
                    .into(mAlbumImg);
            mAlbumName.setTextSize(25);
            mAlbumName.setText(mName);
            updateOnlineEvent();
        }
    }

    private void editSongList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AlbumDetailsActivity.this);
        LayoutInflater inflater = LayoutInflater.from(AlbumDetailsActivity.this);
        View viewDialog = inflater.inflate(R.layout.dialog_edit_likesong, null);
        final EditText likeSongName = viewDialog.findViewById(R.id.likeSongName);
        final Switch is_Public = viewDialog.findViewById(R.id.is_Public);
        likeSongName.setHint(mAlbumName.getText());
        is_Public.setChecked(UserState.getLoginUser().isPublic_song());
        is_Public.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                synchronized (AlbumDetailsActivity.class) {
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
                    if (isPublic != UserState.getLoginUser().isPublic_song()) {
                        User user = new User();
                        if (isPublic) {
                            user.changPublic_song();
                        }
                        final boolean ispublic = isPublic;
                        user.update(UserState.getLoginUser().getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USER), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    UserState.getLoginUser().changPublic_song();
                                    editor.putBoolean(getString(R.string.IS_PUBLIC_SONG), ispublic);
                                    editor.commit();
                                } else {
                                    Toast.makeText(AlbumDetailsActivity.this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
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
                    user.update(UserState.getLoginUser().getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USER), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                if (ispublic != UserState.getLoginUser().isPublic_song()) {
                                    UserState.getLoginUser().changPublic_song();
                                    editor.putBoolean(getString(R.string.IS_PUBLIC_SONG), ispublic);
                                }
                                UserState.getLoginUser().setLikeSongListName(name);
                                editor.putString(getString(R.string.LIKE_SONGLIST_NAME), name);
                                editor.commit();
                                mAlbumName.setText(name);
                            } else {
                                Toast.makeText(AlbumDetailsActivity.this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
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
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.setMaxCacheAge(43200000);//缓存有半天的有效期
        query.addWhereEqualTo(getString(R.string.USERID), UserState.getLoginUser().getUserId());//按当前登录的ID进行查找
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
                    ListDataSaveUtil.setSongList(getString(R.string.LIKE_SONG_LIST), mSongList);
                    updateUI();
                } else {
                    mAlbumHint.setText(R.string.NO_LIKE_SONG);
                }
            }
        });

    }

    private void updateBmobEvent() {
        BmobQuery<Song> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.addWhereEqualTo(getString(R.string.ALBUM_NAME), mAlbum.getAlbumName());
        query.findObjects(new FindListener<Song>() {
            @Override
            public void done(List<Song> list, BmobException e) {
                if (list != null && list.size() != 0) {
                    mSongList = list;
                    for (int i = 0; i < mSongList.size(); i++) {
                        mSongList.get(i).setAlbumId(mAlbum.getAlbumId());
                        mSongList.get(i).setAlbumTime(mAlbum.getTimes());
                    }
                    updateUI();
                } else {
                    new GetNetAlbumList().getJson(mAlbum.getAlbumId(), mAlbumRv, mAlbumHint, mContext);
                }
            }
        });

    }

    private void updateOnlineEvent() {
        mSongList = new ArrayList<>();
        new GetNetMusicList().getJson(mID, findViewById(R.id.album_rv), findViewById(R.id.album_hint), mContext, mSongList);
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
        } else if (NetStateUtil.getNetWorkState(this) == NetStateUtil.DATA_STATE && UserState.isUse_4G() == false) {
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
            ListDataSaveUtil.setSongList(getString(R.string.SONG_LIST), MusicUtil.getSongList());
        }
        if (flag) {
            Toast.makeText(mContext, "添加完成", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        mAlbumHint.setVisibility(View.GONE);
        if (mSongList != null && mSongList.size() > 0) {
            mMusicRecyclerViewAdapter = new MusicRecyclerViewAdapter(mContext, mSongList);
            mAlbumRv.setLayoutManager(new LinearLayoutManager(mContext));
            mAlbumRv.setItemAnimator(new DefaultItemAnimator());//默认动画
            mAlbumRv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            mAlbumRv.setAdapter(mMusicRecyclerViewAdapter);
        } else {
            mAlbumHint.setText(R.string.NO_LIKE_SONG);
        }
    }
}
