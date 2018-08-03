package com.gin.xjh.shin_music.netrequest;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.interfaces.RequestServicesMusicDetailInter;
import com.gin.xjh.shin_music.activities.MusicPlayActivity;
import com.gin.xjh.shin_music.notification.MusicNotification;
import com.gin.xjh.shin_music.utils.ConstantUtil;
import com.gin.xjh.shin_music.utils.MusicUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetNetMusicDetail {

    private Context mContext;

    private static final int REQUEST_SUCCESS = 202;

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
                        String JSONString = AllObject.getString(mContext.getString(R.string.SONGS));
                        JSONArray jsonArray = new JSONArray(JSONString);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        int time = jsonObject.getInt(mContext.getString(R.string.SONG_TIME));
                        String urlString = jsonObject.getString(mContext.getString(R.string.URL_STR));
                        JSONObject urlObject = new JSONObject(urlString);
                        String url = urlObject.getString(mContext.getString(R.string.PICURL));
                        MusicUtil.getSongList().get(MusicUtil.getIndex()).setSongTime(time);
                        MusicUtil.getSongList().get(MusicUtil.getIndex()).setAlbumUrl(url);
                        Intent intent = new Intent(MusicPlayActivity.MUSIC_ACTION_CHANGE);
                        android.support.v4.content.LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                        MusicNotification.getMusicNotification(mContext).onUpdataMusicNotifi();
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
                .baseUrl(ConstantUtil.URL_BASE)
                .build();
        RequestServicesMusicDetailInter requestServices = retrofit.create(RequestServicesMusicDetailInter.class);
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
