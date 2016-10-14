package maeda.edu.getorientationsample;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final int MATRIX_SIZE = 16;
    float[] inR = new float[MATRIX_SIZE];
    float[] outR = new float[MATRIX_SIZE];
    float[] I = new float[MATRIX_SIZE];

    float[] orientationValues = new float[3];
    float[] magneticValues = new float[3];
    float[] accelerometerValues = new float[3];

    TextView textView;
    SensorManager mSensorManager;
    Sensor mAccelerometer, mMagneticField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    //イベントリスナーの登録
    @Override
    protected  void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //イベントリスナーの解除
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        //センサー精度が信頼できない状態のときは無視
        if(event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        //センサータイプ別に値を取得
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticValues = event.values.clone();
                break;
        }

        if(accelerometerValues != null && magneticValues != null){
            //回転行列を取得
            SensorManager.getRotationMatrix(inR, I, accelerometerValues, magneticValues);
            //回転行列を画面の向きに回転
            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
            //回転行列から回転角を取得→結果はラジアンでorientationValuesに格納される
            SensorManager.getOrientation(inR, orientationValues);

            textView.setText("垂直軸:\t" + String.valueOf(radianToDegree(orientationValues[0])) + "\n" +
                            "横軸:　" + String.valueOf(radianToDegree(orientationValues[1])) + "\n" +
                            "縦軸:　" + String.valueOf(radianToDegree(orientationValues[2])) + "\n");
        }
    }

    /**
     * ラジアンから度(°)への変換
     * @param rad ラジアン
     * @return 度(°)
     */
    public int radianToDegree (float rad){
        return (int)Math.floor(Math.toDegrees(rad));
    }
}
