package com.example.minesweeper.views.grid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat; //Klasa ContextCompat jest używana, gdy chcesz pobrać zasoby, takie jak rysowalne lub kolor,
                                            // bez zawracania sobie głowy motywem. Zapewnia jednolity interfejs dostępu do zasobów i zapewnia kompatybilność wsteczną.

import com.example.minesweeper.GameEngine; //zaimportowanie klasy silnika gry
import com.example.minesweeper.R; //

//clasa Cell rozszerza klasę BaseCell
public class Cell extends BaseCell implements View.OnClickListener , View.OnLongClickListener {

    //konsturktor
    public Cell(Context context, int x, int y) {
        //odwolanie sie do nadklasy - klasy nad daną klasą w której jesteśmy
        super(context);

        //
        setPosition(x, y);

        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override //zwykle nacisniecie pola
    public void onClick(View v) {
        GameEngine.getInstance().click(getXPos(), getYPos()); //wywołanie informacji, że zostało klikniete to pole do silnika gry
    }

    @Override //przy dluzszym przytrzymaniu pola
    public boolean onLongClick(View v) {
        GameEngine.getInstance().flag(getXPos(), getYPos()); //wywolanie informacji, że zostało dluugo kliknięte to pole do silnika gry

        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {  //rysowanie odpowiednich kwadratów

        super.onDraw(canvas); //odwolanie sie do klasy nadrzędnej

        Log.d("Minesweeper", "Cell::onDraw"); //log rysowania pol


        drawButton(canvas); //narysuj przycisk


        if (isFlagged()) { //jezeli zaflagowana
            drawFlag(canvas);  //narysuj flate
        } else if (isRevealed() && isBomb() && !isClicked()) {
            drawNormalBomb(canvas); //jezeli jest pole odkryte i jest bomba i nie jest klikniete - narysuj zwykla bombe
        } else {
            if (isClicked()) { //jezeli jest klikniete i jest to bomba
                if (getValue() == -1) {
                    drawBombExploded(canvas); //narysuj eksplodujaca bombe
                } else {
                    drawNumber(canvas); //jezeli nie jest bomba wyswietl numer bomb w poblizu
                }
            } else { //jezeli nie jest klikniete narysuj przycisk do klikniecia
                drawButton(canvas);
            }
        }
    }

    //rysowanie odpowiedniego pola
    private void drawBombExploded(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.bomb_exploded); //pobranie obrazka
        drawable.setBounds(0, 0, getWidth(), getHeight()); //Rozmiar elementu i pozycja  - Pierwsze dwa argumenty x i Y z górnym lewym rogu  elementu, trzeci argument jest szerokość  elementu, a czwarty jest wysokość  elementu.
        drawable.draw(canvas); //rysowanie na płótnie
    }

    //rysowanie odpowiedniego pola
    private void drawFlag(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.flag);
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    //rysowanie odpowiedniego pola
    private void drawButton(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.button);
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    //rysowanie odpowiedniego pola
    private void drawNormalBomb(Canvas canvas) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.bomb_normal);
        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }

    //rysowanie odpowiedniego pola
    private void drawNumber(Canvas canvas) {
        Drawable drawable = null;

        switch (getValue()) {
            case 0:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.number_0);
                break;
            case 1:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.number_1);
                break;
            case 2:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.number_2);
                break;
            case 3:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.number_3);
                break;
            case 4:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.number_4);
                break;
            case 5:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.number_5);
                break;
            case 6:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.number_6);
                break;
            case 7:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.number_7);
                break;
            case 8:
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.number_8);
                break;
        }

        drawable.setBounds(0, 0, getWidth(), getHeight());
        drawable.draw(canvas);
    }
}

