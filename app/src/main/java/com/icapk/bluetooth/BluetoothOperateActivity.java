package com.icapk.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.icapk.bluetooth.Utils.snackbar_utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.icapk.bluetooth.R.id.linearlayout;


public class BluetoothOperateActivity extends AppCompatActivity  {

    private static final String TAG = "BluetoothOperate";

    private Context mContext = BluetoothOperateActivity.this;

    /**
     * 利用Handler处理FloatingActionButton的点击事件
     */
    final Handler hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Snackbar s = Snackbar.make(getCurrentFocus(),"双击事件",Snackbar.LENGTH_SHORT);
                    snackbar_utils.setSnackbarColor(s, Color.GRAY,Color.WHITE);
                    s.show();
                    break;
                case 2:
                    if (Word_Input.getText().toString().length() == 0){
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(),"输入不能为空！",Snackbar.LENGTH_SHORT);
                        snackbar_utils.setSnackbarColor(snackbar, Color.GRAY,Color.WHITE);
                        snackbar.show();
                    }else {
                        Intent i = new Intent(getApplicationContext(), LedPreviewActivity.class);
                        i.putExtra("Input_Word", Word_Input.getText().toString());
                        startActivity(i);
                        Log.i(TAG, "Input_Word");
                    }
                    break;
            }
        }
    };


    @Bind(R.id.opera_toolbar)
    Toolbar Opera_TB;

    @OnClick(R.id.FAB_btn) void FAB_btn(){
        firstPressTime=mNow;
        mNow= System.currentTimeMillis();
        if (mNow - firstPressTime < 300){//双击事件
            //先移除消息栈里面的单击消息
            hand.removeMessages(2);
            hand.sendEmptyMessage(1);
            mNow= 0;
        }else {
            //发送0.31s的延时message，便于区分单双击事件
            hand.sendEmptyMessageDelayed(2, 31);
        }
    }

    @Bind(linearlayout)
    LinearLayout Linearlayout;

    @Bind(R.id.word_input)
    EditText Word_Input;

    private long firstPressTime = 0;

    private long mNow = 0;
    private OutputStream mOutputStream;
    private BluetoothDevice device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_operate);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        device = bundle.getParcelable(BluetoothDevice.EXTRA_DEVICE);



        init();



    }

    private void init() {
        //初始化Toolbar及其Navigation
        Opera_TB.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        Opera_TB.setTitle(device.getName());

        Opera_TB.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Toolbar的menu点击事件
        Opera_TB.inflateMenu(R.menu.menu_toolbar);
        Opera_TB.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(getApplicationContext(), LedPreviewActivity.class);
                i.putExtra("Input_Word", Word_Input.getText().toString());
                startActivity(i);
                Snackbar snackbar = Snackbar.make(getCurrentFocus(),"预览",Snackbar.LENGTH_SHORT);
                snackbar_utils.setSnackbarColor(snackbar, Color.GRAY,Color.WHITE);
                snackbar.show();
                return false;
            }
        });

        //设置Linearlayout，使其获得焦点，释放EditText的焦点
        Linearlayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Linearlayout.setFocusable(true);
                Linearlayout.setFocusableInTouchMode(true);
                Linearlayout.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Linearlayout.getWindowToken(), 0);
                return false;
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        connect(device);
    }

    /**
     * 连接当前蓝牙设备
     */
    private void connect(final BluetoothDevice device) {
        //建立蓝牙连接属于耗时操作，类似TCP Socket，需要放在子线程
        new Thread(){
            public void run(){
                try {
                    //获取BluetoothSocket，UUID需要和蓝牙服务端保持一致
                    BluetoothSocket blurtoothsocket = device.createRfcommSocketToServiceRecord(UUID
                            .fromString("00001101-0000-1000-8000-00805f9b34fb"));
                    //和蓝牙服务端建立连接
                    blurtoothsocket.connect();
                    //获取输出流，往蓝牙服务端写入指令
                    mOutputStream = blurtoothsocket.getOutputStream();

                    //提示用户连接成功
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"连接成功",
                                    Toast.LENGTH_SHORT).show();
//                            TV_Address.setText("已连接");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
