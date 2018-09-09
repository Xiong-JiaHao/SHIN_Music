package com.gin.xjh.shin_music.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;

import com.gin.xjh.shin_music.bean.Lyric;
import com.gin.xjh.shin_music.utils.DisplayUtils;
import com.gin.xjh.shin_music.utils.LrcUtils;
import com.gin.xjh.shin_music.utils.MusicUtil;

import java.util.List;


public class LyricView extends android.support.v7.widget.AppCompatTextView {
    public static final String LYRIC_ACTION_PLAY = "Lyric.To.Play";
    public static final String LYRIC_ACTION_PAUSE = "Lyric.To.Pause";
    private static final int LYRIC = 206;
    private List<Lyric> mLyricList;
    // 标记当前行
    private int mCurrentLine = -1;
    private Paint mCurrentPaint;
    private Paint mOtherPaint;
    private int mCurrentColor = Color.rgb(80, 4, 4);
    private float mCurrentTextSize = 19;
    private int mOtherColor = Color.BLACK;
    private float mOtherTextSize = 16;
    //歌词总行数一半
    private static final int MAX_LYRIC = 10;
    // 行间距
    private float mLineSpace = 30;
    //当前歌词字体
    private Typeface mCurrentTypeface = Typeface.DEFAULT_BOLD;
    //其他歌词字体
    private Typeface mOtherTypeface = Typeface.SERIF;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(mLyricList != null){
                if (mCurrentLine != mLyricList.size() - 1) {
                    if (MusicUtil.getPlayTime() > mLyricList.get(mCurrentLine + 1).timePoint) {
                        while (mCurrentLine < mLyricList.size() - 1 && MusicUtil.getPlayTime() > mLyricList.get(mCurrentLine + 1).timePoint) {
                            mCurrentLine++;
                        }
                        invalidate(); // 刷新,会再次调用onDraw方法
                    }
                    if (MusicUtil.isPlayMusic()) {
                        mHandler.sendEmptyMessageDelayed(LYRIC, 50);//自己给自己刷新
                    }
                }
            }
        }

    };

    private LyricBroadCast mLyricBroadCast;

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCurrentPaint = new Paint();
        mOtherPaint = new Paint();
        mLyricList = null;

        mLineSpace = DisplayUtils.sp2px(context, mLineSpace);

        mCurrentPaint.setColor(mCurrentColor);
        mCurrentPaint.setTextSize(DisplayUtils.sp2px(context, mCurrentTextSize));
        mCurrentPaint.setTextAlign(Paint.Align.CENTER); // 画在中间
        mCurrentPaint.setTypeface(mCurrentTypeface);

        mOtherPaint.setColor(mOtherColor);
        mOtherPaint.setTextSize(DisplayUtils.sp2px(context, mOtherTextSize));
        mOtherPaint.setTextAlign(Paint.Align.CENTER);
        mOtherPaint.setTypeface(mOtherTypeface);

        mLyricBroadCast = new LyricBroadCast();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LYRIC_ACTION_PLAY);
        intentFilter.addAction(LYRIC_ACTION_PAUSE);
        broadcastManager.registerReceiver(mLyricBroadCast, intentFilter);
    }

    public void getLyric(String lyrics) {
        if (lyrics != null) {
            mLyricList = LrcUtils.readLRC(lyrics);
            mCurrentLine = -1;
            mHandler.sendEmptyMessage(LYRIC);
        } else {
            mLyricList = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLyricList != null && mLyricList.size() > 0) {
            Lyric lyric;
            //绘制播放过的歌词
            //特判起点不为0的时候
            if (mCurrentLine == -1) {
                mCurrentLine = 0;
            }

            for (int i = mCurrentLine - 1, j = 0; i >= 0 && j < MAX_LYRIC; i--, j++) {
                lyric = mLyricList.get(i);
                canvas.drawText(lyric.lricString, getWidth() / 2,
                        getHeight() / 2 + mLineSpace * (i - mCurrentLine), mOtherPaint);
            }
            lyric = mLyricList.get(mCurrentLine);
            // 绘制正在播放的歌词
            canvas.drawText(lyric.lricString, getWidth() / 2,
                    getHeight() / 2, mCurrentPaint);
            //绘制未播放的歌词
            for (int i = mCurrentLine + 1, j = 0; i < mLyricList.size() && j < MAX_LYRIC; i++, j++) {
                lyric = mLyricList.get(i);
                canvas.drawText(lyric.lricString, getWidth() / 2,
                        getHeight() / 2 + mLineSpace * (i - mCurrentLine), mOtherPaint);
            }
        } else {
            canvas.drawText("未找到歌词", getWidth() / 2,
                    getHeight() / 2, mOtherPaint);
        }

    }

    public class LyricBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case LYRIC_ACTION_PLAY:
                    mCurrentLine = -1;
                    mHandler.sendEmptyMessage(LYRIC);
                    break;
                case LYRIC_ACTION_PAUSE:
                    mHandler.removeMessages(LYRIC);
                    break;
            }
        }
    }
}
