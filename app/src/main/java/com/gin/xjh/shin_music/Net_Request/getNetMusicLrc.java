package com.gin.xjh.shin_music.Net_Request;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.gin.xjh.shin_music.Interface.RequestServices_MusicLrc;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class getNetMusicLrc {

    private Context mContext;

    private Song song;
    private MediaPlayer mediaPlayer;

    private static final int REQUEST_SUCCESS = 200;

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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void getJson(Song song, MediaPlayer mediaPlayer) {
        this.song = song;
        this.mediaPlayer = mediaPlayer;
        obtainMainHandler();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.URL_BASE)
                .build();
        RequestServices_MusicLrc requestServices = retrofit.create(RequestServices_MusicLrc.class);
        retrofit2.Call<ResponseBody> call = requestServices.getString(song.getSongId());
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
