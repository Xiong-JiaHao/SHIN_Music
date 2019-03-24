package com.gin.xjh.shin_music.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.adapter.FragmentAdapter;
import com.gin.xjh.shin_music.bean.AppUrl;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.fragments.FragmentLocal;
import com.gin.xjh.shin_music.fragments.FragmentOnline;
import com.gin.xjh.shin_music.fragments.FragmentShin;
import com.gin.xjh.shin_music.service.MusicService;
import com.gin.xjh.shin_music.user.UserState;
import com.gin.xjh.shin_music.utils.ConstantUtil;
import com.gin.xjh.shin_music.utils.ListDataSaveUtil;
import com.gin.xjh.shin_music.utils.MusicUtil;
import com.gin.xjh.shin_music.utils.TimesUtil;
import com.tencent.bugly.Bugly;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mTopbar;
    private ImageView mTopbarSetting, mTopbarMusic;//菜单按钮，播放器按钮
    private ViewPager mViewPager;
    private LinearLayout mLine, mShin, mOnlineMusic, mLocalMusic;
    private ImageView mShinImg, mOnlineMusicImg, mLocalMusicImg;
    private TextView mShinText, mOnlineMusicText, mLocalMusicText;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private FragmentAdapter mAdapter;

    //记录当前为哪个Fragment以及当前的屏幕宽度
    private int mIndex;
    private int mSreenWidth;


    private static final int REQUECT_CODE_SDCARD = 2;


    private MusicService mMusicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBmob();
        initBugly();
        new Thread(new Runnable() {
            @Override
            public void run() {
                initalize();
            }
        }).start();
        initView();
        Intent startIntent = new Intent(this, MusicService.class);
        bindService(startIntent, connection, BIND_AUTO_CREATE);
    }

    private void initBugly() {
        /* Bugly SDK初始化
         * 参数1：上下文对象
         * 参数2：APPID，平台注册时得到,注意替换成你的appId
         * 参数3：是否开启调试模式，调试模式下会输出'CrashReport'tag的日志
         */
        Bugly.init(getApplicationContext(), ConstantUtil.BUGLY_APP_ID, true);
    }

    private void initBmob() {

        Bmob.initialize(this, ConstantUtil.BMOB_API_KEY);

        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {

            }
        });
        // 启动推送服务
        BmobPush.startWork(this);
        BmobQuery<AppUrl> query = new BmobQuery<>();
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(43200000);//缓存有半天的有效期
        query.findObjects(new FindListener<AppUrl>() {
            @Override
            public void done(List<AppUrl> list, BmobException e) {
                AppUrl appUrl = list.get(0);
                ConstantUtil.URL_BASE = appUrl.getUrl();
                initEvent();
            }
        });

    }

    private void initalize() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.USER), Context.MODE_PRIVATE);
        ListDataSaveUtil.setPreferences(sharedPreferences);
        String user_id = sharedPreferences.getString(getString(R.string.USER_ID), null);
        String user_name = sharedPreferences.getString(getString(R.string.USER_NAME), null);
        String password = sharedPreferences.getString(getString(R.string.PASSWORD), null);
        String user_qq = sharedPreferences.getString(getString(R.string.USER_QQ), null);
        int user_sex = sharedPreferences.getInt(getString(R.string.USER_SEX), 0);
        String personal_profile = sharedPreferences.getString(getString(R.string.PERSONAL_PROFILE), null);
        String objId = sharedPreferences.getString(getString(R.string.OBJ_ID), null);
        Long time = sharedPreferences.getLong(getString(R.string.TIME), -1L);
        Long nowtime = TimesUtil.dateToLong(new Date(System.currentTimeMillis()));
        if (user_id != null && nowtime - time < 432000000) {
            User user = new User(user_id, user_name, password, user_qq, user_sex, personal_profile);
            user.setObjectId(objId);
            user.setLikeSongListName(sharedPreferences.getString(getString(R.string.LIKE_SONGLIST_NAME), null));
            boolean ispublic = sharedPreferences.getBoolean(getString(R.string.IS_PUBLIC_SONG), false);
            if (ispublic) {
                user.changPublic_song();
            }
            UserState.Login(user);
            UserState.setLikeSongList(ListDataSaveUtil.getSongList(getString(R.string.LIKE_SONG_LIST)));
            UserState.setConcernList(ListDataSaveUtil.getUserList(getString(R.string.CONCERN_USER)));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(getString(R.string.TIME), nowtime);
            editor.commit();
        }
        UserState.setUse_4G(sharedPreferences.getBoolean(getString(R.string.USER_4G), false));
        MusicUtil.setPlay_state(sharedPreferences.getInt(getString(R.string.PLAY_STATE), 0));
        int index = sharedPreferences.getInt(getString(R.string.INDEX), -1);
        if (index >= 0) {
            MusicUtil.setIndex(index);
            MusicUtil.changeSongList(ListDataSaveUtil.getSongList(getString(R.string.SONG_LIST)));
        }
    }

    private void initView() {
        mTopbar = findViewById(R.id.Top_bar);
        mTopbarSetting = findViewById(R.id.Topbar_setting);
        mTopbarMusic = findViewById(R.id.Topbar_music);
        mViewPager = findViewById(R.id.fragment_VP);
        mLine = findViewById(R.id.main_line);
        mShin = findViewById(R.id.shin);
        mOnlineMusic = findViewById(R.id.Online_music);
        mLocalMusic = findViewById(R.id.Local_music);
        mShinImg = findViewById(R.id.shin_img);
        mOnlineMusicImg = findViewById(R.id.Online_music_img);
        mLocalMusicImg = findViewById(R.id.Local_music_img);
        mShinText = findViewById(R.id.shin_text);
        mOnlineMusicText = findViewById(R.id.Online_music_text);
        mLocalMusicText = findViewById(R.id.Local_music_text);
    }

    private void initEvent() {
        mFragmentList.add(new FragmentShin());
        mFragmentList.add(new FragmentOnline());
        mFragmentList.add(new FragmentLocal());

        mIndex = 0;
        mShinImg.setImageResource(R.drawable.icon_shin_red);
        mShinText.setTextColor(Color.RED);
        mSreenWidth = getWindowManager().getDefaultDisplay().getWidth();

        mAdapter = new FragmentAdapter(getSupportFragmentManager(), mFragmentList);
        mViewPager.setAdapter(mAdapter);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLine.getLayoutParams();
        lp.width = mSreenWidth / 3;
        mLine.setLayoutParams(lp);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /**
             * 这个方法会在屏幕滚动过程中不断被调用。
             * 有三个参数，第一个position，这个参数要特别注意一下。当用手指滑动时，如果手指按在页面上不动，position和当前页面index是一致的；
             * 如果手指向左拖动（相应页面向右翻动），这时候position大部分时间和当前页面是一致的，
             * 只有翻页成功的情况下最后一次调用才会变为目标页面；
             * 如果手指向右拖动（相应页面向左翻动），这时候position大部分时间和目标页面是一致的，只有翻页不成功的情况下最后一次调用才会变为原页面。
             * 当直接设置setCurrentItem翻页时，如果是相邻的情况（比如现在是第二个页面，跳到第一或者第三个页面），
             * 如果页面向右翻动，大部分时间是和当前页面是一致的，只有最后才变成目标页面；
             * 如果向左翻动，position和目标页面是一致的。这和用手指拖动页面翻动是基本一致的。
             * 如果不是相邻的情况，比如我从第一个页面跳到第三个页面，position先是0，然后逐步变成1，然后逐步变成2；
             * 我从第三个页面跳到第一个页面，position先是1，然后逐步变成0，并没有出现为2的情况。
             * positionOffset是当前页面滑动比例，如果页面向右翻动，这个值不断变大，最后在趋近1的情况后突变为0。
             * 如果页面向左翻动，这个值不断变小，最后变为0。
             * positionOffsetPixels是当前页面滑动像素，变化情况和positionOffset一致。
             **/
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mIndex == position) {//向右移动
                    ChangeLine((int) ((mIndex + positionOffset) * mSreenWidth / 3));
                } else {//向左移动
                    ChangeLine((int) ((mIndex + positionOffset - 1) * mSreenWidth / 3));
                }
            }

            /**
             * 用手指滑动翻页的时候表示哪个页面被选中
             * 如果翻动成功了（滑动的距离够长），手指抬起来就会立即执行这个方法，
             * position就是当前滑动到的页面
             */

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if (mIndex == 1) {
                            mOnlineMusicImg.setImageResource(R.drawable.icon_online_music_white);
                            mOnlineMusicText.setTextColor(Color.WHITE);
                        } else if (mIndex == 2) {
                            mLocalMusicImg.setImageResource(R.drawable.icon_local_music_white);
                            mLocalMusicText.setTextColor(Color.WHITE);
                        }
                        mShinImg.setImageResource(R.drawable.icon_shin_red);
                        mShinText.setTextColor(Color.RED);
                        mIndex = 0;
                        ChangeLine(0);
                        break;
                    case 1:
                        if (mIndex == 0) {
                            mShinImg.setImageResource(R.drawable.icon_shin_white);
                            mShinText.setTextColor(Color.WHITE);
                        } else if (mIndex == 2) {
                            mLocalMusicImg.setImageResource(R.drawable.icon_local_music_white);
                            mLocalMusicText.setTextColor(Color.WHITE);
                        }
                        mOnlineMusicImg.setImageResource(R.drawable.icon_online_music_red);
                        mOnlineMusicText.setTextColor(Color.RED);
                        mIndex = 1;
                        ChangeLine(mSreenWidth / 3);
                        break;
                    case 2:
                        if (mIndex == 0) {
                            mShinImg.setImageResource(R.drawable.icon_shin_white);
                            mShinText.setTextColor(Color.WHITE);
                        } else if (mIndex == 1) {
                            mOnlineMusicImg.setImageResource(R.drawable.icon_online_music_white);
                            mOnlineMusicText.setTextColor(Color.WHITE);
                        }
                        mLocalMusicImg.setImageResource(R.drawable.icon_local_music_red);
                        mLocalMusicText.setTextColor(Color.RED);
                        mIndex = 2;
                        ChangeLine(2 * (mSreenWidth / 3));
                        break;
                }
            }

            /**
             * 这个方法在手指操作屏幕的时候发生变化。有三个值：0（END）,1(PRESS) , 2(UP) 。
             * 当用手指滑动翻页时，手指按下去的时候会触发这个方法，state值为1，
             * 手指抬起时，如果发生了滑动（即使很小），这个值会变为2，然后最后变为0 。总共执行这个方法三次。
             * 一种特殊情况是手指按下去以后一点滑动也没有发生，这个时候只会调用这个方法两次，state值分别是1,0 。
             * 当setCurrentItem翻页时，会执行这个方法两次，state值分别为2 , 0 。
             */

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mShin.setOnClickListener(this);
        mOnlineMusic.setOnClickListener(this);
        mLocalMusic.setOnClickListener(this);
        mTopbarMusic.setOnClickListener(this);
        mTopbarSetting.setOnClickListener(this);


        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_SDCARD, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    /**
     * 改变偏移长度
     *
     * @param len
     */
    private void ChangeLine(int len) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mLine.getLayoutParams();
        lp.leftMargin = len;
        mLine.setLayoutParams(lp);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shin:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.Online_music:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.Local_music:
                mViewPager.setCurrentItem(2);
                break;
            case R.id.Topbar_setting:
                Opensetting();
                break;
            case R.id.Topbar_music:
                Openmusic();
                break;
        }
    }

    private void Openmusic() {
        Intent intent = new Intent(this, MusicPlayActivity.class);
        startActivity(intent);
    }

    private void Opensetting() {
        Intent intent = new Intent(this, LoginMenuActivity.class);
        startActivity(intent);
    }


    @PermissionGrant(REQUECT_CODE_SDCARD)
    public void requestSdcardSuccess() {

    }


    @PermissionDenied(REQUECT_CODE_SDCARD)
    public void requestSdcardFailed() {
        Toast.makeText(this, "+未允许读取SD卡的权限，无法获取本地歌曲", Toast.LENGTH_SHORT).show();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service1) {
            mMusicService = ((MusicService.MusicBinder) service1).getService();
            mMusicService.changNotifi();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

}