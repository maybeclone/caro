package com.n14dcpt048.caro.negamaxing;

import com.n14dcpt048.caro.broad.ChessBoard;
import com.n14dcpt048.caro.models.Move;
import com.n14dcpt048.caro.models.Record;

/**
 * Created by silent on 4/11/2018.
 */
public class Negamaxing {

    private ChessBoard chessBoard;

    public Record abNegamaxing(ChessBoard chessBoard, int maxDept, int currentDept, int alpha, int beta){

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

            int newAlpha;
            int newBeta;

            if (alpha == Integer.MIN_VALUE) {
                newBeta = Integer.MAX_VALUE;
            } else if (alpha == Integer.MAX_VALUE) {
                newBeta = Integer.MIN_VALUE;
            } else {
                newBeta = -alpha;
            }

            if (beta == Integer.MIN_VALUE) {
                newAlpha = Integer.MAX_VALUE;
            } else if (beta == Integer.MAX_VALUE) {
                newAlpha = Integer.MIN_VALUE;
            } else {
                newAlpha = -beta;
            }

            Record record = abNegamaxing(newBoard, maxDept, currentDept+1, newAlpha, newBeta);
            int currentScore = -record.score;

            if(currentScore > bestScore){
                bestScore = currentScore;
                bestMove = move;
            }


            alpha = Math.max(alpha, currentScore);

            if (alpha >= beta) {
                return new Record(bestMove, bestScore);
            }
        }
        return new Record(bestMove, bestScore);
    }

    public Record negamaxing(ChessBoard chessBoard, int maxDept, int currentDept){

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
            Record record = negamaxing(newBoard, maxDept, currentDept+1);
            int currentScore = -record.score;
            if(currentScore > bestScore){
                bestScore = currentScore;
                bestMove = move;
            }
        }
        return new Record(bestMove, bestScore);
    }



}
