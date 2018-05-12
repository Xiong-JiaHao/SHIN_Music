package com.gin.xjh.shin_music.util;

import android.util.Log;

import com.gin.xjh.shin_music.Interface.RequestServices;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NetUtile {


    public static String getJson(String url) {
        final String[] result = new String[1];
        result[0] = null;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .build();
        RequestServices requestServices = retrofit.create(RequestServices.class);
        Call<ResponseBody> call = requestServices.getString();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        //返回的结果保存在response.body()中
                        result[0] = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("NET", "访问失败");
            }
        });
        return result[0];
    }

}
