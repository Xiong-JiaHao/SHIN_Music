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

import com.gin.xjh.shin_music.netinterface.RequestServicesMusicList;
import com.gin.xjh.shin_music.adapter.RecommendMusicRecyclerViewAdapter;
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

public class GetNetNewMusic {

    private RecyclerView mRecyclerView;
    private TextView mMusicListHint;
    private Context mContext;

    private List<Song> mSongList;
    private RecommendMusicRecyclerViewAdapter mRecommendmusicRecyclerViewAdapter;

    private static final int REQUEST_SUCCESS = 205;

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
                        String ListString = AllObject.getString("playlist");
                        JSONObject ListObject = new JSONObject(ListString);
                        String JSONString = ListObject.getString("tracks");
                        JSONArray jsonArray = new JSONArray(JSONString);
                        int len = Math.min(50,jsonArray.length());
                        for (int i = 0; i < len; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            //歌手
                            String ar = jsonObject.getString("ar");
                            JSONArray arArray = new JSONArray(ar);
                            JSONObject arObject = arArray.getJSONObject(0);
                            String Singer = arObject.getString("name");
                            Long SingerId = arObject.getLong("id");

                            //专辑
                            String al = jsonObject.getString("al");
                            JSONObject alObject = new JSONObject(al);
                            String AlbumName = alObject.getString("name");
                            String AlbumUri = alObject.getString("picUrl");
                            Long AlbumId = alObject.getLong("id");
                            Song song = new Song(jsonObject.getString("name"), jsonObject.getLong("id"), Singer, SingerId, AlbumName, AlbumUri, jsonObject.getInt("dt"));
                            song.setAlbumId(AlbumId);
                            song.setAlbumTime(jsonObject.getLong("publishTime"));
                            mSongList.add(song);
                        }

                        //RecyclerView
                        mRecommendmusicRecyclerViewAdapter = new RecommendMusicRecyclerViewAdapter(mContext, mSongList);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//默认动画
                        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                        mRecyclerView.setAdapter(mRecommendmusicRecyclerViewAdapter);

                        //取消加载提醒
                        mMusicListHint.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void getJson(int id, View mRecyclerView, View music_list_hint, Context mContext) {
        this.mRecyclerView = (RecyclerView) mRecyclerView;
        this.mMusicListHint = (TextView) music_list_hint;
        this.mContext = mContext;
        mSongList = new ArrayList<>();
        obtainMainHandler();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantUtil.URL_BASE)
                .build();
        RequestServicesMusicList requestServices = retrofit.create(RequestServicesMusicList.class);
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
