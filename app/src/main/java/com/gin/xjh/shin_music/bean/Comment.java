package com.gin.xjh.shin_music.bean;

/**
 * Created by Gin on 2018/4/24.
 */

public class Comment {

    private String UserName;//用户名称
    //private int UserId;//用户ID
    private String MyComment;//评论内容
    private String Times;//时间

    public String getUserName() {
        return UserName;
    }

    public String getMyComment() {
        return MyComment;
    }

    public String getTimes() {
        return Times;
    }

    public Comment(String UserName,String MyComment,String Times){
        this.UserName = UserName;
        this.MyComment = MyComment;
        this.Times = Times;
    }

}
