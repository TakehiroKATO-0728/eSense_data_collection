package jp.aoyama.a5815025.esense_data_collection;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by takehiro on 2018/10/09.
 * 加藤岳大制作，csvファイルをローカルに出力するクラス
 */

public class OutputCsv {
    private static final String TAG = "CsvWrite";

    FileOutputStream fos;
    OutputStreamWriter osw;
    BufferedWriter bw;
    int i;
    //Calendar cal = Calendar.getInstance();


    String fileName = "/"+getCurrentDateString()+".csv";
    //String fileName = "/"+new Date().getTime()+".csv";

    public OutputCsv(){
        try {
            fos = new FileOutputStream(Environment.getExternalStorageDirectory() +fileName,false);

            osw = new OutputStreamWriter(fos, "UTF-8");
            bw = new BufferedWriter(osw);
            //bw = new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory() +"/ccc.csv")));
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "error_file");
        }
    }

    //==ファイルに書き込み==//
    public void write(String string){

        try {
            bw.write(string);
            bw.newLine();
            //Log.d(TAG, "file_close");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"error_write");
        }


    }

    //==ファイルを閉じて保存==//
    public void close(){

        try {
            bw.flush();
            bw.close();
            Log.d(TAG, "file_close");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "error_close");
        }
    }

    //==日時を取得==//
    public String getCurrentDateString() {
        //==== 現在時刻を取得 ====//
        Date date = new Date();

        //==== 表示形式を設定 ====//
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd_HH_mm_ss");

        return sdf.format(date);
    }
}