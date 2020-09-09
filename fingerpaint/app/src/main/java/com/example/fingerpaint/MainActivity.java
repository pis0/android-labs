package com.example.fingerpaint;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fingerpaint.view.BackButton;
import com.example.fingerpaint.view.DrawingView;
import com.example.fingerpaint.view.SignatureBtn;
import com.example.fingerpaint.view.SignatureLabel;
import com.example.fingerpaint.view.SignatureView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    DrawingView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        FrameLayout layout = new FrameLayout(this);
        FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        layout.setLayoutParams(layoutparams);

        setContentView(layout);

        SignatureView sv = new SignatureView(this);
        layout.addView(sv);

        SignatureLabel sl = new SignatureLabel(this);
        layout.addView(sl);

        dv = new DrawingView(this);
        layout.addView(dv);

        BackButton backBtn = new BackButton(this);
        layout.addView(backBtn);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        SignatureBtn clearBtn = new SignatureBtn(this, "clear", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clear!");
            }
        });
        layout.addView(clearBtn);

        SignatureBtn saveBtn = new SignatureBtn(this, "save", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "save!");
            }
        });
        layout.addView(saveBtn);


        //saveBtn.setY(metrics.heightPixels);


    }


}