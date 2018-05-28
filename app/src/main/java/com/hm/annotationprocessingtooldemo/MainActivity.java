package com.hm.annotationprocessingtooldemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindViewTools.bind(this);
        tvBindView.setText("bind view");
        btnBindView.setText("ok");
    }

    @OnClick(R.id.tv_bind_view)
    public void clickTvBindView(View v) {
        Toast.makeText(this, "clickTvBindView", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_bind_view)
    public void clickBtnBindView(View v) {
        Toast.makeText(this, "clickBtnBindView", Toast.LENGTH_SHORT).show();

    }
}
