package com.example.minesweeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.minesweeper.GameEngine;
import com.example.minesweeper.MainActivity;
import com.example.minesweeper.common.CustomToast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minesweeper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Observer;
import java.util.Random;

public class GameActivitySolo extends AppCompatActivity {

    private TextView countdownText,loginText;
    private Button backButton;
    private Button restartButton;

    private GameEngine game;

    //obiekt wyswietlajacy Toasta
    public CustomToast customToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ustawienie layoutu
        setContentView(R.layout.activity_game_solo);

        //tworzenie obiektu customToast
        customToast = new CustomToast(this);

        //deklaracja UI
        countdownText = findViewById(R.id.TimeText);
        loginText = findViewById(R.id.loginText);

        backButton = findViewById(R.id.backButton);
        restartButton = findViewById(R.id.restartButton);


            game =  GameEngine.getInstance(); //utworzenie obiektu gry

            // Utworzenie obserwatora
            EngineObserver engObs = new EngineObserver(GameActivitySolo.this);
            // Dodanie obserwatora do obiektu game
            game.addObserver(engObs);

            AlertBox(game, 1,"Start game?","The game start at the moment. Get ready!");


        //przycisk restartu.
        restartButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku restartu.
            @Override
            public void onClick(View v) {

                AlertBox(game, 0, "Restart game?","Are you sure? Your rank won't be saved"); //wyswietlenie powiadomienia o zakonczenie rozgrywki
            }
        });

        /*-----------------MULTI-----------------*/
        //przycisk konca gry.
        backButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku restartu.
            @Override
            public void onClick(View v) {
                AlertBox(game, -1,"End game?", "Are you sure? Your rank won't be saved"); //wyswietlenie powiadomienia o zakonczenie rozgrywki
            }

        });
    }



    @Override
    public void onBackPressed() {
        customToast.showToast("Back button blocked!");
    }

    public void gameWin(String score){

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameActivitySolo.this, R.style.CustomAlertDialog).setTitle("You won!");

        dialogBox.setMessage("Your score: "+score+" Do you want add your it to the rank?") //wiadomość w alertboxie
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss(); //wyłacz dialog
                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        Intent intent = new Intent(GameActivitySolo.this, MainMenuActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).setCancelable(false) //uniemożliwia nacisniecia poza box
                .create()
                .show();
    }

    public void gameLost(String score){

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameActivitySolo.this, R.style.CustomAlertDialog).setTitle("You lost!");

        dialogBox.setMessage("Your score: "+score+" Do you want play again?") //wiadomość w alertboxie
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //zresetuj aktywnosc
                                Intent intent = new Intent(GameActivitySolo.this, GameActivitySolo.class);
                                startActivity(intent);

                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        Intent intent = new Intent(GameActivitySolo.this, MainMenuActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).setCancelable(false) //uniemożliwia nacisniecia poza box
                .create()
                .show();
    }

    public void AlertBox(final GameEngine game, final int option, String title, String massage){
        //Alert box z informacja czy na pewno chcemy zakonczyc rozgrywkę.

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameActivitySolo.this, R.style.CustomAlertDialog).setTitle(title);

        //dialogBox.setView(LayoutInflater.from(this).inflate(R.layout.alert_box_layout, null, false));

        dialogBox.setMessage(massage) //wiadomość w alertboxie
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //  Intent k = new Intent(Game.this, GameTwo.class);
                                //  startActivity(k);

                                switch(option) {
                                    case 1: //Game start
                                        StartGame(game);//rozpoczecie gry
                                        break;
                                    case 0: //Game restart
                                        game.stopGame(); //zakoncz rozgrywkę
                                        StartGame(game);
                                        break;
                                    case -1: //exit game
                                        Intent intent = new Intent(GameActivitySolo.this, MainMenuActivity.class);
                                        startActivity(intent);
                                        finish();

                                        break;
                                    default:

                                }

                                dialog.dismiss(); //wyłacz dialog
                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        if(option==1){
                            Intent intent = new Intent(GameActivitySolo.this, MainMenuActivity.class);
                            startActivity(intent);
                            finish();

                        }else{
                            dialog.dismiss();
                        }
                    }
                }).setCancelable(false) //uniemożliwia nacisniecia poza box
                .create()
                .show();
    }

    public void StartGame(GameEngine game){

        //po 5 sekundach wyświetlenie rozpoczęcie nowej gry
        game.createGrid(this); //rozpoczecie gry

        //załaczenie tekstu zegara oraz jego zresetowanie
        game.windUpTheClock(countdownText);
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();

    }
}