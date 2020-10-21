package com.example.minesweeper.common;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.example.minesweeper.R;

public class InfoDialog {

    private boolean confirm;
    private String title;
    private String message;
    private AlertDialog dialog;

    // constructor
    public InfoDialog(String title, String message, Context context) {
    this.title=title;
    this.message=message;

    create(context);
    }


    public String getTitle(){
       return this.title;
    }
    public String getMessage(){
        return this.message;
    }
    public AlertDialog getDialog(){
    return this.dialog;
    }

    public void create(Context context){


        AlertDialog.Builder dialogBox = new AlertDialog.Builder(context, R.style.CustomAlertDialog).setTitle("End game?");
/*
style zmienic nalezy w pliku  res/values/styles.xml
*/
        AlertDialog dialogue =  new AlertDialog.Builder(context).setTitle(this.title)
                .setMessage(this.message)
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            }
                        })
               /* .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                }) */
                .create();

        this.dialog = dialogue;
    }



}
