package com.icapk.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LedPreviewActivity extends AppCompatActivity {

    private static final String TAG = "LedPreviewActivity";

    private Context context = LedPreviewActivity.this;
    private String input_word;


    @Bind(R.id.led_view)
    Led_View Led_view;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_preview);
        ButterKnife.bind(this);

        Intent i = getIntent();
        input_word = i.getStringExtra("Input_Word");

        Led_view.sendWord(input_word);

        Led_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context,"点击事件",Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * 设置自动隐藏虚拟按键和状态栏
         */
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
