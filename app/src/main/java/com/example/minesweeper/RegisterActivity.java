package com.example.minesweeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.minesweeper.common.CustomToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {

    EditText emailId, password, nick;
    Button btnSignUp, btnSignIn;
    FirebaseAuth mFirebaseAuth;
    String name;
    String email;
    String pwd;

    boolean errors;
    CustomToast customToast;

    ImageView loadingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //animacja ladowania:
        loadingImage = (ImageView)findViewById(R.id.loadingAnimation);
        loadingImage.setBackgroundResource(R.drawable.loading_animation);
        loadingImage.setVisibility(View.INVISIBLE);

        //tworzenie obiektu customToast
        customToast = new CustomToast(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        nick = findViewById(R.id.editTextTextNick);

        //zarejestruj sie
        btnSignUp = findViewById(R.id.SignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                errors = false;

               email = emailId.getText().toString();
                pwd = password.getText().toString();
                name = nick.getText().toString();

                if(email.isEmpty()){
                    emailId.setError("Please enter email!");
                    emailId.requestFocus();
                    errors=true;
                }
                //sprwadzenie czy wypełnił pola
                if(pwd.isEmpty()){
                    password.setError("Please enter password!");
                    password.requestFocus();
                    errors=true;
                }

                //sprwadzenie czy wypełnił pola
                if(name.isEmpty()){
                    nick.setError("Please enter nick!");
                    nick.requestFocus();
                    errors=true;
                }else{
                    FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                    DatabaseReference playerRef =  firebaseDatabase.getReference("players");
                    playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Sprawdz każdego użytkownika
                            Iterable<DataSnapshot> players = snapshot.getChildren();
                            for(DataSnapshot player:players) {
                            if(player.getKey().toString().equals(name)){//jezeli istnieje rekord o takiej nazwie jaka wpisuje nowy uzytkownik
                                errors=true;
                                customToast.showToast("This username is taken, try another one!");
                            }

                            }

                            if(errors==false){
                                registerUser();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

                if(errors==true){
                    //wyswietl powiadomienie z bledami
                    customToast.showToast("Fill the fields!");
                    loadingImage.setVisibility(View.INVISIBLE);
                }
            }
        });

        //przejdz do logowania

        btnSignIn = findViewById(R.id.SignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private  void registerUser(){
        loadingImage.setVisibility(View.VISIBLE);
        // Get the background, which has been compiled to an AnimationDrawable object.
        AnimationDrawable frameAnimation = (AnimationDrawable) loadingImage.getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();


        //zarejestrowanie uzytkownika
        mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //jezeli cos poszlo nie tak
                if(!task.isSuccessful()){

                    //wyswietl powiadomienie, ze cos poszlo nie tak.
                    customToast.showToast("Problem with singing in, please try again");
                    loadingImage.setVisibility(View.INVISIBLE);

                }else{ //wszystko poszlo dobrze
                    //rozpocznij dalsza aktwynosc

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name) //ustawienie nazwy
                            .build();

                    //w momencie gdy nazwa gracza została dodana
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        //przy tworzeniu gracza dodatkowo ustaw jego rekord w bazie (dla rankingu)
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        database.getReference("players/"+name+"/Start Value").setValue("0");

                                        startActivity((new Intent(RegisterActivity.this, MainMenuActivity.class)));
                                        loadingImage.setVisibility(View.INVISIBLE);

                                    }
                                }
                            });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                customToast.showToast(e.getLocalizedMessage());
            }});
    }
}