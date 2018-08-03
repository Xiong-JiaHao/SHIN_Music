package com.gin.xjh.shin_music.user;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Follow;
import com.gin.xjh.shin_music.bean.LikeSong;
import com.gin.xjh.shin_music.bean.Song;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.utils.ListDataSaveUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class UserState {

    private static volatile boolean sUse4G = false;
    private static volatile boolean sLoginFlag = false;
    private static volatile User sUser = null;
    private static volatile List<Song> sLikeSongList = null;
    private static volatile List<User> sConcernList = null;


    public static void setUse_4G(boolean use_4G) {
        sUse4G = use_4G;
    }

    public static void Login(User Loginuser) {
        synchronized (UserState.class) {
            sUser = Loginuser;
            sLoginFlag = true;
        }
    }

    public static void Logout() {
        if (sLoginFlag) {
            synchronized (UserState.class) {
                if (sLoginFlag) {
                    sLoginFlag = false;
                    sUser = null;
                }
            }
        }
    }

    public static boolean isUse_4G() {
        return sUse4G;
    }

    public static User getLoginUser() {
        if (sLoginFlag) {
            synchronized (UserState.class) {
                if (sLoginFlag) {
                    return sUser;
                }
            }
        }
        return null;
    }

    public static boolean getState() {
        synchronized (UserState.class) {
            return sLoginFlag;
        }
    }

    public static List<Song> getLikeSongList() {
        return sLikeSongList;
    }

    public static void setLikeSongList(List<Song> likeSongList) {
        synchronized (UserState.class) {
            UserState.sLikeSongList = likeSongList;
        }
    }

    public static void addLikeSong(final Context context, final ImageView imageView, final Song song) {
        synchronized (UserState.class) {
            if (sLikeSongList == null) {
                sLikeSongList = new ArrayList<>();
            }
            LikeSong likeSong = new LikeSong(UserState.getLoginUser().getUserId(), song);
            likeSong.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        imageView.setImageResource(R.drawable.btn_like_song);
                        song.setObjectId(s);
                        sLikeSongList.add(song);
                        ListDataSaveUtil.setSongList(context.getString(R.string.LIKE_SONG_LIST), sLikeSongList);
                    } else {
                        Toast.makeText(context, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static boolean isLikeSong(Song song) {
        if (sLikeSongList == null) {
            return false;
        }
        for (Song likesong : sLikeSongList) {
            if (likesong.equals(song)) {
                return true;
            }
        }
        return false;
    }

    public static void removeLikeSong(final Context context, final ImageView imageView, final Song song) {
        synchronized (UserState.class) {
            if (sLikeSongList == null) {
                Toast.makeText(context, "喜欢的音乐中没有该歌曲", Toast.LENGTH_SHORT).show();
                return;
            }
            LikeSong likeSong = null;
            int index = 0;
            for (Song likesong : sLikeSongList) {
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
                        imageView.setImageResource(R.drawable.btn_unlike_song);
                        sLikeSongList.remove(finalIndex);
                        ListDataSaveUtil.setSongList(context.getString(R.string.LIKE_SONG_LIST), sLikeSongList);
                    } else {
                        Toast.makeText(context, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static List<User> getConcernList() {
        return sConcernList;
    }

    public static void setConcernList(List<User> concernList) {
        synchronized (UserState.class) {
            UserState.sConcernList = concernList;
        }
    }

    public static void addConcern(final Context context, final ImageView imageView, final User user) {
        synchronized (UserState.class) {
            if (sConcernList == null) {
                sConcernList = new ArrayList<>();
            }
            Follow concernUser = new Follow(UserState.getLoginUser().getUserId(), user);
            concernUser.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        imageView.setImageResource(R.drawable.btn_concern_red);
                        user.setObjectId(s);
                        sConcernList.add(user);
                        ListDataSaveUtil.setUserList(context.getString(R.string.CONCERN_USER), sConcernList);
                    } else {
                        Toast.makeText(context, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static boolean isConcern(User user) {
        if (sConcernList == null) {
            return false;
        }
        for (User user1 : sConcernList) {
            if (user1.equals(user)) {
                return true;
            }
        }
        return false;
    }

    public static void removeConcern(final Context context, final ImageView imageView, final User user) {
        synchronized (UserState.class) {
            if (sConcernList == null) {
                Toast.makeText(context, "你没有关注任何人", Toast.LENGTH_SHORT).show();
                return;
            }
            int index = 0;
            Follow concernUser = null;
            for (User user1 : sConcernList) {
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
                        imageView.setImageResource(R.drawable.btn_concern_gray);
                        sConcernList.remove(finalIndex);
                        ListDataSaveUtil.setUserList(context.getString(R.string.CONCERN_USER), sConcernList);
                    } else {
                        Toast.makeText(context, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static void removeConcern(final Context context, final int index) {
        synchronized (UserState.class) {
            Follow concernUser = new Follow();
            concernUser.setObjectId(sConcernList.get(index).getObjectId());
            concernUser.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        sConcernList.remove(index);
                        ListDataSaveUtil.setUserList(context.getString(R.string.CONCERN_USER), sConcernList);
                    } else {
                        Toast.makeText(context, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}