package com.gin.xjh.shin_music.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Gin on 2018/4/24.
 */

public class Comment extends BmobObject{

    private String UserName;//用户名称
    private String UserId;//用户ID
    private String MyComment;//评论内容
    private String Times;//时间

    public Comment(String UserName,String UserId, String MyComment, String Times) {
        this.UserName = UserName;
        this.UserId = UserId;
        this.MyComment = MyComment;
        this.Times = Times;
    }

    public String getUserId() {
        return UserId;
    }

    public String getUserName() {
        return UserName;
    }

    public String getMyComment() {
        return MyComment;
    }

    public String getTimes() {
        return Times;
    }

}
