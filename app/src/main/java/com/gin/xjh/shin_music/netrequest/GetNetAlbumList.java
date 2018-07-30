package com.gin.xjh.shin_music.netrequest;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gin.xjh.shin_music.netinterface.RequestServicesAlbumList;
import com.gin.xjh.shin_music.adapter.MusicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.util.ConstantUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetNetAlbumList {

    private RecyclerView music_list_rv;
    private TextView music_list_hint;
    private Context mContext;

    private List<Song> mSongList;
    private MusicRecyclerViewAdapter mMusicRecyclerViewAdapter;

    private static final int REQUEST_SUCCESS = 201;

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
                        String AlbumString = AllObject.getString("album");
                        JSONObject AlbumObject = new JSONObject(AlbumString);
                        Long time = AlbumObject.getLong("publishTime");
                        String AlbumName = AlbumObject.getString("name");
                        String AlbumUri = AlbumObject.getString("blurPicUrl");
                        Long AlbumId = AlbumObject.getLong("id");
                        int len = Math.min(50, jsonArray.length());
                        for (int i = 0; i < len; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            //歌手
                            String ar = jsonObject.getString("ar");
                            JSONArray arArray = new JSONArray(ar);
                            JSONObject arObject = arArray.getJSONObject(0);
                            String Singer = arObject.getString("name");
                            Long SingerId = arObject.getLong("id");
                            Song song = new Song(jsonObject.getString("name"), jsonObject.getLong("id"), Singer, SingerId, AlbumName, AlbumUri, jsonObject.getInt("dt"));
                            song.setAlbumId(AlbumId);
                            song.setAlbumTime(time);
                            mSongList.add(song);
                        }

                        //RecyclerView
                        mMusicRecyclerViewAdapter = new MusicRecyclerViewAdapter(mContext, mSongList);
                        music_list_rv.setLayoutManager(new LinearLayoutManager(mContext));
                        music_list_rv.setItemAnimator(new DefaultItemAnimator());//默认动画
                        music_list_rv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                        music_list_rv.setAdapter(mMusicRecyclerViewAdapter);

                        //取消加载提醒
                        music_list_hint.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void getJson(Long id, View music_list_rv, View music_list_hint, Context mContext) {
        this.music_list_rv = (RecyclerView) music_list_rv;
        this.music_list_hint = (TextView) music_list_hint;
        this.mContext = mContext;
        mSongList = new ArrayList<>();
        obtainMainHandler();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantUtil.URL_BASE)
                .build();
        RequestServicesAlbumList requestServices = retrofit.create(RequestServicesAlbumList.class);
        retrofit2.Call<ResponseBody> call = requestServices.getString(id);
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
