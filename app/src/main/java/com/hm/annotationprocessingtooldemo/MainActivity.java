package com.hm.annotationprocessingtooldemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.apt_annotation.BindView;
import com.hm.apt_annotation.OnClick;
import com.hm.apt_library.BindViewTools;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_bind_view)
    TextView tvBindView;
    @BindView(R.id.btn_bind_view)
    Button btnBindView;
    @BindView(R.id.iv_bind_view)
    ImageView ivBindView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindViewTools.bind(this);
        tvBindView.setText("bind view");
        btnBindView.setText("ok");
    }

    @OnClick({R.id.tv_bind_view, R.id.btn_bind_view})
    public void clickTvBindView(View v) {
        switch (v.getId()) {
            case R.id.tv_bind_view:
                Toast.makeText(this, "clickTvBindView", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_bind_view:
                Toast.makeText(this, "clickBtnBindView", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.iv_bind_view)
    public void clickIvBindView(View v) {
        Toast.makeText(this, "clickIvBindView", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.tv_not_use_bind_view)
    public void clickTvNotUseBindView(View v) {
        Toast.makeText(this, "clickTvNotUseBindView", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_not_use_bind_view)
    public void clickBtnNotUseBindView(View v) {
        SecondActivity.launch(this);
    }
}
