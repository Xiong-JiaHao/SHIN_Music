package com.gin.xjh.shin_music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.user.UserState;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.util.DensityUtil;
import com.gin.xjh.shin_music.view.SlidingButtonView;

import java.util.List;

public class ConcernRecyclerViewAdapter extends RecyclerView.Adapter<ConcernRecyclerViewAdapter.MyViewHolder> implements SlidingButtonView.IonSlidingButtonListener {

    private Context mContext;

    private IonSlidingViewClickListener mIDeleteBtnClickListener;

    private List<User> mDatas;

    private SlidingButtonView mMenu = null;

    public ConcernRecyclerViewAdapter(Context context, List<User> datas) {
        mContext = context;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) context;
        mDatas = datas;

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.init(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.concern_item, arg0, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    public void removeData(int position, boolean isFlag) {
        if (isFlag) {
            UserState.removeConcern(mContext, position);
        }
        mDatas.remove(position);
        notifyItemRemoved(position);

    }

    /**
     * 删除菜单打开信息接收
     */
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    /**
     * 滑动或者点击了Item监听
     *
     * @param slidingButtonView
     */
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if (menuIsOpen()) {
            if (mMenu != slidingButtonView) {
                closeMenu();
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;
    }

    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if (mMenu != null) {
            return true;
        }
        return false;
    }

    public interface IonSlidingViewClickListener {
        void onItemClick(View view, int position);

        void onDeleteBtnCilck(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView removeConcern, mConcern_name, mConcern_QQ, mConcern_sign;
        public ImageView mConcern_sex;
        public ViewGroup mConcernUser;

        public MyViewHolder(View itemView) {
            super(itemView);
            removeConcern = itemView.findViewById(R.id.removeConcern);
            mConcern_name = itemView.findViewById(R.id.ConcernUser_Name);
            mConcern_QQ = itemView.findViewById(R.id.ConcernUser_QQ);
            mConcern_sex = itemView.findViewById(R.id.ConcernUser_sex);
            mConcern_sign = itemView.findViewById(R.id.ConcernUser_sign);
            mConcernUser = itemView.findViewById(R.id.ConcernUser);

            ((SlidingButtonView) itemView).setSlidingButtonListener(ConcernRecyclerViewAdapter.this);
        }

        public void init(int position) {
            //设置数据
            User user = mDatas.get(position);
            mConcern_name.setText(user.getUserName());
            mConcern_QQ.setText("QQ:" + user.getUserQQ());
            mConcern_sign.setText("个人简介：" + user.getPersonal_profile());
            switch (user.getUserSex()) {
                case 0:
                    mConcern_sex.setImageResource(R.drawable.sel_sex_man);
                    break;
                case 1:
                    mConcern_sex.setImageResource(R.drawable.sel_sex_woman);
                    break;
                case 2:
                    mConcern_sex.setImageResource(R.drawable.sel_sex_alien);
                    break;
            }


            //设置内容布局的宽为屏幕宽度减去margin的值，也就是显示控件的宽度
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            mConcernUser.getLayoutParams().width = outMetrics.widthPixels - DensityUtil.dp2px(mContext, 49);


            mConcernUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是否有删除菜单打开
                    if (menuIsOpen()) {
                        closeMenu();//关闭菜单
                    } else {
                        int n = getLayoutPosition();
                        mIDeleteBtnClickListener.onItemClick(v, n);
                    }

                }
            });
            removeConcern.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = getLayoutPosition();
                    mIDeleteBtnClickListener.onDeleteBtnCilck(v, n);
                }
            });
        }
    }
}
