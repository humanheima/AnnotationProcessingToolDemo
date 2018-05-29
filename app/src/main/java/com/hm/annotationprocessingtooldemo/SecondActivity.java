package com.hm.annotationprocessingtooldemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hm.apt_annotation.BindView;
import com.hm.apt_annotation.OnClick;
import com.hm.apt_library.BindViewTools;

public class SecondActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.tv_bind_view)
    TextView tvBindView;
    @BindView(R.id.btn_bind_view)
    Button btnBindView;
    ImageView ivBindView;

    public static void launch(Context context) {
        Intent intent = new Intent(context, SecondActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        BindViewTools.bind(this);
        tvBindView.setText("bindView");
        btnBindView.setText("second");
        ivBindView = findViewById(R.id.iv_bind_view);
        ivBindView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "onClick: ";
                Log.d(TAG, msg);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick({R.id.tv_bind_view, R.id.btn_bind_view})
    public void clickTvBindView(View v) {
        switch (v.getId()) {
            case R.id.tv_bind_view:
                Toast.makeText(this, "TvBindView", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_bind_view:
                Toast.makeText(this, "BtnBindView", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.tv_not_use_bind_view)
    public void clickTvNotUseBindView(View v) {
        Toast.makeText(this, "TvNotUseBindView", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_not_use_bind_view)
    public void clickBtnNotUseBindView(View v) {
        Toast.makeText(this, "BtnNotUseBindView", Toast.LENGTH_SHORT).show();
    }
}
