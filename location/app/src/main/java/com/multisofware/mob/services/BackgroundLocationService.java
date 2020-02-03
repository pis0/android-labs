package com.multisofware.mob.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import static android.os.AsyncTask.SERIAL_EXECUTOR;

public class BackgroundLocationService extends Service {


    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    private static String TAG = "BackgroundLocationService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String fResultt;

    @Override
    public void onCreate() {

        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {



                //url
                //http://tms.multicte.com.br/sgt.mobile/ControleEntrega.svc/AtualizarDadosPosicionamento
                /*
                {
                    "token": "token",
                    "usuario": 25072,
                    "empresaMultisoftware": 18,
                    "latitude": "-27,0994205",
                    "longitude": "-52,6339212",
                    "data": "11012020105840"
                }
                */



                //http
                @SuppressLint("StaticFieldLeak")
                final AsyncTask<Void, Void, Boolean> httpTask = new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... params) {

                        HashMap regularValues;
                        String defaultResult = null;
                        URL url;
                        HttpURLConnection conn = null;
                        try {

                            url = new URL("http://tms.multicte.com.br/sgt.mobile/ControleEntrega.svc/AtualizarDadosPosicionamento");
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);

                            JSONObject jo = new JSONObject();
                            jo.put("token", "token");
                            jo.put("usuario", 25072);
                            jo.put("empresaMultisoftware", 18);
                            jo.put("data", "11012020105840");
                            jo.put("latitude", "-27,0994205");
                            jo.put("longitude", "-52,6339212");

                            Log.d(TAG, "resolveHid.json: " + jo.toString());

                            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                            os.writeBytes(jo.toString());
                            os.flush();
                            os.close();

                            final String status = String.valueOf(conn.getResponseCode());
                            final String msg = String.valueOf(conn.getResponseCode());

                            Log.d(TAG, "resolveHid.status: " + status);
                            Log.d(TAG, "resolveHid.msg: " + msg);

                            InputStream inputStream = conn.getInputStream();
                            conn.disconnect();

                            Scanner scan = new Scanner(inputStream, "UTF-8");
                            StringBuilder fResult = new StringBuilder();
                            while (scan.hasNext()) fResult.append(scan.next());

                            JSONObject jResult = new JSONObject(fResult.toString());
                            fResultt = jResult.toString();

                            Log.d(TAG, "resolveHid.result: " + fResultt);
                            //Toast.makeText(context, "resolveHid.result: " + fResultt, Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;

                        } finally {
                            if (conn != null) conn.disconnect();
                            handler.postDelayed(runnable, 10000);
                        }

                        return true;
                    }
                };

                try {
                    if(httpTask.executeOnExecutor(SERIAL_EXECUTOR).get())
                    {
                        Toast.makeText(context, "resolveHid.result: " + fResultt, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
//                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 15000);
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

//    @Override
//    public void onStart(Intent intent, int startid) {
//        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);

    }
}
