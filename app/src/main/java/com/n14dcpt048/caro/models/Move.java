package com.n14dcpt048.caro.models;

import java.util.Objects;

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
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move mv = (Move) obj;
            return (rowIndex == mv.rowIndex) && (colIndex == mv.colIndex);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Move{" +
                "rowIndex=" + rowIndex +
                ", colIndex=" + colIndex +
                '}';
    }
}
