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

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.interfaces.RequestServicesMusicListInter;
import com.gin.xjh.shin_music.adapter.MusicRecyclerViewAdapter;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.utils.ConstantUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetNetMusicList {

    private RecyclerView mMusicListRv;
    private TextView mMusicListHint;
    private Context mContext;

    private List<Song> mSongList;
    private MusicRecyclerViewAdapter mMusicRecyclerViewAdapter;

    private static final int REQUEST_SUCCESS = 203;

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
                        String ListString = AllObject.getString(mContext.getString(R.string.PLAYLIST));
                        JSONObject ListObject = new JSONObject(ListString);
                        String JSONString = ListObject.getString(mContext.getString(R.string.TRACKS));
                        JSONArray jsonArray = new JSONArray(JSONString);
                        int len = Math.min(50,jsonArray.length());
                        for (int i = 0; i < len; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            //歌手
                            String ar = jsonObject.getString(mContext.getString(R.string.SINGER_ARRAY));
                            JSONArray arArray = new JSONArray(ar);
                            JSONObject arObject = arArray.getJSONObject(0);
                            String Singer = arObject.getString(mContext.getString(R.string.NAME));
                            Long SingerId = arObject.getLong(mContext.getString(R.string.ID));
                            //专辑
                            String al = jsonObject.getString(mContext.getString(R.string.URL_STR));
                            JSONObject alObject = new JSONObject(al);
                            String AlbumName = alObject.getString(mContext.getString(R.string.NAME));
                            String AlbumUri = alObject.getString(mContext.getString(R.string.PICURL));
                            Long AlbumId = alObject.getLong(mContext.getString(R.string.ID));
                            Song song = new Song(jsonObject.getString(mContext.getString(R.string.NAME)),
                                    jsonObject.getLong(mContext.getString(R.string.ID)), Singer,
                                    SingerId, AlbumName, AlbumUri, jsonObject.getInt("dt"));
                            song.setAlbumId(AlbumId);
                            song.setAlbumTime(jsonObject.getLong(mContext.getString(R.string.PUBLISH_TIME)));
                            mSongList.add(song);
                        }

                        //RecyclerView
                        mMusicRecyclerViewAdapter = new MusicRecyclerViewAdapter(mContext, mSongList);
                        mMusicListRv.setLayoutManager(new LinearLayoutManager(mContext));
                        mMusicListRv.setItemAnimator(new DefaultItemAnimator());//默认动画
                        mMusicListRv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
                        mMusicListRv.setAdapter(mMusicRecyclerViewAdapter);

                        //取消加载提醒
                        mMusicListHint.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public void getJson(int id, View music_list_rv, View music_list_hint, Context mContext, List<Song> mSongList) {
        this.mMusicListRv = (RecyclerView) music_list_rv;
        this.mMusicListHint = (TextView) music_list_hint;
        this.mContext = mContext;
        this.mSongList = mSongList;
        obtainMainHandler();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantUtil.URL_BASE)
                .build();
        RequestServicesMusicListInter requestServices = retrofit.create(RequestServicesMusicListInter.class);
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
