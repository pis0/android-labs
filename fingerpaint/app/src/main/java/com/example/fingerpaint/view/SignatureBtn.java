package com.example.fingerpaint.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;

public class SignatureBtn extends AppCompatButton {

    private static final String TAG = SignatureBtn.class.getSimpleName();

    public SignatureBtn(final Context context, final String label, final View.OnClickListener listener) {
        super(context);

        super.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
        super.setText(label);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        super.setLayoutParams(params);
        super.setRotation(90);

        super.setOnClickListener(listener);
    }
}
