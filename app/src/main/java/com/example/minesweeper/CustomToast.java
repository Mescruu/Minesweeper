package com.example.minesweeper;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

//klasa tworzaca customowy toast
public class CustomToast {

    private Activity activity;
    private Context context;

    CustomToast(Context context){
        Activity activity = (Activity) context;

        this.activity=activity;
        this.context=context;
    }

    public void showToast(String text) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) this.activity.findViewById(R.id.toast_root));


        TextView toastText = layout.findViewById(R.id.toast_text);
        toastText.setText(text);
        Toast toast = new Toast(this.context);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
