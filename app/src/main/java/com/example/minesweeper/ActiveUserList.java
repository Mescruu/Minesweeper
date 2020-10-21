package com.example.minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.minesweeper.common.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActiveUserList extends AppCompatActivity {
    ListView listView;
    Button button;

    //ista dostepnych graczy
    List<String> roomsList;

    String playerName ="";
    String roomName="";

    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_user_list);

        database = FirebaseDatabase.getInstance();

        //pobierz nazwe gracza i zapisz jego nazwe jako nazwe pokoju
       // SharedPreferences preferences = getSharedPreferences("PREFS",0);
       // playerName = preferences.getString("playerName", "");

        //pobranie z firebase nazwy uzytkownika
        //instancja firebaseAuth
       FirebaseAuth  mFirebaseAuth = FirebaseAuth.getInstance();
        //pobranie uzytkownika
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //ustawienie loginu uzytkownika
        playerName = user.getDisplayName();

        roomName = playerName;

        GameEngine.WIDTH = 5;
        GameEngine.HEIGHT = 5;
        GameEngine.BOMB_NUMBER=5;

        listView = findViewById(R.id.listView);
        button = findViewById(R.id.ReadyButton);

        // wszystkie mozliwe pokoje
        roomsList = new ArrayList<>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //utworzenie pokoju i dodanie siebie jako gracz 1
                button.setText("GET READY");
                button.setEnabled(false);
                roomName = playerName;

                roomRef = database.getReference("rooms/"+roomName+"/player1");
                addRoomEventListener();
                roomRef.setValue(playerName);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //join an existing room and add yourself as player2
                roomName = roomsList.get(position);

                roomRef = database.getReference("rooms/"+roomName+"/player2");
                addRoomEventListener();
                roomRef.setValue(playerName);
            }
        });

        //Wyswietl jezeli nowy pokoj jest dostepny
    addRoomsEventListener();
    }

    @Override
    public void onBackPressed() {

        //usuniecie rekordu gracza
        database.getReference("rooms")
                .child(playerName)
                .removeValue();

        startActivity((new Intent(getApplicationContext(),MainMenuActivity.class)));
        finish();
    }

    private void addRoomEventListener(){
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //dołaczenie do pokoju
                button.setText("GET READY");
                button.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //bład
                button.setText("GET READY");
                button.setEnabled(true);
                new CustomToast(getApplicationContext()).showToast("Error");
            }
        });
    }

    private void addRoomsEventListener(){
        roomRef = database.getReference("rooms");
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //pokaz liste pokoi
                roomsList.clear();
                Iterable<DataSnapshot> rooms = snapshot.getChildren();
                for(DataSnapshot s:rooms){
                    roomsList.add(s.getKey());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ActiveUserList.this,
                            android.R.layout.simple_list_item_1, roomsList);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            //bład
            }
        });
    }
}