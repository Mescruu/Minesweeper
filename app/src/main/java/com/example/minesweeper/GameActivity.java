package com.example.minesweeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.minesweeper.GameEngine;
import com.example.minesweeper.MainActivity;
import com.example.minesweeper.common.CustomToast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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

import java.util.Observer;

public class GameActivity extends AppCompatActivity {

    private TextView countdownText;
    private Button backButton;
    private Button restartButton;

    //obiekt wyswietlajacy Toasta
    public CustomToast customToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        //ustawienie layoutu
        setContentView(R.layout.activity_game);


        //tworzenie obiektu customToast
        customToast = new CustomToast(this);

        //deklaracja UI
        countdownText = findViewById(R.id.TimeText);
        backButton = findViewById(R.id.backButton);
        restartButton = findViewById(R.id.restartButton);

       final GameEngine game =  GameEngine.getInstance(); //utworzenie obiektu gry


        // Utworzenie obserwatora
        EngineObserver engObs = new EngineObserver(this);
        // Dodanie obserwatora do obiektu game
        game.addObserver(engObs);

        /*
        if(game.hasChanged()){
            Log.e("","zmieniło sięę");
            //game.notifyObservers();
        }*/

        //rozpoczecie gry
        AlertBox(game, 1,"Start game?","The game start at the moment. Get ready!");


        //Utworzenie pola dialogowego
        //https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android


        //przycisk restartu.
        restartButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku restartu.
            @Override
            public void onClick(View v) {
                AlertBox(game, 0, "Restart game?","Are you sure? Your rank won't be saved"); //wyswietlenie powiadomienia o zakonczenie rozgrywki
            }

        });

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

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameActivity.this, R.style.CustomAlertDialog).setTitle("You won!");

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
                        Intent intent = new Intent(GameActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();

                    }
                }).setCancelable(false) //uniemożliwia nacisniecia poza box
                .create()
                .show();
    }

    public void gameLost(String score){

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameActivity.this, R.style.CustomAlertDialog).setTitle("You lost!");

        dialogBox.setMessage("Your score: "+score+" Do you want play again?") //wiadomość w alertboxie
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //zresetuj aktywnosc
                                Intent intent = new Intent(GameActivity.this, GameActivity.class);
                                startActivity(intent);

                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        Intent intent = new Intent(GameActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).setCancelable(false) //uniemożliwia nacisniecia poza box
                .create()
                .show();
    }

    public void AlertBox(final GameEngine game, final int option, String title, String massage){
        //Alert box z informacja czy na pewno chcemy zakonczyc rozgrywkę.

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameActivity.this, R.style.CustomAlertDialog).setTitle(title);

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
                                        Intent intent = new Intent(GameActivity.this, MainActivity.class);
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
                            Intent intent = new Intent(GameActivity.this, MainActivity.class);
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

}