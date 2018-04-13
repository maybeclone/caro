package com.n14dcpt048.caro.negamaxing;

import android.util.Log;

import com.n14dcpt048.caro.broad.ChessBoard;
import com.n14dcpt048.caro.models.Move;
import com.n14dcpt048.caro.models.Record;

/**
 * Created by silent on 4/11/2018.
 */
public class Negamaxing {

    private ChessBoard chessBoard;

    public Record negamaxing(ChessBoard chessBoard, int maxDept, int currentDept, int alpha, int beta){

        if(chessBoard.isGameOver() || currentDept == maxDept){
            return new Record(null, chessBoard.evaluate());
        }

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        ChessBoard newBoard;
        for(Move move : chessBoard.getMoves()){
            newBoard = new ChessBoard(chessBoard.getContext(), chessBoard.getBitmapWidth(),
                    chessBoard.getBitmapHeight(), chessBoard.getColQty(), chessBoard.getRowQty());
            newBoard.setBoard(chessBoard.getBoard());
            newBoard.setCurrentPlayer(chessBoard.currentPlayer());
            newBoard.makeMove(move);
            Record record = negamaxing(newBoard, maxDept, currentDept+1, -beta, -Math.max(alpha, bestScore));
            int currentScore = -record.score;
            if(currentScore > bestScore){
                bestScore = currentScore;
                bestMove = move;
                if(bestScore >= beta){
                    return new Record(bestMove, bestScore);
                }
            }
        }
        return new Record(bestMove, bestScore);
    }

}
