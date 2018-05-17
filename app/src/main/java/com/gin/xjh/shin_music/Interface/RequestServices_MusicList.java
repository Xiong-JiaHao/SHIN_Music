package com.gin.xjh.shin_music.Interface;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RequestServices_MusicList {

    //请求方式为GET，参数为basil2style，因为没有变量所以下面getString方法也不需要参数
    @GET("top/list")
    //定义返回的方法，返回的响应体使用了ResponseBody
    Call<ResponseBody> getString(@Query("idx") int groupId);

}
