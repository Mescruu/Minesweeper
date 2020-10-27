package com.example.minesweeper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Random;

public class ActiveUserList extends AppCompatActivity {
    ListView listView;
    Button button;

    //ista dostepnych graczy
    List<String> roomsList;
    List<String> seedList;
    List<String> difficultyList;

    String playerName ="";
    String roomName="";

    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;
    DatabaseReference playersRef;

    private ValueEventListener roomListener;
    private ValueEventListener roomsListener;
    private ValueEventListener playersListener;

    private String winningPlayer;

    long seed;
    boolean exitOnce;

    int difficulty =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_user_list);

        Log.e("Active user list", "activate!");

        database = FirebaseDatabase.getInstance();

        //pobranie z firebase nazwy uzytkownika
        //instancja firebaseAuth
        //pobranie uzytkownika
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //ustawienie loginu uzytkownika
        playerName = user.getDisplayName();

        roomName = playerName;

        winningPlayer=""; //na poczatku aktywnosci ustaw winingPlayer jako "", ponieważ nie ma jeszcze wygranego.

        exitOnce=true;

        listView = findViewById(R.id.listView);
        button = findViewById(R.id.ReadyButton);

        // wszystkie mozliwe pokoje
        roomsList = new ArrayList<>();
        //seedy:
        seedList = new ArrayList<>();
        //poziomy trudnosci
        difficultyList = new ArrayList<>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //przy tworzeniu pokoju nalezy wybrac poziom trudnosci
            setDifficulty();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomName = roomsList.get(position);//ustal nazwe pokoju z listy
                roomName = roomName.substring(0, roomName.length() - 14); //odjęcie od nazwy pokoju tekstu np:  " difficulty: 1"


                seed = Long.parseLong(seedList.get(position)); //ustal seed z pobranej listy seedów

                String difficultyString = difficultyList.get(position); //ustal poziom trudnosci z pobranej listy seedów

                switch(difficultyString) {
                    case "1":
                        //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                        GameEngine.WIDTH = 5;
                        GameEngine.HEIGHT = 5;
                        GameEngine.BOMB_NUMBER=5;

                        break;
                    case "2":
                        //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                        GameEngine.WIDTH = 10;
                        GameEngine.HEIGHT = 10;
                        GameEngine.BOMB_NUMBER=12;
                        break;
                    case "3":
                        //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                        GameEngine.WIDTH = 12;
                        GameEngine.HEIGHT = 12;
                        GameEngine.BOMB_NUMBER=20;
                        break;
                    default:
                        //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                        GameEngine.WIDTH = 10;
                        GameEngine.HEIGHT = 10;
                        GameEngine.BOMB_NUMBER=12;
                        break;
                }

                GameEngine.SEED = seed; //ustalenie seeda w silniku gry

                roomRef = database.getReference("rooms/"+roomName+"/player2");
                addRoomEventListener();
                roomRef.setValue(playerName);

            }
        });

        //Wyswietl jezeli nowy pokoj jest dostepny
        addRoomsEventListener();


    }

    public void setDifficulty(){

        Log.e("logo", "alertbox");

        AlertDialog.Builder dialogBox = new AlertDialog.Builder(ActiveUserList.this, R.style.CustomAlertDialog).setTitle("Check difficulty level.");

        String[] items = {"Easy","Normal","Hard"};
        int checkedItem = 1;

        //defaultowa wartość
        //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
        GameEngine.WIDTH = 10;
        GameEngine.HEIGHT = 10;
        GameEngine.BOMB_NUMBER=12;
        difficulty=2;

        dialogBox.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                                GameEngine.WIDTH = 5;
                                GameEngine.HEIGHT = 5;
                                GameEngine.BOMB_NUMBER=5;
                                difficulty=1;
                                break;
                            case 1:
                                //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                                GameEngine.WIDTH = 10;
                                GameEngine.HEIGHT = 10;
                                GameEngine.BOMB_NUMBER=12;
                                difficulty=2;

                                break;
                            case 2:
                                //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                                GameEngine.WIDTH = 12;
                                GameEngine.HEIGHT = 12;
                                GameEngine.BOMB_NUMBER=20;
                                difficulty=3;

                                break;
                            default:
                                //ustawienie szerokosci, wysokosci i ilosci bomb musi byc ustawione wczesniej niz wywolanie layoutu gry
                                GameEngine.WIDTH = 10;
                                GameEngine.HEIGHT = 10;
                                GameEngine.BOMB_NUMBER=12;
                                difficulty=2;
                                break;
                        }

                        //ustawienie seeda silnika gra
                        Random generator = new Random();
                        seed = generator.nextLong();
                        GameEngine.SEED = seed;

                    }
                }).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int which) {

                        //utworzenie pokoju i dodanie siebie jako gracz 1
                        button.setEnabled(false);

                        //pobranie daty
                        LocalDateTime myDateObj = LocalDateTime.now();
                        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                        String formattedDate = myDateObj.format(myFormatObj);
                        Log.e("After formatting", formattedDate);


                        //roomName = playerName;
                        roomName = playerName; //pokoj nazwij jako Nick level:Poziom_trudnosci

                        roomRef = database.getReference("rooms/"+roomName+"/played");
                        roomRef.setValue("player 1 play");

                        roomRef = database.getReference("rooms/"+roomName+"/player1");
                        roomRef.setValue(playerName);

                        /*
                        roomRef_difficulty = database.getReference("rooms/"+roomName+"/difficulty");
                        roomRef_difficulty.setValue(difficulty);
                        */

                        roomRef = database.getReference("rooms/"+roomName+"/seed");
                        roomRef.setValue(seed);

                        roomRef = database.getReference("rooms/"+roomName+"/difficulty");
                        roomRef.setValue(difficulty);

                        roomRef = database.getReference("rooms/"+roomName+"/time");
                        roomRef.setValue(formattedDate);


                        addRoomEventListener();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false) //uniemożliwia nacisniecia poza box
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {

        if(exitOnce){

            /*
            //usuniecie rekordu gracza
            database.getReference("rooms") //usuniecie gracza z rekordu "players"
                    .child(playerName)
                    .removeValue();
           */

            finish();
            startActivity((new Intent(getApplicationContext(),MainMenuActivity.class)));

            exitOnce=false;
        }

    }

    private void addRoomEventListener(){
      roomListener = roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChild("remove")){
                    //dołaczenie do pokoju
                    button.setText("GET READY");
                    button.setEnabled(true);

                    if(exitOnce){
                        Intent intent = new Intent(getApplicationContext(), GameActivity.class);

                        intent.putExtra("roomName", roomName);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        exitOnce=false;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              /*
                //bład
                button.setText("GET READY");
                button.setEnabled(true);
                new CustomToast(getApplicationContext()).showToast("Error"); */
            }
        });
    }

    private void addRoomsEventListener(){
        roomRef = database.getReference("rooms");
      roomsListener = roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //wyczysc listy
                roomsList.clear();
                seedList.clear();

                //pozwol na tworzenie wyzwania
                button.setEnabled(true);


                //Dodaj liste pokoi z bazy danych
                Iterable<DataSnapshot> rooms = snapshot.getChildren();
                for(DataSnapshot s:rooms){

                    if(s.hasChild("player1")){ //jezeli juz utworzylismy wyzwanie, nie mozemy utworzyc kolejnego, dopoki ktos go nie rozegra.
                        if(s.child("player1").getValue().equals(playerName)){
                            button.setEnabled(false);
                        }
                    }

                    if(s.hasChild("remove")){ //jezeli pokoj jest do usuniecia

                        playersRef = database.getReference("players");
                        int player1Score=0;
                        int player2Score=0;
                        if(s.hasChild("player1-score"))
                        player1Score=Integer.parseInt(s.child("player1-score").getValue().toString()); //pobranie punktow gracza 1 i przemielenie tego na inta

                        if(s.hasChild("player2-score"))
                        player2Score=Integer.parseInt(s.child("player2-score").getValue().toString()); //pobranie punktow gracza 2 i przemielenie tego na inta

                        if(player1Score>player2Score){ //jezeli gracz 1 zdobyl wiecej punktow
                            int score = (player1Score-player2Score)*(Integer.parseInt(s.child("difficulty").getValue().toString())+1)+10;

                            String newPosition = s.getKey()+s.child("time").getValue().toString();

                            playersRef.child(
                                            s.child("player1").getValue().toString() //pobierz nazwe uzytkownika 1
                                            ).child(newPosition).setValue(score); //dodaj punkty

                            winningPlayer =  s.child("player1").getValue().toString(); //ustaw nick gracza 2 jako wygranego.

                        }

                        if(player2Score>player1Score){ //jezeli gracz 2 zdobyl wiecej punktow

                            int score = (player2Score-player1Score)*(Integer.parseInt(s.child("difficulty").getValue().toString())+1)+10;

                            String newPosition = s.getKey()+s.child("time").getValue().toString();

                            playersRef.child(
                                             s.child("player2").getValue().toString() //pobierz nazwe uzytkownika 1
                                            ).child(newPosition).setValue(score); //dodaj punkty

                        }
                        /*
                        if(player1Score==player2Score&&player1Score!=0) //jezeli mieli taki sam czas, i ten czas jest rozny od 0 (czyli wgl zagrali jakos sensownie)
                        {
                            int score = 10;

                            playersRef.child(
                                    s.child("player1").getValue().toString() //pobierz nazwe uzytkownika 1
                            ).child("scoreToAdd").setValue(score); //dodaj mu 10 punktow za rozegranie meczu

                            playersRef.child(
                                    s.child("player2").getValue().toString() //pobierz nazwe uzytkownika 1
                            ).child("scoreToAdd").setValue(score); //dodaj mu 10 punktow za rozegranie meczu
                        }
                         */

                        s.getRef().removeValue(); //usun pusty pokoj.
                    }

                    if(!s.hasChild("remove")&&!s.hasChild("played")&&!s.getKey().equals(playerName)){ //jezeli pokoj nie jest do usuniecia i nie jest rozgrywany przez osobe tworzaca pokoj i nie jest to pokoj stworzony przez nas
                            Log.e("Rooms:",s.getKey());
                            roomsList.add(s.getKey()+" difficulty: "+s.child("difficulty").getValue().toString()); //dodanie do listy pokoju

                            Log.e("s.child(s.getKey()):",s.child("seed").getValue().toString());
                            seedList.add(s.child("seed").getValue().toString()); //dodanie do listy seeda

                        Log.e("s.child(s.getKey()):",s.child("seed").getValue().toString());
                        difficultyList.add(s.child("difficulty").getValue().toString()); //dodanie do listy poziomu trudnosci

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(ActiveUserList.this,
                                    R.layout.active_user_list, roomsList);
                            listView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            //bład
            }
        });
    }



    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //usuniecie listenerow

        if(roomListener!=null&&roomsRef!=null)
        roomRef.removeEventListener(roomListener);

        if(roomsListener!=null&&roomsRef!=null)
        roomsRef.removeEventListener(roomsListener);

        if(playersListener!=null&&playersRef!=null)
            playersRef.removeEventListener(playersListener);

        finish();
    }
}