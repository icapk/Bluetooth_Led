package com.icapk.bluetooth.Utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by songjian on 2017/3/14.
 */

public class FontUtils {

    private Context context;
    /*
   * 字库名
   * */
    private static String dotMatrixFont = "HZK16.dat";
    /*
    * 编码GB2312
    * */
    private final static String ENCODE = "GB2312";
    /*
    * 16*16的字库用16个点表示
    * */
    private int dots = 16;
    /*
    * 一个字用点表示需要多少字节，16*16的字体需要32字节
    * */
    private int wordByteByDots = 32;

    public  FontUtils(Context context){
        this.context = context;

    }

    /*
   * 获取字符串的点阵矩阵
   * */
    public boolean [][] getWordsInfo(String str)
    {
        byte[] dataBytes = null;
        try{
            //得到汉字对应的区码和位码
            dataBytes = str.getBytes(ENCODE);
//            System.out.println(str.length());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //汉字对应的byte数组
        int [] byteCode = new int [dataBytes.length];
        //当成无符号对待
        for (int i = 0 ; i < dataBytes.length; i++)
        {//根据每两个byte数组计算一个汉字的相对位置
            byteCode[i] = dataBytes[i] < 0 ? 256 + dataBytes[i] : dataBytes[i];
//            System.out.println(byteCode[i]);
        }
        //用来存放所汉字对应的字摸信息
        //一个汉字32 byte
        int wordNums = byteCode.length/2;
        boolean[][] matrix = new boolean[dots][wordNums * dots];//[16]*[汉字数*16]
        byte [] dataResult = new byte[wordNums * wordByteByDots];
        for (int i = 0 , numIndex = 0;i < byteCode.length ; i += 2 , numIndex++)
        {
//            System.out.println(byteCode.length);
            //依次读取到这个汉字对应的32位字摸信息
            byte[] data = read(byteCode[i],byteCode[i+1]);
//            System.out.println(data+"---");
            //复制单个汉字data数据到dataResult（源数组，源数据开始复制位置，目标数组，目标数组开始复制的位置，复制数据的长度）
            System.arraycopy(data,0,dataResult,numIndex * data.length,data.length);

        }

        for (int num = 0; num < wordNums;num++ ) {//单字循环
            for (int i = 0; i < dots; i++) {//行循环
                for (int j1 = 0; j1 < 2; j1++){
                    //对每个字进行解析
                    byte tmp = dataResult[num * wordByteByDots + i * 2 + j1];
                    System.out.print(tmp);

                    for (int j2 = 0; j2 < 8; j2++){
                        if (((tmp >> (7-j2)) & 1) == 1)
                        {
                            matrix [i][num * dots + j1 * 8 + j2] = true;
                            System.out.print("●");
                        }else {
                            matrix[i][num * dots + j1 * 8 +j2] = false;
                            System.out.print("○");
                        }

                    }
                }
                System.out.println(" ");
            }
        }
        return matrix;

    }

    /*
    * 从字库找到这个汉字的字摸信息
    * areaCode 区码  对应编码的第一个字节
    * posCode  位码  对应编码的第二个字节
    * */
    protected  byte[] read(int areaCode,int posCode)
    {
        byte[] data = null;
        try{//得到汉字在HZK16中的绝对偏移位置
            int area = areaCode - 0xa0;//区码 = 区号 - 0xa0
            int pos  = posCode - 0xa0;//位码 = 位号 - 0xa0
            InputStream in = context.getResources().getAssets().open(dotMatrixFont);
            long offset = wordByteByDots * ((area - 1) * 94 + pos -1);//offset=(94*(区码-1)+(位码-1))*32
            in.skip(offset);//忽略的字节数，返回值为实际忽略的字节数
            data = new byte[wordByteByDots];
            in.read(data,0,wordByteByDots);//从输入流中最多读取wordByteByDots个字节的数据，存放到偏移量为0的data数组中
            in.close();//关闭流
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
