package com.example.minesweeper;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minesweeper.common.CustomToast;
import com.example.minesweeper.util.Generator; //zaimportowany generator tworzenia siatki
import com.example.minesweeper.util.PrintGrid; //zaimportowana klasa tworzaca log mapy
import com.example.minesweeper.views.grid.Cell;//zaimportowana klasa pola gry

import java.util.Observable; //klasa umożliwiajaca bycie obserwowanym przez inny obiekt
import java.util.Random;


public class GameEngine extends Observable {

    private static GameEngine instance;

    public static int BOMB_NUMBER; //ilosc bomb
    public static int WIDTH; //rozmiar siatki - szerokość     5/10/12
    public static int HEIGHT;//rozmiar siatki - wysokość      5/10/12
    public static Long SEED=100L;


    //zegar
    private CountDownTimer countDownTimer;
    private boolean timeRunning;
    private long timeLeft = 60000; //czas w milisekundach - metoda wymaga long (60 000 - 1 minut)
    public long timeLeftStartValue = 60000; //czas w milisekundach - metoda wymaga long (60 000 - 1 minut)
    private TextView countdownText; //wyswietlanie zegara

    private boolean playerInGame; //true - gracz moze wykonywac ruchy, false - gracz przegrał, bądź skończył grę.

    //CustomToast obiekt
    public CustomToast customToast;

    private Context context; //przechowywanie obiektów tekstu itd.

    private Cell[][] MinesweeperGrid = new Cell[WIDTH][HEIGHT]; //nowy typ pol z szerokoscia i dlugoscia


    public static GameEngine getInstance() {
        if( instance == null ){
            instance = new GameEngine();
        }

        //tworzenie obiektu customToast
        return instance;
    }

    private GameEngine(){ }


    public void createGrid(Context context){ //tworzenie siatki

        playerInGame=true;//gracz moze wykonywac ruchy

        //utworzenie powiadomień
        customToast = new CustomToast(context);
        customToast.showToast("New game started");

        Log.e("player in game", ""+playerInGame);

        this.context = context; //dodanie go do kontekstu

        // create the grid and store it
        int[][] GeneratedGrid = Generator.generate(BOMB_NUMBER,WIDTH, HEIGHT);

        PrintGrid.print(GeneratedGrid,WIDTH,HEIGHT); //wyswietlenie siatki w logach

        setGrid(context,GeneratedGrid); //ustawienie siatki
    }

    //zainicjowanie wszystkich pól (cell) gry
    private void setGrid( final Context context, final int[][] grid ){
       //dla każdego pola (cell)
        for( int x = 0 ; x < WIDTH ; x++ ){
            for( int y = 0 ; y < HEIGHT ; y++ ){

                //utworzenie nowego pola
                if( MinesweeperGrid[x][y] == null ){
                    MinesweeperGrid[x][y] = new Cell( context , x,y);
                }

                MinesweeperGrid[x][y].setValue(grid[x][y]); //ustaw wartość pola
                MinesweeperGrid[x][y].invalidate(); //przerysuj obraz
            }
        }
    }

//
    public Cell getCellAt(int position) {
        int x = position % WIDTH;
        int y = position / WIDTH;

        return MinesweeperGrid[x][y];
    }
    public Cell getCellAt( int x , int y ){ //pobierz pole (cell) o tej pozycji
        return MinesweeperGrid[x][y];
    }

    public void click( int x , int y ){

        if(playerInGame){

            if( x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT && !getCellAt(x,y).isClicked() ){ //jezeli pole zostało kliknięte
                getCellAt(x,y).setClicked(); //ustaw pole jako kliknięte

                if( getCellAt(x,y).getValue() == 0 ){ //pobierz pole i sprawdz jego wartość, czy jest zwykłym polem (nie mina i nie sąsiedztwo)

                    for( int xt = -1 ; xt <= 1 ; xt++ ){ //
                        for( int yt = -1 ; yt <= 1 ; yt++){
                            if( xt != yt ){ //jezeli to nie jest to samo pole co wcześniej
                                click(x + xt , y + yt);  //kliknij w pola obok, żeby wywołać planszę
                            }
                        }
                    }
                }

                //jezeli pole to bomba to koniec gry
                if( getCellAt(x,y).isBomb() ){
                    onGameLost(false); //wywolanie funkcji koniec gry
                    playerInGame=false;
                }else{
                    long bonusTime = getCellAt(x,y).getValue()*1000; //dodawanie 1 sekundy za "punkt"
                    //customToast.showToast("seconds added");
                    windUpTheClock(countdownText, bonusTime);
                }
            }

        }
    if(playerInGame)
        checkEnd();
    }

    private boolean checkEnd(){
        int bombNotFound = BOMB_NUMBER;
        int notRevealed = WIDTH * HEIGHT;
        for ( int x = 0 ; x < WIDTH ; x++ ){
            for( int y = 0 ; y < HEIGHT ; y++ ){
                if( getCellAt(x,y).isRevealed() || getCellAt(x,y).isFlagged() ){
                    notRevealed--;
                }

                if( getCellAt(x,y).isFlagged() && getCellAt(x,y).isBomb() ){
                    bombNotFound--;
                }
            }
        }

        if( bombNotFound == 0 && notRevealed == 0 ){
            customToast.showToast("Game won!");
            stopTimer();

            //wprowadz zmiane
            setChanged();
            //poinformuj obserwatora
            Object massage = "[userwin,"+timeLeft/1000+"]";//punkty = sekundy

            notifyObservers(massage);

        }
        return false;
    }

    //oflagowanie pola (cell)
    public void flag( int x , int y ){
        boolean isFlagged = getCellAt(x,y).isFlagged(); //jakie jest oflagowanie?
        getCellAt(x,y).setFlagged(!isFlagged); // ustaw oflagowanie na przeciwne
        getCellAt(x,y).invalidate(); //przerysuj mape gry
    }

    private void onGameLost(boolean restart){
        customToast.showToast("Game lost!");

        //przejmij koenic gry
        //wyswietl komunikat o końcu gry
        playerInGame=false;//gracz nie może wykonywac ruchy
        stopTimer(); //zatrzymaj zegar

        //wprowadz zmiane
        setChanged();

        for ( int x = 0 ; x < WIDTH ; x++ ) {
            for (int y = 0; y < HEIGHT; y++) {
                getCellAt(x,y).setRevealed();
            }
        }

        //poinformuj obserwatora
        Object massage = "[userlost,"+timeLeft/1000+"]"; //punkty = sekundy
        notifyObservers(massage);

        /*
        if(!restart){
                //Opóźnienie
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    //po 5 sekundach wyświetlenie rozpoczęcie nowej gry


                    }
            }, 1000);   //1 seconds
        } */
    }

    public void stopGame(){
        onGameLost(true);
    }


    /*TIMER*/
    public void windUpTheClock(TextView countdownText){
        timeLeft=timeLeftStartValue;
        windUpTheClock(countdownText,0);
    }

    //podkrecenie zegara
    public void windUpTheClock(TextView countdownText, long timeLeftbonus){
        this.countdownText = countdownText;
        if(timeRunning)//jezeli zegar odlicza - zatrzymaj go
            stopTimer(); //zatrzymanie zegara

        timeLeft+=timeLeftbonus;
        StartTimer(); //uruchomienie zegara
        UpdateTimer(); //update zegara
    }


    private void StartTimer(){
        //Utworzenie timera ze zmienną początkową i interwałem - 1000 oznacza milisekundy
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                UpdateTimer();
            }

            @Override
            public void onFinish() { //w momencie kiedy czas się skończy.
                String FinishText = "Time left!";
                Log.e("",FinishText);
                countdownText.setText(FinishText);
                onGameLost(false);
            }
        }.start();

        timeRunning=true; //bool informujacy, czy zegar pracuje
    }

    public void stopTimer(){ //funkcja zatrzymujaca zegar
        countDownTimer.cancel();
        timeRunning = false;
    }

    private   void UpdateTimer(){
        int minutes = (int) timeLeft / 60000; //przerabiamy czas na minuty
        int seconds = (int) timeLeft % 60000 /1000;

        String TimeleftText;
        TimeleftText = ""+minutes;
        TimeleftText +=":";

        if(seconds<10) TimeleftText+="0"; //zeby pokazywac zero przed jednosciami

        TimeleftText +=seconds;

        Log.e("",TimeleftText);
        Log.e("player in game", ""+playerInGame);

        countdownText.setText(TimeleftText); //ustawienie czasu

        if(timeLeft<=0) //jezeli czas sie skonczył
            stopTimer(); //zakoncz czas
    }

}
