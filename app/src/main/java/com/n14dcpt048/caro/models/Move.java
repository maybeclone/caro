package com.n14dcpt048.caro.models;

/**
 * Created by silent on 4/11/2018.
 */
public class Move {
    public int rowIndex;
    public int colIndex;

    public Move(int rowIndex, int colIndex){
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
    }

    @Override
    public String toString() {
        return "Move{" +
                "rowIndex=" + rowIndex +
                ", colIndex=" + colIndex +
                '}';
    }
}
