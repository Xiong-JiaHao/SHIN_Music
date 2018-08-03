package com.gin.xjh.shin_music.netrequest;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gin.xjh.shin_music.interfaces.RequestServicesMusicLrcInter;
import com.gin.xjh.shin_music.utils.ConstantUtil;
import com.gin.xjh.shin_music.utils.MusicUtil;
import com.gin.xjh.shin_music.view.LyricView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetNetMusicLrc {


    private TextView mHint;
    private LyricView mLyricView;

    private static final int REQUEST_SUCCESS = 204;

    private Handler mMainHandler;

    private void obtainMainHandler() {
        if (mMainHandler != null) {
            return;
        }
        mMainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == REQUEST_SUCCESS) {
                    try {
                        String result = (String) msg.obj;
                        JSONObject AllObject = new JSONObject(result);
                        String JSONString = AllObject.getString("lrc");
                        JSONObject jsonObject = new JSONObject(JSONString);
                        String lyric = jsonObject.getString("lyric");
                        MusicUtil.getNowSong().setLyric(lyric);
                        mLyricView.getLyric(lyric);
                        mHint.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void getJson(LyricView lyricView, TextView hint) {
        this.mLyricView = lyricView;
        this.mHint = hint;
        obtainMainHandler();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantUtil.URL_BASE)
                .build();
        RequestServicesMusicLrcInter requestServices = retrofit.create(RequestServicesMusicLrcInter.class);
        retrofit2.Call<ResponseBody> call = requestServices.getString(MusicUtil.getNowSong().getSongId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        //返回的结果保存在response.body()中
                        Message msg = new Message();
                        msg.what = REQUEST_SUCCESS;
                        msg.obj = response.body().string();
                        mMainHandler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Log.i("NET", "访问失败");
            }
        });
    }

}
