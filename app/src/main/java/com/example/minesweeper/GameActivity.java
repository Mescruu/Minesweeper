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

public class GameActivity extends AppCompatActivity {

    private TextView countdownText,loginText;
    private Button backButton;

    private GameEngine game;

    //obiekt wyswietlajacy Toasta
    public CustomToast customToast;



    /*-----------------MULTI-----------------*/
    //obiekt firebase
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase database;

    //gra multi
    String playerName="";
    String opponentLogin="";

    String roomName = "";
    String role="";
    /*-----------------MULTI-----------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ustawienie layoutu
        setContentView(R.layout.activity_game);

        //tworzenie obiektu customToast
        customToast = new CustomToast(this);

        //deklaracja UI
        countdownText = findViewById(R.id.TimeText);
        loginText = findViewById(R.id.loginText);
        backButton = findViewById(R.id.backButton);

        /*--------------------MULTI---------------------*/
        //instancja firebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        //pobranie uzytkownika
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //ustawienie loginu uzytkownika
        loginText.setText("...");

        //ustawienie loginu uzytkownika
        playerName = user.getDisplayName();

        Bundle extras = getIntent().getExtras();
        if(extras !=null){
            roomName = extras.getString("roomName");
            if(roomName.equals(playerName)){ //z nazwy pokoju zostaw ciag znakow o dlugosci playerName i porownaj do playerName
                role="host";
                opponentLogin="---";
            }else{
                role="guest";
                opponentLogin= roomName; //nazwa pokoju
            }
        }
        Log.e("player name", "Player name taken from auth sys:" + playerName);
        Log.e("player name", "Player name taken from room name:"+roomName);
        Log.e("room name", roomName);
        Log.e("Role", role);

        loginText.setText(opponentLogin);

        /*--------------------MULTI---------------------*/
        game =  GameEngine.getInstance(); //utworzenie obiektu gry

        // Utworzenie obserwatora
        EngineObserver engObs = new EngineObserver(GameActivity.this);
        // Dodanie obserwatora do obiektu game
        game.addObserver(engObs);

        AlertBox(game, 1,"Start game?","The game start at the moment. Get ready!");

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

    public void gameWin(String s){

        int scoreNumber = Integer.parseInt(s);
        s = ""+(scoreNumber+100); //za wygraną dodaj 100 punktów

        final String score=s; //przekaz wartosc

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameActivity.this, R.style.CustomAlertDialog).setTitle("You won!");

        dialogBox.setMessage("Your score: "+score+" Your score will be saved. Go back to main menu.") //wiadomość w alertboxie
                .setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                exitGame(dialog, score);
                            }
                        })
                .setCancelable(false) //uniemożliwia nacisniecia poza box
                .create()
                .show();
    }

    public void gameLost(final String score){

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(GameActivity.this, R.style.CustomAlertDialog).setTitle("You lost!");

        dialogBox.setMessage("Your score: "+score+" It could be worse..") //wiadomość w alertboxie
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                exitGame(dialog, score); //sprobuj wylaczyc gre
                            }
                        })
                .setCancelable(false) //uniemożliwia nacisniecia poza box
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
                                    case -1: //exit game
                                        exitGame(dialog, "0"); //sprobuj wylaczyc gre
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
                            exitGame(dialog,"0"); //sproboj wylaczyc gre

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

    public void exitGame(DialogInterface d, String s){

    final DialogInterface dialog = d; //ustawienie jako final, zeby mozna bylo dissmisowac w onDataChange
    final String score = s;
        Log.e("Exit room:", playerName);
        Log.e("Role", role);
        Log.e("Score", score);


        //jezeli gracz zdobyl jakies punkty
        if(!score.equals("0")){
            Log.e("score isnt 0",score);

            if(role.equals("host")){
                //ustaw liczbe punktow gracza 1
                database.getReference("rooms")
                        .child(roomName)
                        .child("player1-score")
                        .setValue(score);

                Log.e("set score to player1",score);

            }else{
                //ustaw liczbe punktow gracza 2
                database.getReference("rooms")
                        .child(roomName)
                        .child("player2-score")
                        .setValue(score);

                Log.e("set score to player2",score);
            }
        }


        //referencja do pokoju
        DatabaseReference roomRef = database.getReference("rooms").child(roomName);

        //nasluchuj (raz) czy wystapila zmiana w pokoju
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(role.equals("host")){ //jezeli Ty jestes graczem nr 1
                    //ustaw pole jako rozegrane przez gracza 1 (gracz 2 moze odebrac wyzwanie
                    database.getReference("rooms")
                            .child(roomName)
                            .child("played")
                            .removeValue();
                }else{ //jezeli jestes graczem 2.
                    //utworzenie pola informujacego o usuniecie gry
                    database.getReference("rooms")
                            .child(roomName)
                            .child("remove")
                            .setValue("remove");
                }


                game.deleteObservers();
                game.stopTimer();

                dialog.dismiss(); //wylacz dialogbox

                Intent intent = new Intent(GameActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}