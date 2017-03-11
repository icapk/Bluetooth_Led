package com.icapk.bluetooth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


public class BluetoothOperateActivity extends AppCompatActivity {

    private long firstPressTime = 0;
    private FloatingActionButton btn;
    private long mNow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_operate);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        Snackbar.make(getCurrentFocus(),"双击事件",Snackbar.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Snackbar.make(getCurrentFocus(),"单击事件",Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        btn = (FloatingActionButton)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"hasjdf",Toast.LENGTH_SHORT).show();
                firstPressTime=mNow;
                mNow= System.currentTimeMillis();
                if (mNow - firstPressTime < 300){//双击事件
                    //先移除消息栈里面的单击消息
                    handler.removeMessages(2);
                    handler.sendEmptyMessage(1);
                    mNow= 0;
                }else {
                    //发送0.31s的延时message，便于区分单双击事件
                    handler.sendEmptyMessageDelayed(2,310);


                }
            }
        });
    }

}
