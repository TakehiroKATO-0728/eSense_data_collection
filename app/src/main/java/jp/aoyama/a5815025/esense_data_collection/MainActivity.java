package jp.aoyama.a5815025.esense_data_collection;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import io.esense.esenselib.*;

public class MainActivity extends Activity implements ESenseConnectionListener, ESenseSensorListener{

    TextView textView;
    public AudioManager audioManager;
    Calendar cal;
    Record rec;
    OutputCsv outputCsv;
    private long startTime;
    private long progress_time;


    int flag = 0;
    private String fileName;
    int minute;
    int second;
    int milli;
    int month;
    int day;
    int hour;
    int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.text);
        textView.setText(R.string.start_message);

        //eSenseを登録
        ESenseManager manager = new ESenseManager("eSense-0041", this,this);
        manager.connect(2000);

        //音声録音用のaudioManagerとrecインスタンス
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.startBluetoothSco();
        audioManager.setBluetoothScoOn(true);
        rec=new Record();

        outputCsv = new OutputCsv();
    }

    @Override
    public void onDeviceFound(ESenseManager eSenseManager) {
        eSenseManager.connect(4000);
    }

    @Override
    public void onDeviceNotFound(ESenseManager eSenseManager) {

    }

    @Override
    public void onConnected(ESenseManager eSenseManager) {
        //センサーリスナーを登録し、センサーデータを受信するサンプリングレートを指定
        eSenseManager.registerSensorListener(this,100);
    }

    @Override
    public void onDisconnected(ESenseManager eSenseManager) {

    }

    public void onClick(View v){

        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        second = cal.get(Calendar.SECOND);
        milli = cal.get(Calendar.MILLISECOND);

        switch (v.getId()){
            case R.id.start_btn:
                if(flag == 0) {
                    textView.setText(R.string.doing_message);
                    startTime = System.currentTimeMillis();
                    flag = 1;
                    break;
                }
            case R.id.stop_btn:
                if(flag == 1) {
                    rec.StopRec();
                    textView.setText(R.string.done_message);
                    flag = 0;
                    break;
                }
        }
    }

    @Override
    public void onSensorChanged(ESenseEvent eSenseEvent) {
        short[] acc=eSenseEvent.getAccel();
        short[] gyro=eSenseEvent.getGyro();
        if(flag == 1) {
            fileName = Environment.getExternalStorageDirectory() + "/" + year + (month + 1) + day + "_" + hour + "_" + minute + "_" + second + ".wav";
            rec.init(fileName, audioManager);
            rec.StartRec();

            progress_time = System.currentTimeMillis() - startTime;
            outputCsv.write( String.valueOf(acc[0])+ "," + String.valueOf(acc[1])+ "," +String.valueOf(acc[2])+ "," +String.valueOf(gyro[0])+ "," + String.valueOf(gyro[1])+ "," +String.valueOf(gyro[2])+ "," +String.valueOf(progress_time));
        }
    }
}
