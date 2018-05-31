package com.gin.xjh.shin_music.Net_Request;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.gin.xjh.shin_music.Interface.RequestServices_MusicDetail;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.music_play_Activity;
import com.gin.xjh.shin_music.util.Constant;
import com.gin.xjh.shin_music.util.MusicUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class getNetMusicDetail {

    private Context mContext;

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
                        String JSONString = AllObject.getString("songs");
                        JSONArray jsonArray = new JSONArray(JSONString);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        Long time = jsonObject.getLong("dt");
                        String urlString = jsonObject.getString("al");
                        JSONObject urlObject = new JSONObject(urlString);
                        String url = urlObject.getString("picUrl");
                        Song song = MusicUtil.getNowSong();
                        Song newsong = new Song();
                        newsong.setSongTime(time);
                        MusicUtil.getSongList().get(MusicUtil.getIndex()).setSongTime(time);
                        MusicUtil.getSongList().get(MusicUtil.getIndex()).setAlbumUrl(url);
                        Intent intent = new Intent(music_play_Activity.MUSIC_ACTION_CHANGE);
                        android.support.v4.content.LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        newsong.update(song.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void getJson(Context context) {
        mContext = context;
        obtainMainHandler();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.URL_BASE)
                .build();
        RequestServices_MusicDetail requestServices = retrofit.create(RequestServices_MusicDetail.class);
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
