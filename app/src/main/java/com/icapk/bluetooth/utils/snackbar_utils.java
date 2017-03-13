package com.icapk.bluetooth.utils;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.icapk.bluetooth.R;

/**
 * Created by songjian on 2017/3/12.
 */

public class snackbar_utils {
    /**
     * 设置Snackbar的颜色
     * @param snackbar
     * @param messageColor
     * @param backgroundColor
     */
    public static void setSnackbarColor(Snackbar snackbar , int messageColor , int backgroundColor){
        View view = snackbar.getView();
        if (view != null){
            view.setBackgroundColor(backgroundColor);
            ((TextView)view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);
        }
    }
}
