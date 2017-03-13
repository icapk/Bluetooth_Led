package com.icapk.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.icapk.bluetooth.utils.snackbar_utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import static android.support.design.widget.Snackbar.make;


public class MainActivity extends Activity implements AdapterView.OnItemClickListener {

    private int DISCOVERABLE_TIME = 300;


    private FloatingActionButton btn;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE = 1;
    private Toolbar toolbar;

    private String MyName;
    private String MyAddress;
    private FloatingActionButton Fab;

    private DrawerLayout drawer_layout;
    private NavigationView NavigationView;
    private View headerView;

    private ListView mLv;
    private DeviceAdapter mAdapter;
    private TextView TV_Title;
    private OutputStream mOutputStream;
    private TextView TV_Address;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isFlresh = false;
    private long firstPressTime = 0;
    private long mNow = 0;
    private View coordinatorLayout;
    private  GestureDetector gestureDetector;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获得设备本身的蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        drawer_layout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView = (NavigationView)findViewById(R.id.navigation_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefreshlayout);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        btn = (FloatingActionButton)findViewById(R.id.fab_search);

        mLv = (ListView)findViewById(R.id.lv);
        mAdapter = new DeviceAdapter(getApplicationContext(),mDevices);
        mLv.setAdapter(mAdapter);
        mLv.setOnItemClickListener(this);
        TV_Address = (TextView)findViewById(R.id.tv_address);

//        discoverable();
        init();
        initBluetooth();
        initdevice();

        initDrawerLayout();
    }

    //当点击menu按键的时候执行这个方法
    public boolean onMenuOpened(int featureId,Menu menu){

        make(getCurrentFocus(),"退出",Snackbar.LENGTH_SHORT)
                .show();
//        setContentView(R.layout.layout_item);


        return false;
    }







    /**
     * 初始化广播，搜索按钮及Toolbar的title
     */
    private void init() {

        // 注册广播接收者, 当扫描到蓝牙设备的时候, 系统会发送广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBluetoothReceiver, filter);


        /**
         * 下拉刷新
         */
        //设置下拉圆圈里进度条的颜色
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE,Color.BLACK,Color.GREEN,Color.RED);
        //设置下拉圆圈的大小
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        //设置手指下拉多少距离触发刷新
//        mSwipeRefreshLayout.setDistanceToTriggerSync(300);
        //设置下拉刷新的监听事件
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isFlresh){
                    isFlresh = true;

                    mDevices.clear();
                    mBluetoothAdapter.startDiscovery();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(false);
                            Snackbar sk = Snackbar.make(getCurrentFocus(),"刷新完成", BaseTransientBottomBar.LENGTH_SHORT);
                            snackbar_utils.setSnackbarColor(sk,Color.BLUE,Color.WHITE);
                            sk.show();
                            isFlresh = false;
                        }
                    },12000);
                }
            }
        });


        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        Snackbar sk = Snackbar.make(findViewById(R.id.toolbar),"双击事件",Snackbar.LENGTH_SHORT);
                        snackbar_utils.setSnackbarColor(sk,Color.BLUE,Color.WHITE);
                        sk.show();
                        break;
                    case 2:
                        Snackbar _sk = Snackbar.make(getCurrentFocus(),"单击事件",Snackbar.LENGTH_SHORT);
                        snackbar_utils.setSnackbarColor(_sk,Color.BLUE,Color.WHITE);
                        _sk.show();
                        break;
                }
            }
        };


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mDevices.clear();

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
//                    mBluetoothAdapter.startDiscovery();
//                if (btn.getText().toString() == "正在搜索..."){
//                    mBluetoothAdapter.cancelDiscovery();
//
//                    btn.setText("搜索");
//                }
//                    btn.setClickable(false);

                }
            }
        });


        Toast.makeText(getApplicationContext(),MyName,Toast.LENGTH_SHORT).show();
//        toolbar.setTitleTextColor(0x000000);
        toolbar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(),"重命名"+mBluetoothAdapter.getName(),Toast.LENGTH_SHORT).show();
//                mBluetoothAdapter.setName("M2");

                return false;
            }
        });
        MyName = mBluetoothAdapter.getName();
        MyAddress = mBluetoothAdapter.getAddress();
        toolbar.setTitle(MyName);
    }


    /**
     * 初始化本地蓝牙设备
     */
    private void initBluetooth() {

        //获得设备本身的蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //判断设备是否存在蓝牙
        if (mBluetoothAdapter == null ){
            Toast.makeText(this,"此设备没有蓝牙功能",Toast.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled()){ //蓝牙未开启
            //弹出对话框提示用户是否打开蓝牙
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler,REQUEST_ENABLE);
        }
    }

    /**
     *
     * 初始化已配对列表
     */
    private void initdevice() {


        //得到已经配对的蓝牙设备
        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        if (devices.size() > 0)
        {
            for (Iterator iterator = devices.iterator(); iterator.hasNext();)
            {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
                //得到蓝牙设备的地址与名称
                String name = bluetoothDevice.getName();
                String mac = bluetoothDevice.getBluetoothClass().toString();
//                Toast.makeText(getApplicationContext(),name+mac,Toast.LENGTH_SHORT).show();
                System.out.println("已配对远程设备名称   "+name+"   "+mac);
//                mLv.setAdapter();
                mDevices.add(bluetoothDevice);
                mAdapter.notifyDataSetChanged();

            }
        }

    }

    /**
     * 初始化扫描到的蓝牙列表
     */
    private ArrayList<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();
    //注册广播监听蓝牙，监听有无可用蓝牙设备
    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //判断收的的广播是否是搜索到蓝牙设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //从Intent里获取搜索到的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDevices.add(device);
                mAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                btn.setClickable(true);
            }
        }
    };

    /**
     * 初始化DrawerLayout
     */

    private void initDrawerLayout() {

        headerView = NavigationView.getHeaderView(0);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make(getCurrentFocus(), "点击了 HeadView", Snackbar.LENGTH_SHORT).show();
            }
        });

        //menu的点击事件处理
        NavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.nv_friends:
                        make(getCurrentFocus(), "点击了 朋友", Snackbar.LENGTH_SHORT)
                                .show();
                        break;
                    case R.id.nv_location:
                        make(getCurrentFocus(), "点击了 本地", Snackbar.LENGTH_SHORT).show();
                        break;
                    case R.id.nv_update:
                        make(getCurrentFocus(), "点击了 更新", Snackbar.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        toolbar.inflateMenu(R.menu.menu_toolbar);

        ActionBarDrawerToggle drawertoggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.open, R.string.close);
        drawertoggle.syncState();
        drawer_layout.addDrawerListener(drawertoggle);

    }


    /**
     * listview的点击事件
     * 点击连接到蓝牙
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = mDevices.get(position);
//        conn(device);
        Bundle b = new Bundle();
        b.putParcelable(BluetoothDevice.EXTRA_DEVICE,device);
        Intent intent = new Intent(getApplicationContext(), BluetoothOperateActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    /**
     * 蓝牙连接实现
     * 作为客户端连接蓝牙模块
     * @param device
     */
    private void conn(final BluetoothDevice device) {
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
                            Toast.makeText(getApplicationContext(),"连接成功。。。",
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

    /**
     * 软件开启启动可检测性
     */
    private void discoverable(){
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_TIME);
        startActivity(discoverableIntent);
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothReceiver);
    }

    /**
     * 双击返回键退出应用
     */
    public void onBackPressed(){
        long now = System.currentTimeMillis();
        if ((now - firstPressTime) > 2000){
            Snackbar snackbar = Snackbar.make(getCurrentFocus(),"再按一次退出",Toast.LENGTH_SHORT);
                    snackbar_utils.setSnackbarColor(snackbar,Color.BLUE,Color.WHITE);
                    snackbar.show();
            firstPressTime = now;
        }else {
            finish();
            System.exit(0);
        }
    }


}
