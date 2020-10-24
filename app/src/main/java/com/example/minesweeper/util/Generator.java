package com.example.minesweeper.util;


import com.example.minesweeper.GameEngine;

import java.util.Random;

public class Generator {

    public static int[][] generate( int bombnumber , final int width , final int height){ //metoda generująca bomby
        int [][] grid = new int[width][height]; //tablica z siatką

        // Random for generating numbers
        Random r = new Random(GameEngine.SEED); //randomowa liczba służąca do generowania pól z minami

        //Tworzenie dwu-wymiarowej tablicy, która posłuży za siatkę
        for( int x = 0 ; x< width ;x++ ){
            grid[x] = new int[height];
        }

        //ustawienie bomb na planszy
        while( bombnumber > 0 ){
            int x = r.nextInt(width);
            int y = r.nextInt(height);

            //jezeli nie jest bomba
            if( grid[x][y] != -1 ){
                grid[x][y] = -1; //ustaw bombe w tej pozycji
                bombnumber--; //zmniejsz liczbę dostępnych bomb
            }
        }
        //obliczenie pol z liczbą bomb w okolicy.
        grid = calculateNeigbours(grid,width,height);

        return grid;
    }

    //final poniewaz nie zmienia się
    private static int[][] calculateNeigbours( int[][] grid , final int width , final int height){

        //ile bomb jest w okolicy
        for( int x = 0 ; x < width ; x++){
            for( int y = 0 ; y < height ; y++){
                grid[x][y] = getNeighbourNumber(grid,x,y,width,height);
            }
        }

        return grid;
    }

    private static int getNeighbourNumber( final int grid[][] , final int x , final int y , final int width , final int height){
        if( grid[x][y] == -1 ){ //jezeli w danym miejscu znajduje się bomba, przerwij wykonywanie funkcji z -1 (czyli "tutaj jest bomba")
            return -1;
        }

        //na poczatku ustawiamy 0
        int count = 0;

        //jeżeli w tym miejscu jest bomba to dodaj do licznika +1.  (obliczanie liczby bomb w okolicy pola, który nie jest bombą)
        if( isMineAt(grid,x - 1 ,y + 1,width,height)) count++; // top-left
        if( isMineAt(grid,x     ,y + 1,width,height)) count++; // top
        if( isMineAt(grid,x + 1 ,y + 1,width,height)) count++; // top-right
        if( isMineAt(grid,x - 1 ,y    ,width,height)) count++; // left
        if( isMineAt(grid,x + 1 ,y    ,width,height)) count++; // right
        if( isMineAt(grid,x - 1 ,y - 1,width,height)) count++; // bottom-left
        if( isMineAt(grid,x     ,y - 1,width,height)) count++; // bottom
        if( isMineAt(grid,x + 1 ,y - 1,width,height)) count++; // bottom-right

        return count;
    }

    //funkcja sprawdzajaca, czy
    private static boolean isMineAt( final int [][] grid, final int x , final int y , final int width , final int height){
       //sprawdzamy, czy jest podana zmienna jest (x) oraz (y) są w tablicy
        if( x >= 0 && y >= 0 && x < width && y < height ){
            if( grid[x][y] == -1 ){ //czy to jest bomba?
                return true; //tak to bomba
            }
        }
        return false;
    }

}
