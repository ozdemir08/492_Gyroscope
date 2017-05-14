package com.goodfellas.a492_gyroscope;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mehmet on 21/04/2017.
 */

public class Connection extends AsyncTask<Void, Void, Void>{

    public static OkHttpClient client = new OkHttpClient();

    static String TAG = "Connection";
    Float yaw, pitch;

    public Connection(Float yaw, Float pitch){
        Log.i(TAG, "constructor");
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    protected Void doInBackground(Void... params) {
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Yaw", "" + yaw)
                .addFormDataPart("Pitch", "" + pitch)
                .build();

        Request request = new Request.Builder()
                .url(getUrl())
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.i(TAG, response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "Connection problem");
        }
        return null;
    }

    private String getUrl(){
        Log.i(TAG, "ip_address" + MainActivity.ip_address);
        String s = "http://" + MainActivity.ip_address + ":8000/Angles/postData";
        Log.i(TAG, "ip from getUrl: " + s);
        return s;
    }
}
