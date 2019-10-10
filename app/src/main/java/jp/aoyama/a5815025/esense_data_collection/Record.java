package jp.aoyama.a5815025.esense_data_collection;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.util.Log;


import java.io.FileNotFoundException;

public class Record {

    private static final int SAMPLING_RATE = 8000;
    private static final String TAG = "Record";
    int bufferSize;
    private AudioRecord audioRecord;
    private boolean isRecording;
    public short[] shortBuf;
    public long[] STE;
    private int SteLength;

    private WaveFile wavFile;
    //private AnalyzeEating analyzeEating;
    //private FeatureExtraction shortTermEnergy;

    public Record(){
    }

    public void init(String fileName, AudioManager audioManager) {
        //バッファサイズを計算
        bufferSize = AudioRecord.getMinBufferSize(
                SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        Log.d(TAG,"buf");

        //インスタンス生成
        audioRecord = new AudioRecord(
                audioManager.STREAM_VOICE_CALL,
                SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);
        Log.d(TAG,"インスタンス");

        wavFile = new WaveFile();
        //analyzeEating = new AnalyzeEating();

        try {
            wavFile.createFile(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        isRecording = true;

        shortBuf = new short[bufferSize];

        //shortTermEnergy = new FeatureExtraction(shortBuf);
        //SteLength = (shortTermEnergy.getSteLength());
        //STE = new long[SteLength];

        audioRecord.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener(){
            @Override
            public void onMarkerReached(AudioRecord recorder) {
                //TODO Auto-generated method stub
            }

            @Override
            public void onPeriodicNotification(AudioRecord recorder) {
                //.d(TAG, "録音コールバック");
                audioRecord.read(shortBuf, 0, bufferSize );
                wavFile.addBigEndianData(shortBuf);
                //STE = shortTermEnergy.calculateShortTermEnergy();
                //analyzeEating.count(STE);
            }
        });
        // コールバックが呼ばれる間隔を指定
        audioRecord.setPositionNotificationPeriod(bufferSize / 2);
    }

    public void StopRec(){
        Log.d(TAG,"録音停止");
        isRecording = false;
        audioRecord.stop();
        wavFile.close();
        audioRecord.release();
    }

    public void StartRec(){
        Log.d(TAG,"録音開始");
        audioRecord.startRecording();
        audioRecord.read(shortBuf, 0 , bufferSize/2);

    }

    public void Destroy(){
        audioRecord.release();
    }
}

