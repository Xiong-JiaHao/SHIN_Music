package com.gin.xjh.shin_music.User;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Follow;
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
    private static volatile List<User> mConcernList = null;


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

    public static void addLikeSong(final Context context, final ImageView imageView, final Song song) {
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
                        ListDataSaveUtil.setSongList("likesong", likeSongList);
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

    public static void removeLikeSong(final Context context, final ImageView imageView, final Song song) {
        synchronized (User_state.class) {
            if (likeSongList == null) {
                Toast.makeText(context, "喜欢的音乐中没有该歌曲", Toast.LENGTH_SHORT).show();
                return;
            }
            LikeSong likeSong = null;
            int index = 0;
            for (Song likesong : likeSongList) {
                if (likesong.equals(song)) {
                    likeSong = new LikeSong();
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
                        ListDataSaveUtil.setSongList("likesong", likeSongList);
                    } else {
                        Toast.makeText(context, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static List<User> getConcernList() {
        return mConcernList;
    }

    public static void setConcernList(List<User> concernList) {
        synchronized (User_state.class) {
            User_state.mConcernList = concernList;
        }
    }

    public static void addConcern(final Context context, final ImageView imageView, final User user) {
        synchronized (User_state.class) {
            if (mConcernList == null) {
                mConcernList = new ArrayList<>();
            }
            Follow concernUser = new Follow(User_state.getLoginUser().getUserId(), user);
            concernUser.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        imageView.setImageResource(R.drawable.concern_red);
                        user.setObjectId(s);
                        mConcernList.add(user);
                        ListDataSaveUtil.setUserList("concernUser", mConcernList);
                    } else {
                        Toast.makeText(context, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static boolean isConcern(User user) {
        if (mConcernList == null) {
            return false;
        }
        for (User user1 : mConcernList) {
            if (user1.equals(user)) {
                return true;
            }
        }
        return false;
    }

    public static void removeConcern(final Context context, final ImageView imageView, final User user) {
        synchronized (User_state.class) {
            if (mConcernList == null) {
                Toast.makeText(context, "你没有关注任何人", Toast.LENGTH_SHORT).show();
                return;
            }
            int index = 0;
            Follow concernUser = null;
            for (User user1 : mConcernList) {
                if (user1.equals(user)) {
                    concernUser = new Follow();
                    concernUser.setObjectId(user1.getObjectId());
                    break;
                }
                index++;
            }
            if (concernUser == null) {
                Toast.makeText(context, "你没有关注该用户", Toast.LENGTH_SHORT).show();
                return;
            }
            final int finalIndex = index;
            concernUser.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        imageView.setImageResource(R.drawable.concern_gray);
                        mConcernList.remove(finalIndex);
                        ListDataSaveUtil.setUserList("concernUser", mConcernList);
                    } else {
                        Toast.makeText(context, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void removeConcern(final Context context, final int index) {
        synchronized (User_state.class) {
            Follow concernUser = new Follow();
            concernUser.setObjectId(mConcernList.get(index).getObjectId());
            concernUser.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        mConcernList.remove(index);
                        ListDataSaveUtil.setUserList("concernUser", mConcernList);
                    } else {
                        Toast.makeText(context, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}