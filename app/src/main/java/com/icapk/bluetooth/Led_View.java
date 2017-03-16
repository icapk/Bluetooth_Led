package com.icapk.bluetooth;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.icapk.bluetooth.Utils.FontUtils;

/**
 * Created by songjian on 2017/3/14.
 */

class Led_View extends TextView{

    private static final String TAG = "Led_View";

    private FontUtils utils;
    /**
     * 一个字节用16*16的点阵表示
     */
    private int dots = 16;
    /**
     * 点阵之间的距离
     */
    private float spacing = 10;
    /**
     * 点阵中点的半径
     */
    private float radius;
    private Paint normalPaint;
    private Paint selectPaint;
    /**
     * 汉字对应的点阵矩阵
     */
    private boolean [][] matrix;
    /**
     * 是否开始滚动
     */
    private boolean scroll = true;
    /**
     * 默认颜色蓝色
     */
    private int paintColor = Color.BLUE;

    private Thread thread;
    /**
     * 滚动的text
     */
    private volatile boolean scrollText = true;
    /**
     * 用来调整滚动速度
     */
    private int sleepTime = 100;
    /**
     * 滚动方向，默认0向左
     */
    private int scrollDirection = 0;
    /**
     * 需要显示的汉字
     */
     String text = "测试";

    public void sendWord(String str ){
        this.text = str;
        System.out.print(str+"nothing");
        Log.i(TAG, "sendword" + str);
        matrix = utils.getWordsInfo(text);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Led_View(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);



        selectPaint = new Paint();
        selectPaint.setStyle(Paint.Style.FILL);
        selectPaint.setColor(paintColor);
        normalPaint = new Paint();
        normalPaint.setStyle(Paint.Style.STROKE);
        normalPaint.setColor(paintColor);

        utils = new FontUtils(context);
        if (text != null){
            matrix = utils.getWordsInfo(text);
            Log.i(TAG, text);
        }
        if (scroll)
        {
            thread = new ScrollThread();
            thread.start();
        }
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




    /*
     * 用于控制滚动，仅在开启滚动的时候启动
     */
    private class ScrollThread extends Thread
    {
        public void run()
        {
            while(scrollText)
            {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (0 == scrollDirection)
                {
                    matrixLeftMove(matrix);
                }else {
                    matrixRightMove(matrix);
                }
                postInvalidate();//刷新View
            }
        }
    }

    /*
    *向左滚动时调用 列循环左移
    */
    private void matrixLeftMove( boolean [][] matrix)
    {
        for (int i = 0 ; i < matrix.length ; i++ )
        {
            boolean tmp = matrix[i][0];
            System.arraycopy(matrix[i], 1, matrix[i], 0, matrix[0].length - 1);
            matrix[i][matrix[0].length - 1] = tmp;
        }
    }
    /*
    * 向右滚动时调用 列循环右移
    * */
    private void matrixRightMove(boolean [][] matrix)
    {
        for (int i = 0 ; i < matrix.length ; i++ )
        {
            boolean tmp = matrix[i][matrix[0].length - 1];
            System.arraycopy(matrix[i], 0, matrix[i], 1, matrix[0].length - 1);
            matrix[i][0] = tmp;
        }
    }

    /*
    * 主要是想处理AT_MOST的情况，由于View继承自TextView ，而TextView
    * 重写了onMeasure ，因此这里参考View#onMeasure函数的写法即可
    * */
    protected  void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec));
    }

    private void drawText(Canvas canvas)
    {
        radius = (getHeight() - (dots + 1) * spacing) / (2 * dots);
        //行
        int row = 0;
        //列
        int column = 0;
        while (getYPosition(row) < getHeight()) {
            while (getXPosition(column) < getWidth()) {
                //just Draw
                if (row < matrix.length && column < matrix[0].length && matrix[row][column]) {
                    canvas.drawCircle(getXPosition(column), getYPosition(row), radius, selectPaint);
                } else {
                    canvas.drawCircle(getXPosition(column), getYPosition(row), radius, normalPaint);
                }
                column++;
            }
            row++;
            column = 0;
        }
    }

    /*
    * 获取绘制第column列的点的X坐标
    * @return
    * */
    private float getXPosition (int column)
    {
        return spacing + radius + (spacing + 2 * radius) * column;
    }

    /*
    * 获取绘制第row行的点的Y坐标
    * @return
    * */
    private float getYPosition (int row)
    {
        return spacing + radius + (spacing + 2 * radius) * row;
    }

    /*
    * 停止滚动
    * */
    private void stopScroll()
    {
        scrollText = false;
    }

    protected void onDetachedFromWindow()
    {
        stopScroll();
        super.onDetachedFromWindow();
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawText(canvas);
    }

}

