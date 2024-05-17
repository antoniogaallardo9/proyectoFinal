package com.esteban.socialmedia.loading;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.esteban.socialmedia.R;

public class LoadingDialog extends Dialog {
    public LoadingDialog(@NonNull Context context) {
        super(context);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = android.view.Gravity.CENTER;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        View view = View.inflate(context, R.layout.loading_layout, null);
        setContentView(view);
    }
}
