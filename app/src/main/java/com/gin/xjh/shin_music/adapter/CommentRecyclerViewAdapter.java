package com.gin.xjh.shin_music.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Comment;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.activity.PersonalMenuActivity;
import com.gin.xjh.shin_music.util.NetStateUtil;
import com.gin.xjh.shin_music.util.TimesUtil;

import java.text.ParseException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Gin on 2018/4/24.
 */

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.commentViewHolder> {
    public List<Comment> mList;
    private Context mContext;

    public CommentRecyclerViewAdapter(Context context, List<Comment> list) {
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public CommentRecyclerViewAdapter.commentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new commentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false));
    }

    //绑定视图
    @Override
    public void onBindViewHolder(CommentRecyclerViewAdapter.commentViewHolder holder, int position) {
        holder.init(mList.get(position), mContext);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class commentViewHolder extends RecyclerView.ViewHolder {
        private TextView mItemName, mItemComment, mItemTimes;

        public commentViewHolder(View itemView) {
            super(itemView);
            mItemName = itemView.findViewById(R.id.comment_name);
            mItemComment = itemView.findViewById(R.id.comment_content);
            mItemTimes = itemView.findViewById(R.id.comment_time);
        }

        public void init(final Comment comment, final Context context) {
            try {
                final AlertDialog[] dia = new AlertDialog[1];
                mItemName.setText(comment.getUserName());
                mItemComment.setText(comment.getMyComment());
                mItemTimes.setText(TimesUtil.longToString(comment.getTimes(), "yyyy-MM-dd HH:mm:ss"));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (NetStateUtil.getNetWorkState(mContext) == NetStateUtil.NO_STATE) {
                            Toast.makeText(mContext, "当前无网络...", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final User[] user = new User[1];
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = LayoutInflater.from(context);
                        View viewDialog = inflater.inflate(R.layout.dialog_comment_details, null);

                        final TextView User_name = viewDialog.findViewById(R.id.User_name);
                        final ImageView User_sex = viewDialog.findViewById(R.id.User_sex);
                        final TextView User_QQ = viewDialog.findViewById(R.id.User_QQ);
                        final TextView User_sign = viewDialog.findViewById(R.id.User_sign);
                        final TextView comment_content = viewDialog.findViewById(R.id.comment_content);
                        TextView likesong = viewDialog.findViewById(R.id.likesong);
                        final String Userid = comment.getUserId();
                        User_name.setText(comment.getUserName());
                        comment_content.setText(comment.getMyComment());
                        BmobQuery<User> query = new BmobQuery<>();
                        query.addWhereEqualTo(context.getString(R.string.USERID), Userid);
                        query.findObjects(new FindListener<User>() {
                            @Override
                            public void done(List<User> list, BmobException e) {
                                if (e == null) {
                                    user[0] = list.get(0);
                                    switch (user[0].getUserSex()) {
                                        case 0:
                                            User_sex.setImageResource(R.drawable.sel_sex_man);
                                            break;
                                        case 1:
                                            User_sex.setImageResource(R.drawable.sel_sex_woman);
                                            break;
                                        case 2:
                                            User_sex.setImageResource(R.drawable.sel_sex_alien);
                                            break;
                                    }
                                    User_QQ.setText("QQ:" + user[0].getUserQQ());
                                    User_sign.setText(user[0].getPersonal_profile());
                                }
                            }
                        });
                        likesong.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, PersonalMenuActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(context.getString(R.string.USER), user[0]);
                                intent.putExtra(context.getString(R.string.USER), bundle);
                                context.startActivity(intent);
                                dia[0].dismiss();
                            }
                        });

                        builder.setView(viewDialog);
                        builder.create();
                        dia[0] = builder.show();
                    }
                });
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void addData(Comment comment) {
//      在list中添加数据，并通知条目加入一条
        mList.add(0, comment);
        //添加动画
        notifyItemInserted(0);
    }
}
