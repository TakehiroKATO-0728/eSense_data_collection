package jp.aoyama.a5815025.esense_data_collection;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WaveFile {
    private static final String TAG = "CreateWav2";
    private final int FILESIZE_SEEK = 4;
    private final int DATASIZE_SEEK = 40;

    private RandomAccessFile raf;
    private final int SAMPLING_RATE = 8000;
    private File recFile;
    private String fileName = "test.wav";
    private byte[] RIFF = {'R', 'I', 'F', 'F'};//wavファイル　リフチャンク　先頭８バイトがチャンクヘッダ
    private int fileSize = 36;
    private byte[] WAVE = {'W', 'A', 'V', 'E'};//wav形式でRTFFフォーマとを使用
    private byte[] fmt = {'f', 'm', 't', ' '};//fmtチャンク　利府チャンクに書き込むチャンクID用
    private int fmtSize = 16;//fmtのバイト数
    private byte[] fmtID = {1, 0}; // フォーマットID　2byte
    private short chCount = 1;//チャンネルカウント　モノラル１　ステレオ２
    private int bytePerSec = SAMPLING_RATE * (fmtSize / 8)*chCount;//データ速度
    private short blockSize = (short) ((fmtSize / 8) * chCount);//ブロックサイズ
    private short bitPerSample = 16;//サンプルあたりのビット数　wavでは8bitか16bit
    private byte[] data = {'d', 'a', 't', 'a'};//dataチャンク
    private int dataSize = 0;//波形データのバイト数
    //short型は16ビットで-32768～32767

    public void createFile(String fName) throws FileNotFoundException {
        Log.d(TAG, "ファイルパス : " + fName);
        fileName = fName;
        String ex_path = "";//Environment.getExternalStorageDirectory().getAbsolutePath();
        Uri newUri = Uri.parse(ex_path.concat(fName));//cr.insert(base, values);
        // ファイルを作成
        recFile = new File(newUri.getPath());//new File(fileName);
        if (recFile.exists()) {
            recFile.delete();
        }
        if(recFile.canRead()){
            Log.d(TAG, "can read " );
        }else {
            Log.d(TAG, "cannot read  " );
        }
        if(recFile.canWrite()) {//can read and write free to do what is needed
            Log.d(TAG, "can write " );
        }else {
            Log.d(TAG, "cannot write " );
        }
        try {
            recFile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "createNewFile: " + fName + "\n"+e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            raf = new RandomAccessFile(recFile, "rw");
        } catch (FileNotFoundException e) {
            Log.e(TAG, "RandomAccessFile: " + fName + "\n"+e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // wavのヘッダを書き込み
        try {
            raf.seek(0);
            raf.write(RIFF);
            raf.write(littleEndianInteger(fileSize));
            raf.write(WAVE);
            raf.write(fmt);
            raf.write(littleEndianInteger(fmtSize));
            raf.write(fmtID);
            raf.write(littleEndianShort(chCount));
            raf.write(littleEndianInteger(SAMPLING_RATE));
            raf.write(littleEndianInteger(bytePerSec));
            raf.write(littleEndianShort(blockSize));
            raf.write(littleEndianShort(bitPerSample));
            raf.write(data);
            raf.write(littleEndianInteger(dataSize));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // int型をリトルエンディアンのbyte配列に変更
    private byte[] littleEndianInteger(int i) {
        byte[] buffer = new byte[4];
        buffer[0] = (byte) i;
        buffer[1] = (byte) (i >> 8);
        buffer[2] = (byte) (i >> 16);
        buffer[3] = (byte) (i >> 24);
        return buffer;
    }

    // PCMデータを追記するメソッド
    public void addBigEndianData(short[] shortData){
        Log.d(TAG, "addBigEndianData: " + shortData[0]);

        // ファイルにデータを追記
        try {
            raf.seek(raf.length());
            raf.write(littleEndianShorts(shortData));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // ファイルサイズを更新
        updateFileSize();

        // データサイズを更新
        updateDataSize();

    }

    // ファイルサイズを更新
    private void updateFileSize(){
        fileSize = (int) (recFile.length() - 8);
        byte[] fileSizeBytes = littleEndianInteger(fileSize);
        try {
            raf.seek(FILESIZE_SEEK);
            raf.write(fileSizeBytes);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // データサイズを更新
    private void updateDataSize(){
        dataSize = (int) (recFile.length() - 44);
        byte[] dataSizeBytes = littleEndianInteger(dataSize);
        try {
            raf.seek(DATASIZE_SEEK);
            raf.write(dataSizeBytes);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // short型変数をリトルエンディアンのbyte配列に変更
    private byte[] littleEndianShort(short s){

        byte[] buffer = new byte[2];

        buffer[0] = (byte) s;
        buffer[1] = (byte) (s >> 8);

        return buffer;
    }

    // short型配列をリトルエンディアンのbyte配列に変更
    private byte[] littleEndianShorts(short[] s){

        byte[] buffer = new byte[s.length * 2];
        int i;

        for(i = 0; i < s.length; i++){
            buffer[2 * i] = (byte) s[i];
            buffer[2 * i + 1] = (byte) (s[i] >> 8);
        }
        return buffer;
    }

    // ファイルを閉じる
    public void close(){
        try {
            raf.close();
            Log.d(TAG, "ファイルクローズ");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

