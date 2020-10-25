package com.example.minesweeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RankActivity extends AppCompatActivity {

    ListView simpleList;

    ArrayList<String> sortedKeys;

    ArrayList<String> Users = new ArrayList<String>();
    ArrayList<String> Scores = new ArrayList<String>();

    HashMap<String, Integer> Users_Scores = new HashMap<String, Integer>(); //mapa z nazwami uzytkownikow i ich punktami



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get datasnapshot at your "users" root node
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("players");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        collectPhoneNumbers((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    public void onBackPressed() {

        Intent intent = new Intent(RankActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void collectPhoneNumbers(Map<String,Object> users) {

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()){
            int user_score=0;

            //poszczegolny uzytkownik
            Map singleUser = (Map) entry.getValue();

            //Log.e("user",entry.getKey());
            Users.add(entry.getKey());//dodanie do listy uzytkownikow danego uzytkownika

            for (Object key: singleUser.keySet()) {
                System.out.println("key : " + key);
                System.out.println("score : " + Integer.parseInt(singleUser.get(key).toString()));
                System.out.println("---------");

                //Log.e("value : ",singleUser.get(key).toString());
                user_score+=Integer.parseInt(singleUser.get(key).toString());//dodanie do ogólnych punktów, punktów z danego rekordu.
            }
            Scores.add(String.valueOf(user_score));

            Users_Scores.put(entry.getKey(),user_score);//dodanie do mapy nazwe uzytkownika i ilosc punktów

        }

        sortedKeys =
                new ArrayList<String>(Users_Scores.keySet());

        //sortowanie w odwrotnej kolejnosci
        Collections.sort(sortedKeys, Collections.reverseOrder());

        //deklaracja tablic typu STRING
        int size = sortedKeys.size();
        String loginList[] = new String[size];
        String rankList[] = new String[size];
        Integer scores[] = new Integer[size];

        int i =0;
        // wyswietlenie posortowanej mapy i dodanie do tablicy
        for (String x : sortedKeys){
            loginList[i]=x.toString();
            scores[i]=Integer.parseInt(Users_Scores.get(x).toString());

            System.out.println("Key = " + x +
                    ", Value = " + Users_Scores.get(x));
            i++;
        }

        bubbleSort(loginList, scores);

        for (i = 0; i < scores.length; i++)
            rankList[i] = String.valueOf(scores[i]);

        //wywołanie widoku listy
        setContentView(R.layout.activity_rank);
        simpleList = (ListView) findViewById(R.id.simpleListView);

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), rankList, loginList);
        simpleList.setAdapter(customAdapter);
    }

    void bubbleSort(String logins[],Integer scores[])
    {
        int n = logins.length;
        for (int i = 0; i < n-1; i++){
            for (int j = 0; j < n-i-1; j++)
                if (scores[j] < scores[j+1])
                {
                    int temp = scores[j];
                    scores[j] = scores[j+1];
                    scores[j+1] = temp;

                    String tempString = logins[j];
                    logins[j] = logins[j+1];
                    logins[j+1] = tempString;
                }
        }
    }

}

