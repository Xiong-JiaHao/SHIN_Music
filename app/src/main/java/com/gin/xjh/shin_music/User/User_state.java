package com.gin.xjh.shin_music.User;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.LikeSong;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.util.ListDataSaveUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class User_state {

    private static volatile boolean Use_4G = false;
    private static volatile boolean Login_flag = false;
    private static volatile User user = null;
    private static volatile List<Song> likeSongList = null;


    public static void setUse_4G(boolean use_4G) {
        Use_4G = use_4G;
    }

    public static void Login(User Loginuser) {
        synchronized (User_state.class) {
            user = Loginuser;
            Login_flag = true;
        }
    }

    public static void Logout() {
        if (Login_flag) {
            synchronized (User_state.class) {
                if (Login_flag) {
                    Login_flag = false;
                    user = null;
                }
            }
        }
    }

    public static boolean isUse_4G() {
        return Use_4G;
    }

    public static User getLoginUser() {
        if (Login_flag) {
            synchronized (User_state.class) {
                if (Login_flag) {
                    return user;
                }
            }
        }
        return null;
    }

    public static boolean getState() {
        synchronized (User_state.class) {
            return Login_flag;
        }
    }

    public static List<Song> getLikeSongList() {
        return likeSongList;
    }

    public static void setLikeSongList(List<Song> likeSongList) {
        synchronized (User_state.class) {
            User_state.likeSongList = likeSongList;
        }
    }

    public static void addLikeSongList(final Context context, final ImageView imageView, final Song song) {
        synchronized (User_state.class) {
            if (likeSongList == null) {
                likeSongList = new ArrayList<>();
            }
            LikeSong likeSong = new LikeSong(User_state.getLoginUser().getUserId(), song);
            likeSong.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        imageView.setImageResource(R.drawable.likesong);
                        song.setObjectId(s);
                        likeSongList.add(song);
                        ListDataSaveUtil.setDataList("likesong", likeSongList);
                    } else {
                        Toast.makeText(context, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static boolean isLikeSong(Song song) {
        if (likeSongList == null) {
            return false;
        }
        for (Song likesong : likeSongList) {
            if (likesong.equals(song)) {
                return true;
            }
        }
        return false;
    }

    public static void removeLikeSongList(final Context context, final ImageView imageView, final Song song) {
        synchronized (User_state.class) {
            if (likeSongList == null) {
                Toast.makeText(context, "喜欢的音乐中没有该歌曲", Toast.LENGTH_SHORT).show();
                return;
            }
            LikeSong likeSong = null;
            int index = 0;
            for (Song likesong : likeSongList) {
                if (likesong.equals(song)) {
                    likeSong = new LikeSong(User_state.getLoginUser().getUserId(), likesong);
                    likeSong.setObjectId(likesong.getObjectId());
                    break;
                }
                index++;
            }
            if (likeSong == null) {
                Toast.makeText(context, "喜欢的音乐中没有该歌曲", Toast.LENGTH_SHORT).show();
                return;
            }
            final int finalIndex = index;
            likeSong.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        imageView.setImageResource(R.drawable.unlikesong);
                        likeSongList.remove(finalIndex);
                        ListDataSaveUtil.setDataList("likesong", likeSongList);
                    } else {
                        Toast.makeText(context, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}