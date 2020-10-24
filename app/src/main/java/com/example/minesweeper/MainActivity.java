package com.example.minesweeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.minesweeper.common.CustomToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {


    EditText emailId, password;
    Button btnSignUp, btnSignIn;
    FirebaseAuth mFirebaseAuth;

    ImageView loadingImage;

    CustomToast customToast;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //animacja ladowania:
        loadingImage = (ImageView)findViewById(R.id.loadingAnimation);
        loadingImage.setBackgroundResource(R.drawable.loading_animation);
        loadingImage.setVisibility(View.INVISIBLE);


        //tworzenie obiektu customToast
        customToast = new CustomToast(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);

        long info;
        info = 100L;

        String longText = String.valueOf(info);
        Log.e("Text", longText);

        Long longNumber = Long.parseLong(longText);
        Log.e("Number", ""+longNumber);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
               @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                   FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

                   if(mFirebaseUser != null){
                    //wyswietl info, ze user jest zalogowany juz
                       Intent intent = new Intent(MainActivity.this, MainMenuActivity.class);
                       startActivity(intent);
                   }else{
                       //wyswietl info, ze prosimy uzytkownika o zalogowanie sie
                       customToast.showToast("Please sign in first!");
                   }

            }
        };

        //przejdz do aktywnosci z logowaniem sie
        btnSignUp = findViewById(R.id.SignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //zarejestruj sie
        btnSignIn = findViewById(R.id.SignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean errors = false;

                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
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



                if(errors==false){


                    loadingImage.setVisibility(View.VISIBLE);
                    // Get the background, which has been compiled to an AnimationDrawable object.
                    AnimationDrawable frameAnimation = (AnimationDrawable) loadingImage.getBackground();

                    // Start the animation (looped playback by default).
                    frameAnimation.start();

                    //zarejestrowanie uzytkownika
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {



                            //jezeli cos poszlo nie tak
                            if(!task.isSuccessful()){

                                loadingImage.setVisibility(View.INVISIBLE);

                                //wyswietl powiadomienie, ze cos poszlo nie tak.
                                customToast.showToast("Problem with logging in, please try again");
                            }else{ //wszystko poszlo dobrze
                                //rozpocznij dalsza aktwynosc
                                startActivity((new Intent(MainActivity.this, MainMenuActivity.class)));
                            }
                        }
                    });
                }else{
                    //wyswietl powiadomienie z bledami
                    customToast.showToast("Fill the fields!");
                    loadingImage.setVisibility(View.INVISIBLE);
                }
            }
        });

    }
}