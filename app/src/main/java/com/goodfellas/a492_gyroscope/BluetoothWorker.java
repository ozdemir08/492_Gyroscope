package com.goodfellas.a492_gyroscope;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mehmet on 15/05/2017.
 */

public class BluetoothWorker {

//    private static UUID generalUuid = UUID.fromString(MainActivity.uuid);

    private static BluetoothSocket socket;
    private static String TAG = "Bluetooth";
    private static OutputStream outputStream;


    private static BluetoothSocket getBluetoothSocket(){

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                if(device.getName().startsWith(("Mehmet"))){
                    Log.i(TAG, device.getName());
                    try {
                        UUID generalUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                        return device.createRfcommSocketToServiceRecord(generalUuid);
                    } catch (IOException e) {
                        return null;
                    }
                }
            }
        }
        else {
            Log.e(TAG, "no paired devices");
        }
        return null;
    }

    public static int sendData(String status){

        if(socket == null)
            socket = getBluetoothSocket();

        if(socket == null){
            Log.e(TAG, "socket is null");
            return 0;
        }
        else {
            Log.i(TAG, "socket is found yeahhh!!");
        }

        try {
            socket.connect();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            socket = null;
            Log.e(TAG, "socket connection problem");
            return 1;
        }

        if(socket != null){
            try {
                if(outputStream == null)
                    outputStream = socket.getOutputStream();
                outputStream.write(status.getBytes());
                outputStream.flush();
//                socket.close();
                Log.i(TAG, "EVERYTHING IS OK");
                return 2;
            } catch (IOException e) {
                socket = null;
                return 3;
            }
        }else{
            return 4;
        }
    }
}
