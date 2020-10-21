package com.example.minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private Button soloGameButton, logoutButton;
    FirebaseAuth mFirebaseAuth;
    CustomToast customToast;

    String playerName="";
    DatabaseReference playerRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        customToast = new CustomToast(this);



        //instancja firebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS",0);
        playerName = preferences.getString("playerName","");

        //pobranie uzytkownika
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        customToast.showToast("Welcome "+user.getDisplayName());

        database = FirebaseDatabase.getInstance();

        soloGameButton = findViewById(R.id.newGameSolo);
        logoutButton = findViewById(R.id.logoutButton);

        //sprawdzenie czy gracz istnieje
        playerName = user.getDisplayName();


        //przycisk nowej gry.
        soloGameButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku restartu.
            @Override
            public void onClick(View v) {
                //w przypadku użycia przycisku nowej gry.

                if(!playerName.equals("")){
                    playerRef = database.getReference("players/"+playerName);
                    addEventListener();
                    playerRef.setValue("");
                }

                Intent intent = new Intent(MainMenuActivity.this, ActiveUserList.class);
                startActivity(intent);
            }

        });

        //przycisk wylogowania.
        logoutButton.setOnClickListener(new View.OnClickListener(){

            //w przypadku użycia przycisku logout.
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                //usuniecie rekordu gracza
                database.getReference("players")
                        .child(playerName)
                        .removeValue();

                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                startActivity(intent);
            }

        });

    }

    private void addEventListener(){
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //w razie sukcesu rozpocznij nowa aktywnosc po dodaniu gracza
                     if(!playerName.equals("")){
                         SharedPreferences preferences = getSharedPreferences("PREFS",0);
                     SharedPreferences.Editor editor = preferences.edit();
                     editor.putString("playerName", playerName);
                     editor.apply();

                     startActivity((new Intent(getApplicationContext(),ActiveUserList.class)));
                     finish();

                     }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                customToast.showToast("Error");
            }
        });
    }
}