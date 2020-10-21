package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainMenuActivity extends AppCompatActivity {

    private Button soloGameButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        soloGameButton = findViewById(R.id.newGameSolo);
        logoutButton = findViewById(R.id.logoutButton);

        //przycisk nowej gry.
        soloGameButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku restartu.
            @Override
            public void onClick(View v) {
                //w przypadku użycia przycisku nowej gry.

                Intent intent = new Intent(MainMenuActivity.this, GameSettingActivity.class);
                startActivity(intent);
            }

        });

        //przycisk nowej gry.
        logoutButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku logout.
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                startActivity(intent);
            }

        });

    }
}