package com.example.minesweeper.views.grid;

import android.content.Context;
import android.view.View;

import com.example.minesweeper.GameEngine; //zaimportowany silnik gry

//abstract - nie można zainicjować
public abstract class BaseCell extends View {

    private int value; //wawrtosc pola "1", "0", "-1" //-1 bomba
    private boolean isBomb; //czy to pole jest bombą
    private boolean isRevealed; //czy pole jest odkryte
    private boolean isClicked; //czy jest klikniete
    private boolean isFlagged; //czy jest zaflagowane

    private int x , y; //pozycja pola
    private int position; //pozycja w grze?

    //konstruktor - umozliwia dziedziczenie po widoku, do tworzenia pól
    public BaseCell(Context context ){
        super(context);
    }

    public int getValue() {
        return value;
    } //pobiera wartość pola

    public void setValue(int value) { //ustawienie wartosci

        //ustawienie wartosci poczatkowych
        isBomb = false;
        isRevealed = false;
        isClicked = false;
        isFlagged = false;

        //jezeli wartosc jest =-1, ustaw pole jako bomba
        if( value == -1 ){
            isBomb = true;
        }

        //ustaw wartosc pola
        this.value = value;
    }

    //czy pole jest bomba?
    public boolean isBomb() {
        return isBomb;
    }

    //ustaw pole jako bomba
    public void setBomb(boolean bomb) {
        isBomb = bomb;
    }

    //czy pole jest odkryte
    public boolean isRevealed() {
        return isRevealed;
    }

    //ustaw jako odkryte
    public void setRevealed() {
        isRevealed = true;
        invalidate(); //funkcja zmuszajaca do przerysowac obraz
    }

    //czy jest klikniete
    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked() {
        this.isClicked = true;
        this.isRevealed = true;

        invalidate(); //funkcja zmuszajaca do przerysowac obraz
    }

    //czy pole jest zaflagowane
    public boolean isFlagged() {
        return isFlagged;
    }

    //ustaw pole jako zaflagowane
    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    //pobierz wartosc X pola
    public int getXPos() {
        return x;
    }

    //pobierz wartosc y pola
    public int getYPos() {
        return y;
    }

    public int getPosition() {
        return position;
    }

    //ustawienie pozycji pola
    public void setPosition( int x , int y ){
        this.x = x; //pozycja pola
        this.y = y; //pozycja pola

        this.position = y * GameEngine.WIDTH + x; //pozycja w grze


        invalidate(); //funkcja zmuszajaca do przerysowac obraz
    }

}
