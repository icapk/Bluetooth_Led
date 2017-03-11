package com.icapk.bluetooth;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class BluetoothOperateActivity extends AppCompatActivity {

    private long firstPressTime = 0;
    private FloatingActionButton btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_operate);
        btn = (FloatingActionButton)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getCurrentFocus(),"haha",Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
    }

}
