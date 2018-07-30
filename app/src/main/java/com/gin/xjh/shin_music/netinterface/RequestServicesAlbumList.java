package com.gin.xjh.shin_music.netinterface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RequestServicesAlbumList {

    @GET("album")
        //定义返回的方法，返回的响应体使用了ResponseBody
    Call<ResponseBody> getString(@Query("id") Long Id);

}
