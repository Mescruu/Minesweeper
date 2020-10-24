package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Random;

public class GameSettingActivity extends AppCompatActivity {

    private Button confirmGameSettings;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setting);

        radioGroup = (RadioGroup) findViewById(R.id.RadioGroup);

        confirmGameSettings = findViewById(R.id.confirmGameSettings);

        //przycisk nowej gry.
        confirmGameSettings.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku restartu.
            @Override
            public void onClick(View v) {

                // get selected radio button from radioGroup
                int selected = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton) findViewById(selected);
                Log.e("",(String)radioButton.getText());

                switch((String)radioButton.getText()) {
                    case "Easy":
                        //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                        GameEngine.WIDTH = 5;
                        GameEngine.HEIGHT = 5;
                        GameEngine.BOMB_NUMBER=5;
                        break;
                    case "Normal":
                        //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                        GameEngine.WIDTH = 10;
                        GameEngine.HEIGHT = 10;
                        GameEngine.BOMB_NUMBER=12;
                        break;
                    case "Hard":
                        //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                        GameEngine.WIDTH = 12;
                        GameEngine.HEIGHT = 12;
                        GameEngine.BOMB_NUMBER=20;
                        break;
                    default:
                        //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                        GameEngine.WIDTH = 4;
                        GameEngine.HEIGHT = 4;
                        GameEngine.BOMB_NUMBER=4;
                        break;
                }

                //ustalenie seeda gry
                Random generator = new Random();
                GameEngine.SEED = generator.nextLong();

                Intent intent = new Intent(GameSettingActivity.this, GameActivitySolo.class);
                startActivity(intent);
                finish();
            }

        });

    }

    public void onBackPressed() {

        Intent intent = new Intent(GameSettingActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

}