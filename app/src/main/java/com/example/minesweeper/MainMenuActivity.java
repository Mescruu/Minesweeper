package com.example.minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.minesweeper.common.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainMenuActivity extends AppCompatActivity {

    private Button soloGameButton, multiplayerGameButton, logoutButton, rankButton;
    FirebaseAuth mFirebaseAuth;
    CustomToast customToast;

    String playerName="";
    DatabaseReference playerRef;
    FirebaseDatabase database;

    boolean exitOnce;
    boolean firstChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        customToast = new CustomToast(this);

        exitOnce=true;
        firstChange=true;

        //instancja firebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        playerName = preferences.getString("playerName","");

        //pobranie uzytkownika
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        customToast.showToast("Welcome "+user.getDisplayName());

        database = FirebaseDatabase.getInstance();

        soloGameButton = findViewById(R.id.newGameSolo);
        multiplayerGameButton = findViewById(R.id.newGameMultiplayer);

        logoutButton = findViewById(R.id.logoutButton);
        rankButton = findViewById(R.id.rankButton);
        //sprawdzenie czy gracz istnieje
        playerName = user.getDisplayName();

        playerRef = database.getReference("players/"+playerName);

        //przycisk nowej gry solo.
        soloGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(exitOnce){
                    Intent intent = new Intent(MainMenuActivity.this, GameSettingActivity.class);
                    intent.putExtra("GameType", "solo");
                    startActivity(intent);
                    finish();
                    exitOnce=false;
                }

            }
        });

        //przycisk nowej gry multi.
        multiplayerGameButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku restartu.
            @Override
            public void onClick(View v) {
                //w przypadku użycia przycisku nowej gry.

                    addEventListener();
            }

        });

        //przycisk rankingu.
        rankButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku restartu.
            @Override
            public void onClick(View v) {
                //w przypadku użycia przycisku nowej gry.

                if(exitOnce){
                    Intent intent = new Intent(MainMenuActivity.this, RankActivity.class);
                    startActivity(intent);
                    finish();
                    exitOnce=false;
                }
            }

        });

        //przycisk wylogowania.
        logoutButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku logout.
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                /* nie usuwaj gracza
                //usuniecie rekordu gracza
                database.getReference("players")
                        .child(playerName)
                        .removeValue();
                */

                if(exitOnce){
                    Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    exitOnce=false;
                }

            }

        });

    }

    @Override
    public void onBackPressed() {
        customToast.showToast("Back button blocked!");
    }

    private void addEventListener(){
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(!firstChange){
                        customToast.showToast("Your score has changed!");
                    }

                    //w razie sukcesu rozpocznij nowa aktywnosc po dodaniu gracza
                     if(!playerName.equals("")){

                     SharedPreferences preferences = getSharedPreferences("PREFS",0);
                     SharedPreferences.Editor editor = preferences.edit();
                     editor.putString("playerName", playerName);
                     editor.apply();
                         if(exitOnce) {

                             firstChange=false;
                             startActivity((new Intent(getApplicationContext(), ActiveUserList.class)));
                             finish();
                             exitOnce=false;
                         }
                     }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customToast.showToast("Error");
            }
        });
    }
}