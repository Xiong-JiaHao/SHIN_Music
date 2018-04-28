package com.gin.xjh.shin_music;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

public class album_details_Activity extends Activity{

    private ImageView album_img;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_details);
    }
}
