package com.example.minesweeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button soloGameButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soloGameButton = findViewById(R.id.newGameSolo);

        //przycisk nowej gry.
        soloGameButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku restartu.
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameSettingActivity.class);
                startActivity(intent);
            }

        });

    }

}