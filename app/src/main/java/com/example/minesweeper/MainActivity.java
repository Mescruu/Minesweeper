package com.example.minesweeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView; //do wypisywania czasu

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private TextView countdownText;
    private Button backButton;
    private Button restartButton;

    //obiekt wyswietlajacy Toasta
    public  CustomToast customToast;
    //zegar
    private CountDownTimer countDownTimer;
    private boolean timeRunning;
    private long timeLeft = 60000; //czas w milisekundach - metoda wymaga long (60 000 - 1 minut)
    public long timeLeftStartValue = 60000; //czas w milisekundach - metoda wymaga long (60 000 - 1 minut)


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //tworzenie obiektu customToast
       customToast = new CustomToast(this);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //deklaracja UI
        countdownText = findViewById(R.id.TimeText);
        backButton = findViewById(R.id.backButton);
        restartButton = findViewById(R.id.restartButton);

        final GameEngine game =  GameEngine.getInstance(); //utworzenie obiektu gry
        StartGame(game);//rozpoczecie gry

    //Utworzenie pola dialogowego
        //https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android


        //przycisk restartu.
        restartButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku restartu.
            @Override
            public void onClick(View v) {

                AlertBox(game, false); //wyswietlenie powiadomienia o zakonczenie rozgrywki

            }

        });
    }

    public void AlertBox(final GameEngine game, final boolean endGame){
        //Alert box z informacja czy na pewno chcemy zakonczyc rozgrywkę.

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog).setTitle("End game?");

        //dialogBox.setView(LayoutInflater.from(this).inflate(R.layout.alert_box_layout, null, false));

        dialogBox.setMessage("Are you sure? Your rank won't be saved") //wiadomość w alertboxie
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //  Intent k = new Intent(Game.this, GameTwo.class);
                                //  startActivity(k);
                            if(endGame){ //endgame = true wtedy zakoncz gre i przejdz do menu glownego.

                            }else{ //endGame=false wtedy zakoncz gre i rozpocznij nową.
                                game.stopGame(); //zakoncz rozgrywkę
                                StartGame(game);
                            }


                                dialog.dismiss(); //wyłacz dialog
                            }
                        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                })
                .create()
                .show();



    }

    public void StartGame(GameEngine game){
        //po 5 sekundach wyświetlenie rozpoczęcie nowej gry
        game.createGrid(this); //rozpoczecie gry

        if(timeRunning)//jezeli zegar odlicza - zatrzymaj go
        stopTimer(); //zatrzymanie zegara

        timeLeft=timeLeftStartValue;
        StartTimer(); //uruchomienie zegara
        UpdateTimer(); //update zegara

    }

    public void StartTimer(){
        //Utworzenie timera ze zmienną początkową i interwałem - 1000 oznacza milisekundy
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                UpdateTimer();
            }

            @Override
            public void onFinish() { //w momencie kiedy czas się skończy.
             String FinishText = "Time left!";
                Log.e("",FinishText);

                countdownText.setText(FinishText);
            }
        }.start();

        timeRunning=true; //bool informujacy, czy zegar pracuje
    }

    public  void stopTimer(){ //funkcja zatrzymujaca zegar
        countDownTimer.cancel();
        timeRunning = false;
    }

    public  void UpdateTimer(){
        int minutes = (int) timeLeft / 60000; //przerabiamy czas na minuty
        int seconds = (int) timeLeft % 60000 /1000;

        String TimeleftText;
        TimeleftText = ""+minutes;
        TimeleftText +=":";

        if(seconds<10) TimeleftText+="0"; //zeby pokazywac zero przed jednosciami

        TimeleftText +=seconds;

        Log.e("",TimeleftText);

        countdownText.setText(TimeleftText); //ustawienie czasu

        if(timeLeft<=0) //jezeli czas sie skonczył
            stopTimer(); //zakoncz czas
    }
}