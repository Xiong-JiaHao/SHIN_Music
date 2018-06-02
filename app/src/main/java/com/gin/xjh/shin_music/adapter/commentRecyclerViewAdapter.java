package com.gin.xjh.shin_music.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Comment;
import com.gin.xjh.shin_music.bean.User;
import com.gin.xjh.shin_music.util.TimesUtil;

import java.text.ParseException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Gin on 2018/4/24.
 */

public class commentRecyclerViewAdapter extends RecyclerView.Adapter<commentRecyclerViewAdapter.commentViewHolder> {
    public List<Comment> list;
    private Context context;

    public commentRecyclerViewAdapter(Context context, List<Comment> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public commentRecyclerViewAdapter.commentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new commentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false));
    }

    //绑定视图
    @Override
    public void onBindViewHolder(commentRecyclerViewAdapter.commentViewHolder holder, int position) {
        holder.load(list.get(position), context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class commentViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemComment, itemTimes;

        public commentViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.comment_name);
            itemComment = itemView.findViewById(R.id.comment_content);
            itemTimes = itemView.findViewById(R.id.comment_time);
        }

        public void load(final Comment comment, final Context context) {
            try {
                itemName.setText(comment.getUserName());
                itemComment.setText(comment.getMyComment());
                itemTimes.setText(TimesUtil.longToString(comment.getTimes(), "yyyy-MM-dd HH:mm:ss"));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = LayoutInflater.from(context);
                        View viewDialog = inflater.inflate(R.layout.comment_details, null);

                        final TextView User_name = viewDialog.findViewById(R.id.User_name);
                        final ImageView User_sex = viewDialog.findViewById(R.id.User_sex);
                        final TextView User_QQ = viewDialog.findViewById(R.id.User_QQ);
                        final TextView User_sign = viewDialog.findViewById(R.id.User_sign);
                        final TextView comment_content = viewDialog.findViewById(R.id.comment_content);
                        final String Userid = comment.getUserId();
                        User_name.setText(comment.getUserName());
                        comment_content.setText(comment.getMyComment());
                        BmobQuery<User> query = new BmobQuery<>();
                        query.addWhereEqualTo("UserId", Userid);
                        query.findObjects(new FindListener<User>() {
                            @Override
                            public void done(List<User> list, BmobException e) {
                                if (e == null) {
                                    User user = list.get(0);
                                    switch (user.getUserSex()) {
                                        case 0:
                                            User_sex.setImageResource(R.drawable.man);
                                            break;
                                        case 1:
                                            User_sex.setImageResource(R.drawable.woman);
                                            break;
                                        case 2:
                                            User_sex.setImageResource(R.drawable.alien);
                                            break;
                                    }
                                    User_QQ.setText("QQ:" + user.getUserQQ());
                                    User_sign.setText(user.getPersonal_profile());
                                }
                            }
                        });

                        builder.setView(viewDialog);
                        builder.create();
                        builder.show();
                    }
                });
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
