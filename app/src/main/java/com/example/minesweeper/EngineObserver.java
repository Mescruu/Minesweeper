package com.example.minesweeper;

import android.util.Log;

import java.util.Observable;
import java.util.Observer;

public class EngineObserver implements Observer {
    private String massage;
    private GameActivity gameActivity; //gra multi
    private GameActivitySolo gameActivitySolo; //gra solo

    //jezeli wywolala to gra multi
    public EngineObserver(GameActivity activity) {
        massage = null;
        this.gameActivity = activity;
    }

    //jezeli wywolala to gra solowa
    public EngineObserver(GameActivitySolo activity) {
        massage = null;
        this.gameActivitySolo = activity;
    }

    public void update(Observable obj, Object arg) {

           massage = (String) arg;
        if (! massage.startsWith("[") || ! massage.endsWith("]"))
            throw new IllegalArgumentException("Bad data: " + massage);
        String[] massageArray = massage.substring(1, massage.length() - 1).split(",", -1);

            if(massageArray[0].equals("userwin")){
                if(gameActivity==null){ //jezeli to gra solowa
                    gameActivitySolo.gameWin(massageArray[1]);
                }else{//jezeli to gra multi
                    gameActivity.gameWin(massageArray[1]);
                }
            }
            if(massageArray[0].equals("userlost")){
                if(gameActivity==null){ //jezeli to gra solowa
                    gameActivitySolo.gameLost(massageArray[1]);
                }else{ //jezeli to gra multi
                    gameActivity.gameLost(massageArray[1]);
                }
            }

                System.out.println("NameObserver: massage: " + massageArray[0]);

    }
}