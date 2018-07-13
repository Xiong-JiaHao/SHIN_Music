package com.gin.xjh.shin_music;

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

import com.gin.xjh.shin_music.User.User_state;
import com.gin.xjh.shin_music.adapter.FragmentAdapter;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.fragment.Fragment_Local;
import com.gin.xjh.shin_music.fragment.Fragment_Online;
import com.gin.xjh.shin_music.fragment.Fragment_Shin;
import com.gin.xjh.shin_music.service.MusicService;
import com.gin.xjh.shin_music.util.ListDataSaveUtil;
import com.gin.xjh.shin_music.util.MusicUtil;
import com.gin.xjh.shin_music.util.TimesUtil;
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
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mTopbar;
    private ImageView Topbar_setting, Topbar_music;//菜单按钮，播放器按钮
    private ViewPager mViewPager;
    private LinearLayout mline, shin, Online_music, Local_music;
    private ImageView shin_img, Online_music_img, Local_music_img;
    private TextView shin_text, Online_music_text, Local_music_text;

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentAdapter adapter;

    //记录当前为哪个Fragment以及当前的屏幕宽度
    private int Index;
    private int mSreenWidth;


    private static final int REQUECT_CODE_SDCARD = 2;


    private MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBmob();
        initalize();
        initView();
        initEvent();
        Intent startIntent = new Intent(this, MusicService.class);
        bindService(startIntent, connection, BIND_AUTO_CREATE);
    }

    private void initBmob() {

        Bmob.initialize(this, "df98b1644c7d3aa94239034059791d40");

        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {

            }
        });
        // 启动推送服务
        BmobPush.startWork(this);

    }

    private void initalize() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        ListDataSaveUtil.setPreferences(sharedPreferences);
        String user_id = sharedPreferences.getString("user_id", null);
        String user_name = sharedPreferences.getString("user_name", null);
        String password = sharedPreferences.getString("password", null);
        String user_qq = sharedPreferences.getString("user_qq", null);
        int user_sex = sharedPreferences.getInt("user_sex", 0);
        String personal_profile = sharedPreferences.getString("personal_profile", null);
        String objId = sharedPreferences.getString("objId", null);
        Long time = sharedPreferences.getLong("time", -1L);
        Long nowtime = TimesUtil.dateToLong(new Date(System.currentTimeMillis()));
        if (user_id != null && nowtime - time < 432000000) {
            User user = new User(user_id, user_name, password, user_qq, user_sex, personal_profile);
            user.setObjectId(objId);
            user.setLikeSongListName(sharedPreferences.getString("likesonglistname", null));
            boolean ispublic = sharedPreferences.getBoolean("public_song", false);
            if(ispublic){
                user.changPublic_song();
            }
            User_state.Login(user);
            User_state.setLikeSongList(ListDataSaveUtil.getDataList("likesong"));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("time", nowtime);
            editor.commit();
        }
        User_state.setUse_4G(sharedPreferences.getBoolean("use4G", false));
        MusicUtil.setPlay_state(sharedPreferences.getInt("play_state", 0));
        int index = sharedPreferences.getInt("index", -1);
        if (index >= 0) {
            MusicUtil.setIndex(index);
            MusicUtil.changeSongList(ListDataSaveUtil.getDataList("songlist"));
        }
    }

    private void initView() {
        mTopbar = findViewById(R.id.Top_bar);
        Topbar_setting = findViewById(R.id.Topbar_setting);
        Topbar_music = findViewById(R.id.Topbar_music);
        mViewPager = findViewById(R.id.fragment_VP);
        mline = findViewById(R.id.main_line);
        shin = findViewById(R.id.shin);
        Online_music = findViewById(R.id.Online_music);
        Local_music = findViewById(R.id.Local_music);
        shin_img = findViewById(R.id.shin_img);
        Online_music_img = findViewById(R.id.Online_music_img);
        Local_music_img = findViewById(R.id.Local_music_img);
        shin_text = findViewById(R.id.shin_text);
        Online_music_text = findViewById(R.id.Online_music_text);
        Local_music_text = findViewById(R.id.Local_music_text);
    }

    private void initEvent() {
        fragments.add(new Fragment_Shin());
        fragments.add(new Fragment_Online());
        fragments.add(new Fragment_Local());

        Index = 0;
        shin_img.setImageResource(R.drawable.shin_red);
        shin_text.setTextColor(Color.RED);
        mSreenWidth = getWindowManager().getDefaultDisplay().getWidth();

        adapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(adapter);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mline.getLayoutParams();
        lp.width = mSreenWidth / 3;
        mline.setLayoutParams(lp);

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
                if (Index == position) {//向右移动
                    ChangeLine((int) ((Index + positionOffset) * mSreenWidth / 3));
                } else {//向左移动
                    ChangeLine((int) ((Index + positionOffset - 1) * mSreenWidth / 3));
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
                        if (Index == 1) {
                            Online_music_img.setImageResource(R.drawable.online_music_white);
                            Online_music_text.setTextColor(Color.WHITE);
                        } else if (Index == 2) {
                            Local_music_img.setImageResource(R.drawable.local_music_white);
                            Local_music_text.setTextColor(Color.WHITE);
                        }
                        shin_img.setImageResource(R.drawable.shin_red);
                        shin_text.setTextColor(Color.RED);
                        Index = 0;
                        ChangeLine(0);
                        break;
                    case 1:
                        if (Index == 0) {
                            shin_img.setImageResource(R.drawable.shin_white);
                            shin_text.setTextColor(Color.WHITE);
                        } else if (Index == 2) {
                            Local_music_img.setImageResource(R.drawable.local_music_white);
                            Local_music_text.setTextColor(Color.WHITE);
                        }
                        Online_music_img.setImageResource(R.drawable.online_music_red);
                        Online_music_text.setTextColor(Color.RED);
                        Index = 1;
                        ChangeLine(mSreenWidth / 3);
                        break;
                    case 2:
                        if (Index == 0) {
                            shin_img.setImageResource(R.drawable.shin_white);
                            shin_text.setTextColor(Color.WHITE);
                        } else if (Index == 1) {
                            Online_music_img.setImageResource(R.drawable.online_music_white);
                            Online_music_text.setTextColor(Color.WHITE);
                        }
                        Local_music_img.setImageResource(R.drawable.local_music_red);
                        Local_music_text.setTextColor(Color.RED);
                        Index = 2;
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

        shin.setOnClickListener(this);
        Online_music.setOnClickListener(this);
        Local_music.setOnClickListener(this);
        Topbar_music.setOnClickListener(this);
        Topbar_setting.setOnClickListener(this);


        MPermissions.requestPermissions(MainActivity.this, REQUECT_CODE_SDCARD, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    /**
     * 改变偏移长度
     *
     * @param len
     */
    private void ChangeLine(int len) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mline.getLayoutParams();
        lp.leftMargin = len;
        mline.setLayoutParams(lp);
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
        Intent intent = new Intent(this, music_play_Activity.class);
        startActivity(intent);
    }

    private void Opensetting() {
        Intent intent = new Intent(this, login_menu_Activity.class);
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
            musicService = ((MusicService.MusicBinder) service1).getService();
            musicService.changNotifi();
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