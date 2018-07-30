package com.gin.xjh.shin_music.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gin.xjh.shin_music.R;
import com.gin.xjh.shin_music.bean.Question;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class AddQuestionActivity extends BaseActivity implements View.OnClickListener {

    private ImageView go_back;

    private RadioGroup Question_Classify;
    private EditText Question_Content, Question_User;

    private Button submit;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_question_activity);
        initView();
        initEvent();
    }

    private void initView() {
        go_back = findViewById(R.id.go_back);
        Question_Classify = findViewById(R.id.question_rg);
        Question_Content = findViewById(R.id.question);
        Question_User = findViewById(R.id.user);
        submit = findViewById(R.id.submit);
    }

    private void initEvent() {
        go_back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                finish();
                break;
            case R.id.submit:
                Submit_Question();
                break;
        }
    }

    private void Submit_Question() {
        String question = Question_Content.getText().toString();
        String question_user = Question_User.getText().toString();
        String category = "";

        if (question != "" && question.length() > 0) {
            switch (Question_Classify.getCheckedRadioButtonId()) {
                case R.id.suggest_rb:
                    category = "产品建议";
                    break;
                case R.id.bug_rb:
                    category = "程序漏洞";
                    break;
            }
            Question youQuestion = new Question(category, question_user, question);
            youQuestion.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Question_Content.setText("");
                        Question_User.setText("");
                        Toast.makeText(AddQuestionActivity.this, "提交成功，稍后会进行解决", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        } else {
            Toast.makeText(this, "您未填写意见，请勿点击按钮", Toast.LENGTH_SHORT).show();
        }
    }
}
