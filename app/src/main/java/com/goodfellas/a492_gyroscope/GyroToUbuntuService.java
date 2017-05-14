package com.goodfellas.a492_gyroscope;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * Created by mehmet on 21/04/2017.
 */

public class GyroToUbuntuService extends Service implements SensorEventListener{

    static final String TAG = "GyroToUbuntuService";
    float[] rotMat = new float[9];
    float[] orientationVals = new float[3];
    private SensorManager mSensorManager;
    private Sensor mSensor, mAccelerometer, mGyroscope ;
    int numberData = 0;
    private Calendar startTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "GyroToUbuntuService basladi", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onStartCommand");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(numberData == 0)
            startTime = Calendar.getInstance();
        numberData++;
        if(numberData == 100){
            String oldTime = "" + startTime.get(Calendar.MINUTE) + ":" + startTime.get(Calendar.SECOND);
            String currentTime = "" + Calendar.getInstance().get(Calendar.MINUTE) + ":" + Calendar.getInstance().get(Calendar.SECOND);
            Log.i(TAG, "100 data has been taken between:\n\t" + oldTime + "\n\t" + currentTime);
        }

        Log.i(TAG, "onSensorChanged");
        // It is good practice to check that we received the proper sensor event
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR)
        {
            // Convert the rotation-vector to a 4x4 matrix.
            SensorManager.getRotationMatrixFromVector(rotMat,
                    event.values);
            SensorManager
                    .remapCoordinateSystem(rotMat,
                            SensorManager.AXIS_X, SensorManager.AXIS_Z,
                            rotMat);
            SensorManager.getOrientation(rotMat, orientationVals);

            //Server'dan yollamak icindi burasi.
//            new Connection(orientationVals[0], orientationVals[1]).execute();

            int res = BluetoothWorker.sendData(String.valueOf(orientationVals[0]) + "," + String.valueOf(orientationVals[1]));
            switch (res){
                case 0:
                    Toast.makeText(this, "Socket is null", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(this, "Socket connection problem", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(this, "Everything is awesome", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(this, "IO Exception", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(this, "Socket is null", Toast.LENGTH_SHORT).show();
                    break;
            }


            // Optionally convert the result from radians to degrees
            orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);
            orientationVals[1] = (float) Math.toDegrees(orientationVals[1]);
            orientationVals[2] = (float) Math.toDegrees(orientationVals[2]);

//            Log.i(TAG, Calendar.getInstance().toString());
            Log.i(TAG, " Yaw: " + orientationVals[0] + "\n Pitch: "
                    + orientationVals[1] + "\n Roll (not used): "
                    + orientationVals[2]);


        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static byte [] float2ByteArray (float value)
    {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

}
