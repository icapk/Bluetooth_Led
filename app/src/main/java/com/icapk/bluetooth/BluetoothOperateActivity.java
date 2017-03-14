package com.icapk.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.icapk.bluetooth.Utils.FontUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothOperateActivity extends AppCompatActivity  {

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
                    Input_Word = Word_Input.getText().toString();
                    FontUtils fontUtils = new FontUtils(mContext);
//                    Word_Input.setText(fontUtils.getWordsInfo(Input_Word).toString());

                    System.out.println(fontUtils.getWordsInfo(Input_Word));
                    System.out.println(fontUtils.getWordsInfo(Input_Word).toString());
                    Snackbar.make(getCurrentFocus(),"双击事件",Snackbar.LENGTH_INDEFINITE).show();
                    break;
                case 2:

//                    fontUtils = new FontUtils().getWordsInfo(Word_Input.getText().toString());
//                    System.out.println(fontUtils.toString());
                    Snackbar.make(getCurrentFocus(),"单击事件",Snackbar.LENGTH_SHORT).show();
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
            hand.sendEmptyMessageDelayed(2, 310);
        }
    }

    @Bind(R.id.cooldinatorLayout_operate)
    CoordinatorLayout CooldinatorLayout_Operate;

    @Bind(R.id.word_input)
    EditText Word_Input;

    private long firstPressTime = 0;

    private long mNow = 0;
    private OutputStream mOutputStream;
    private BluetoothDevice device;
    private String Input_Word ;

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

        //设置CooldinatorLayout，使其获得焦点，释放EditText的焦点
        CooldinatorLayout_Operate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CooldinatorLayout_Operate.setFocusable(true);
                CooldinatorLayout_Operate.setFocusableInTouchMode(true);
                CooldinatorLayout_Operate.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(CooldinatorLayout_Operate.getWindowToken(), 0);
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
