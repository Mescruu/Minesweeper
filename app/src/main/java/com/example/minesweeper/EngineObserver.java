package com.example.minesweeper;

import android.util.Log;

import java.util.Observable;
import java.util.Observer;

public class EngineObserver implements Observer {
    private String massage;
    private GameActivity gameActivity;

    public EngineObserver(GameActivity activity) {
        massage = null;
        this.gameActivity = activity;
    }

    public void update(Observable obj, Object arg) {

           massage = (String) arg;
        if (! massage.startsWith("[") || ! massage.endsWith("]"))
            throw new IllegalArgumentException("Bad data: " + massage);
        String[] massageArray = massage.substring(1, massage.length() - 1).split(",", -1);


            if(massageArray[0].equals("userwin")){
                gameActivity.gameWin(massageArray[1]);
            }
            if(massageArray[0].equals("userlost")){
                gameActivity.gameLost(massageArray[1]);
            }

                System.out.println("NameObserver: massage: " + massageArray[0]);

    }
}