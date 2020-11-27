package com.assukar.android.attest;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    private TextView output;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        output = findViewById(R.id.output);
        output.setText("apkSHA: " + getApkSha());
    }






    public static String getApkSha() {

        String apkPath = context.getPackageCodePath();
        MessageDigest msgDigest = null;

        try {

            msgDigest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = new byte[1024];
            int byteCount;
            FileInputStream fis = new FileInputStream(new File(apkPath));

            while ((byteCount = fis.read(bytes)) > 0) {
                msgDigest.update(bytes, 0, byteCount);
            }

            BigInteger bi = new BigInteger(1, msgDigest.digest());
            String sha = bi.toString(16);

            fis.close();

            return sha;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
