package com.goodfellas.a492_gyroscope;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.hardware.Sensor.TYPE_GYROSCOPE;

public class MainActivity extends AppCompatActivity{

    private static final int REQUEST_ENABLE_BT = 1;
    private Button button, changeIpButton;
    private EditText editIp;
    private boolean serviceRunning = false;
    public static String ip_address = "";
    public static String mac_address = "";
    private Intent intent;
    private final String TAG = "MainActivity";
    private TextView uuidText;
    public static String uuid;


    private final static String MAC_KEY = "mac_address";

    Activity activity;
    private final int REQUEST_READ_PHONE_STATE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        activity = this;
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!serviceRunning) {
                    intent = new Intent(MainActivity.this, GyroToUbuntuService.class);
                    startService(intent);
                    button.setText("Stop taking data");
                    Toast.makeText(v.getContext(), "Service has been started", Toast.LENGTH_LONG).show();
                    serviceRunning = true;
                }
                else{
                    stopService(intent);
                    button.setText("Take data!");
                    Toast.makeText(v.getContext(), "Service has been stopped", Toast.LENGTH_LONG).show();
                    serviceRunning = false;
                }
            }
        });
        
        editIp = (EditText) findViewById(R.id.ip_address);
        changeIpButton = (Button) findViewById(R.id.change_ip);
        changeIpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setShareInstanceMACAddress(editIp.getText().toString());
            }
        });

        mac_address = getSharedInstanceMACAddress();
        editIp.setText(mac_address);

        uuidText = (TextView) findViewById(R.id.uuid);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
            TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            uuid = tManager.getDeviceId();
            Log.i(TAG, "uuid is " + uuid);
            uuidText.setText("Uuid is " + uuid);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    uuid = tManager.getDeviceId();
                    Log.i(TAG, "uuid is " + uuid);
                    uuidText.setText("Uuid is " + uuid);
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
            if(requestCode == REQUEST_ENABLE_BT){
                Toast.makeText(this, "Bluetooth has been opened", Toast.LENGTH_SHORT).show();
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }

    public String getSharedInstanceMACAddress(){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        mac_address = sharedPref.getString(MAC_KEY, "???");
        return mac_address;
    }

    public void setShareInstanceMACAddress(String mac){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MAC_KEY, mac);
        editor.commit();
        Log.i(TAG, "mac address has been set as " + mac);
        mac_address = mac;
    }
}
