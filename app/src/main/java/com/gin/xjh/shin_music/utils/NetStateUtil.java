package com.gin.xjh.shin_music.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;


public class NetStateUtil {
    public static int WIFI_STATE = 1;
    public static int DATA_STATE = 2;
    public static int NO_STATE = 0;

    private static int state = NO_STATE;

    public static int getNetWorkState(Context context) {
        boolean wifi = false;
        boolean data = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo dataInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiInfo.isConnected()) {
                state = WIFI_STATE;
            } else if (dataInfo.isConnected()) {
                state = DATA_STATE;
            } else {
                state = NO_STATE;
            }
        } else {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有网络连接的信息
            Network[] networks = connMgr.getAllNetworks();
            //用于存放网络连接信息
            StringBuilder sb = new StringBuilder();
            //通过循环将网络信息逐个取出来
            for (int j = 0; j < networks.length; j++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[j]);
                Log.i("TAG", "onReceive: " + networkInfo.getTypeName());
                if (networkInfo.getTypeName().equals("WIFI")) {
                    wifi = true;
                } else if (networkInfo.getTypeName().equals("MOBILE")) {
                    data = true;
                }
            }
            if (wifi) {
                state = WIFI_STATE;
            } else if (data) {
                state = DATA_STATE;
            } else {
                state = NO_STATE;
            }
        }
        return state;
    }

}