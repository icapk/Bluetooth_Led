package com.icapk.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.icapk.bluetooth.Utils.FontUtils;

/**
 * Created by songjian on 2017/3/14.
 */

class Led_View extends TextView{

    private final String text;
//    private   fontUtils;

    public Led_View(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        text = getText().toString();
        FontUtils fontUtils = new FontUtils(context);
        fontUtils.getWordsInfo(text);

    }
    public Led_View(Context context) {
        this(context,null,0);
    }

    public Led_View(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Led_View(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }


}
