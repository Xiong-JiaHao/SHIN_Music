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
import com.gin.xjh.shin_music.util.LrcUtils;
import com.gin.xjh.shin_music.util.MusicUtil;

import java.util.List;


public class LyricView extends android.support.v7.widget.AppCompatTextView {
    public static final String LYRIC_ACTION_PLAY = "Lyric.To.Play";
    public static final String LYRIC_ACTION_PAUSE = "Lyric.To.Pause";
    private static final int LYRIC = 200;
    private List<Lyric> lyricList;
    // 标记当前行
    private int currentLine = -1;
    private Paint currentPaint;
    private Paint otherPaint;
    private int currentColor = Color.rgb(179, 11, 11);
    private int currentTextSize = 60;
    private int otherColor = Color.BLACK;
    private int otherTextSize = 40;
    //歌词总行数一半
    private int MAX_LYRIC = 7;
    // 行间距
    private int lineSpace = 70;
    //当前歌词字体
    private Typeface currentTypeface = Typeface.DEFAULT_BOLD;
    //其他歌词字体
    private Typeface otherTypeface = Typeface.SERIF;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if(lyricList != null){
                if (currentLine != lyricList.size() - 1) {
                    if (MusicUtil.getPlayTime() > lyricList.get(currentLine + 1).timePoint) {
                        while (currentLine < lyricList.size() - 1 && MusicUtil.getPlayTime() > lyricList.get(currentLine + 1).timePoint) {
                            currentLine++;
                        }
                        invalidate(); // 刷新,会再次调用onDraw方法
                    }
                    if (MusicUtil.isPlayMusic()) {
                        handler.sendEmptyMessageDelayed(LYRIC, 50);//自己给自己刷新
                    }
                }
            }
        }

    };

    private LyricBroadCast mLyricBroadCast;

    public LyricView(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentPaint = new Paint();
        otherPaint = new Paint();
        lyricList = null;

        currentPaint.setColor(currentColor);
        currentPaint.setTextSize(currentTextSize);
        currentPaint.setTextAlign(Paint.Align.CENTER); // 画在中间
        currentPaint.setTypeface(currentTypeface);

        otherPaint.setColor(otherColor);
        otherPaint.setTextSize(otherTextSize);
        otherPaint.setTextAlign(Paint.Align.CENTER);
        otherPaint.setTypeface(otherTypeface);

        mLyricBroadCast = new LyricBroadCast();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LYRIC_ACTION_PLAY);
        intentFilter.addAction(LYRIC_ACTION_PAUSE);
        broadcastManager.registerReceiver(mLyricBroadCast, intentFilter);
    }

    public void getLyric(String lyrics) {
        //Log.d("xjhlyric", lyrics);
        lyricList = LrcUtils.readLRC(lyrics);
        currentLine = -1;
        handler.sendEmptyMessage(LYRIC);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (lyricList != null && lyricList.size() > 0) {
            Lyric lyric = null;
            //绘制播放过的歌词

            //特判起点不为0的时候
            if (currentLine == -1) {
                currentLine = 0;
            }

            for (int i = currentLine - 1, j = 0; i >= 0 && j < MAX_LYRIC; i--, j++) {
                lyric = lyricList.get(i);
                canvas.drawText(lyric.lricString, getWidth() / 2,
                        getHeight() / 2 + lineSpace * (i - currentLine), otherPaint);
            }
            lyric = lyricList.get(currentLine);
            // 绘制正在播放的歌词
            canvas.drawText(lyric.lricString, getWidth() / 2,
                    getHeight() / 2, currentPaint);
            //绘制未播放的歌词
            for (int i = currentLine + 1, j = 0; i < lyricList.size() && j < MAX_LYRIC; i++, j++) {
                lyric = lyricList.get(i);
                canvas.drawText(lyric.lricString, getWidth() / 2,
                        getHeight() / 2 + lineSpace * (i - currentLine), otherPaint);
            }
        } else {
            canvas.drawText("未找到歌词", getWidth() / 2,
                    getHeight() / 2, otherPaint);
        }
        super.onDraw(canvas);
    }

    public class LyricBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case LYRIC_ACTION_PLAY:
                    currentLine = -1;
                    handler.sendEmptyMessage(LYRIC);
                    break;
                case LYRIC_ACTION_PAUSE:
                    handler.removeMessages(LYRIC);
                    break;
            }
        }
    }
}
