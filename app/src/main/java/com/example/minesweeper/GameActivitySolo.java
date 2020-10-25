package com.example.minesweeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.minesweeper.common.CustomToast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class GameActivitySolo extends AppCompatActivity {

    private GameEngine gameEngine;

    private TextView countdownText;
    private Button backButton;

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
        backButton = findViewById(R.id.backButton);

        game =  GameEngine.getInstance(); //utworzenie obiektu gry

        // Utworzenie obserwatora
        EngineObserver engObs = new EngineObserver(this);
        // Dodanie obserwatora do obiektu game
        game.addObserver(engObs);

        //rozpoczecie gry
        AlertBox(game, 1,"Start game?","The game start at the moment. Get ready!");

        //Utworzenie pola dialogowego
        //https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android

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

        dialogBox.setMessage("Your score: "+score+".") //wiadomość w alertboxie
                .setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitGame(dialog);

                    }
                }).setCancelable(false) //uniemożliwia nacisniecia poza box
                .create()
                .show();
    }

    public void gameLost(String score){

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameActivitySolo.this, R.style.CustomAlertDialog).setTitle("You lost!");

        dialogBox.setMessage("Your score: "+score+" Go back to main menu?") //wiadomość w alertboxie
                .setNegativeButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitGame(dialog);
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
                                    case -1: //exit game
                                        exitGame(dialog);
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
                            exitGame(dialog);
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
      //  game.createGrid(this); //rozpoczecie gry

        //załaczenie tekstu zegara oraz jego zresetowanie
        game.windUpTheClock(countdownText);
    }

    public void exitGame(DialogInterface dialog){


                game.deleteObservers();
                game.stopTimer();

                dialog.dismiss(); //wylacz dialogbox

                Intent intent = new Intent(GameActivitySolo.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
    }


    /*

    protected void onPause() {
        super.onPause();

        if(gameEngine.countObservers()>0)
        gameEngine.deleteObservers();
        gameEngine.stopTimer();


        finish();
    }

    protected void onStop() {
        super.onStop();

        gameEngine.stopGame();

        if(gameEngine.countObservers()>0)
        gameEngine.deleteObservers();

        gameEngine.stopTimer();

        finish();
    }
    */


}