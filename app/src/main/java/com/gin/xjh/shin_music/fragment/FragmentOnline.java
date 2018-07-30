package com.gin.xjh.shin_music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.netrequest.GetNetNewMusic;
import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.activity.AlbumDetailsActivity;
import com.gin.xjh.shin_music.activity.MusicDetailsActivity;
import com.gin.xjh.shin_music.util.NetStateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gin on 2018/4/23.
 */

public class FragmentOnline extends Fragment {

    private GridView gridView;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter adapter;

    private EditText mFind = null;
    private ImageView mCheck;

    private int[] bitmapids = new int[]{R.drawable.icon_top, R.drawable.icon_hitfmtop, R.drawable.icon_billboard, R.drawable.btn_classical, R.drawable.icon_hiphop, R.drawable.icon_electricsong};
    private String[] bitmapname = new String[]{"中国Top榜", "HitFMTop榜", "Billboard榜", "金曲榜", "嘻哈榜", "电音榜"};
    private int[] ids = new int[]{15, 9, 6, 17, 18, 4};
    private String[] bitmapUrl = new String[]{"http://p1.music.126.net/d8faOLHxwWPta02fskrdDQ==/2057186255569164.jpg",
                                                "http://p1.music.126.net/ZRvvfxWy6l12Kzth56Jzaw==/2034096511385987.jpg",
                                                "http://p1.music.126.net/BmeUMF4Cr0f343lCwj1_7Q==/2105564767199852.jpg",
                                                "http://p1.music.126.net/N2whh2Prf0l8QHmCpShrcQ==/19140298416347251.jpg",
                                                "http://p1.music.126.net/tgLdvFIFcKAx24QCrYdYPw==/19071029184053433.jpg",
                                                "http://chuantu.biz/t6/313/1526562910x-1404793351.png"};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_music_fragment, null);
        initView(view);
        initData();
        initEvent(view);
        return view;
    }

    private void initView(View view) {
        gridView = view.findViewById(R.id.Online_music_gv);
        mFind = view.findViewById(R.id.find_online_name);
        mCheck = view.findViewById(R.id.find_Onlinemusic);
    }

    private void initData() {
        dataList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("img", bitmapids[i]);
            map.put("text", bitmapname[i]);
            dataList.add(map);
        }

    }

    private void initEvent(View view) {

        //GridView
        String[] from = {"img", "text"};
        int[] to = {R.id.music_img, R.id.music_text};
        adapter = new SimpleAdapter(getContext(), dataList, R.layout.online_music_item, from, to);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (NetStateUtil.getNetWorkState(getContext()) == NetStateUtil.NO_STATE) {
                    Toast.makeText(getContext(), "当前无网络...", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getContext(), AlbumDetailsActivity.class);
                intent.putExtra("isAlbum", false);
                intent.putExtra("name", bitmapname[arg2]);
                intent.putExtra("id", ids[arg2]);
                intent.putExtra("url",bitmapUrl[arg2]);
                startActivity(intent);
            }
        });

        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                find();
            }
        });

        //RecyclerView
        if (NetStateUtil.getNetWorkState(getContext()) == NetStateUtil.NO_STATE) {
            Toast.makeText(getContext(), "当前无网络...", Toast.LENGTH_SHORT).show();
            TextView hint = view.findViewById(R.id.new_Song_hint);
            hint.setText("当前无网络,请联网后重开APP");
            return;
        }
        new GetNetNewMusic().getJson(0, view.findViewById(R.id.fragment_recommend_music_list), view.findViewById(R.id.new_Song_hint), getContext());

    }

    /**
     * 查找数据
     */
    private void find() {
        String name = mFind.getText().toString();
        if (name.compareTo("") == 0 || name.length() == 0) {
            Toast.makeText(getContext(), "请输入搜索名称再点击按钮", Toast.LENGTH_SHORT).show();
            return;
        }
        mFind.setText("");
        Intent intent = new Intent(getContext(), MusicDetailsActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("isOnline", true);
        startActivity(intent);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (mFind != null) {
            mFind.setText("");
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
