package com.example.minesweeper;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.minesweeper.util.Generator; //zaimportowany generator tworzenia siatki
import com.example.minesweeper.util.PrintGrid; //zaimportowana klasa tworzaca log mapy
import com.example.minesweeper.views.grid.Cell;//zaimportowana klasa pola gry

public class GameEngine {

    private static GameEngine instance;

    public static final int BOMB_NUMBER = 10; //ilosc bomb
    public static final int WIDTH = 5; //rozmiar siatki - szerokość
    public static final int HEIGHT = 10;//rozmiar siatki - wysokość

    private Context context; //przechowywanie obiektów tekstu itd.

    private Cell[][] MinesweeperGrid = new Cell[WIDTH][HEIGHT]; //nowy typ pol z szerokoscia i dlugoscia

    public static GameEngine getInstance() {
        if( instance == null ){
            instance = new GameEngine();
        }
        return instance;
    }

    private GameEngine(){ }


    public void createGrid(Context context){ //tworzenie siatki
        Log.e("GameEngine","createGrid is working");
        this.context = context; //dodanie go do kontekstu

        // create the grid and store it
        int[][] GeneratedGrid = Generator.generate(BOMB_NUMBER,WIDTH, HEIGHT);

        PrintGrid.print(GeneratedGrid,WIDTH,HEIGHT); //wyswietlenie siatki

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
                onGameLost(); //wywolanie funkcji koniec gry
            }
        }

        checkEnd(); //
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
            Toast.makeText(context,"Game won", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //oflagowanie pola (cell)
    public void flag( int x , int y ){
        boolean isFlagged = getCellAt(x,y).isFlagged(); //jakie jest oflagowanie?
        getCellAt(x,y).setFlagged(!isFlagged); // ustaw oflagowanie na przeciwne
        getCellAt(x,y).invalidate(); //przerysuj mape gry
    }

    private void onGameLost(){
        //przejmij koenic gry
        //wyswietl komunikat o końcu gry
        Toast.makeText(context,"Game lost", Toast.LENGTH_SHORT).show();

        for ( int x = 0 ; x < WIDTH ; x++ ) {
            for (int y = 0; y < HEIGHT; y++) {
                getCellAt(x,y).setRevealed();
            }
        }
    }
}
