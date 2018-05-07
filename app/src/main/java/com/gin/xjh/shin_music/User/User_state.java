package com.gin.xjh.shin_music.User;

import com.gin.xjh.shin_music.bean.User;

public class User_state {

    private static volatile boolean Login_flag = false;
    private static volatile User user = null;

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

}