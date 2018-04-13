package com.n14dcpt048.caro.broad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.n14dcpt048.caro.callbacks.OnTouchCallback;
import com.n14dcpt048.caro.models.Line;
import com.n14dcpt048.caro.models.Move;
import com.n14dcpt048.caro.models.Record;
import com.n14dcpt048.caro.negamaxing.Negamaxing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by silent on 3/15/2018.
 */

public class ChessBoard {

    public static final int EMPTY = 0;
    public static final int PLAYER_HUMAN = 1;
    public static final int PLAYER_BOT = 2;
    public static final int COUNT_WIN = 4;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    private int[][] board;
    private int currentPlayer;
    private Context context;

    private int bitmapWidth;
    private int bitmapHeight;
    private int colQty;
    private int rowQty;

    private List<Line> lines;

    private Bitmap bitmapTick;
    private Bitmap bitmapCross;

    private Negamaxing negamaxing;

    private Move currentMove;

    private int winner;

    private OnTouchCallback callback;

    public ChessBoard(Context context, int bitmapWidth, int bitmapHeight, int colQty, int rowQty) {
        this.context = context;
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
        this.colQty = colQty;
        this.rowQty = rowQty;
        board = new int[rowQty][colQty];
        winner = -1;
        if (context instanceof OnTouchCallback) {
            this.callback = (OnTouchCallback) context;
        }
    }

    public void init() {
        bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_4444);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setStrokeWidth(2);
        currentPlayer = PLAYER_HUMAN;
        lines = new ArrayList<>();
        int celWidth = bitmapWidth / colQty;
        int celHeight = bitmapHeight / rowQty;
        for (int i = 0; i <= colQty; i++) {
            lines.add(new Line(0, i * celWidth, bitmapWidth, i * celWidth));
        }
        for (int i = 0; i <= rowQty; i++) {
            lines.add(new Line(i * celHeight, 0, i * celHeight, bitmapHeight));
        }
        bitmapTick = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_input_add);
        bitmapCross = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_delete);
        negamaxing = new Negamaxing();
    }

    public void setBoard(int[][] oldBoard) {
        for (int i = 0; i < rowQty; i++) {
            for (int j = 0; j < colQty; j++) {
                board[i][j] = oldBoard[i][j];
            }
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public Context getContext() {
        return context;
    }

    public int getColQty() {
        return colQty;
    }

    public int getRowQty() {
        return rowQty;
    }

    public int getBitmapWidth() {
        return bitmapWidth;
    }

    public int getBitmapHeight() {
        return bitmapHeight;
    }

    public void setCurrentPlayer(int player) {
        this.currentPlayer = player;
    }

    public Bitmap drawBoard() {
        for (Line line : lines) {
            canvas.drawLine(line.getStartX(), line.getStartY(), line.getStopX(), line.getStopY(), paint);
        }
        return bitmap;
    }

    public boolean onTouchPlayingWithPlayer(View view, MotionEvent event) {
        int celWidth = view.getWidth() / colQty;
        int celHeight = view.getHeight() / rowQty;

        final int celWidthBM = bitmapWidth / colQty;
        final int celHeightBM = bitmapHeight / rowQty;

        final int colIndex = (int) (event.getX() / celWidth);
        final int rowIndex = (int) (event.getY() / celHeight);

        canvas.drawBitmap(bitmapCross, new Rect(0, 0, bitmapCross.getWidth(), bitmapCross.getHeight()),
                new Rect(colIndex * celWidthBM, rowIndex * celHeightBM, (colIndex + 1) * celWidthBM, (rowIndex + 1) * celHeightBM), paint);
        view.invalidate();
        callback.onTouch(colIndex, rowIndex);
        return true;
    }

    public void onDrawOpposite(View view, int colIndex, int rowIndex){
        final int celWidthBM = bitmapWidth / colQty;
        final int celHeightBM = bitmapHeight / rowQty;
        canvas.drawBitmap(bitmapTick, new Rect(0, 0, bitmapTick.getWidth(), bitmapTick.getHeight()),
                new Rect(colIndex * celWidthBM, rowIndex * celHeightBM, (colIndex + 1) * celWidthBM, (rowIndex + 1) * celHeightBM), paint);
        view.invalidate();
    }

    public boolean onTouch(final View view, MotionEvent event) {
        int celWidth = view.getWidth() / colQty;
        int celHeight = view.getHeight() / rowQty;

        final int celWidthBM = bitmapWidth / colQty;
        final int celHeightBM = bitmapHeight / rowQty;

        final int colIndex = (int) (event.getX() / celWidth);
        final int rowIndex = (int) (event.getY() / celHeight);

        Log.i("TRUNG", "col: " + colIndex + " row: " + rowIndex);

        if (board[rowIndex][colIndex] != EMPTY) {
            return true;
        }

        makeMove(new Move(rowIndex, colIndex));

        if (currentPlayer == PLAYER_HUMAN) {
            canvas.drawBitmap(bitmapCross, new Rect(0, 0, bitmapCross.getWidth(), bitmapCross.getHeight()),
                    new Rect(colIndex * celWidthBM, rowIndex * celHeightBM, (colIndex + 1) * celWidthBM, (rowIndex + 1) * celHeightBM), paint);
        } else {
            canvas.drawBitmap(bitmapTick, new Rect(0, 0, bitmapTick.getWidth(), bitmapTick.getHeight()),
                    new Rect(colIndex * celWidthBM, rowIndex * celHeightBM, (colIndex + 1) * celWidthBM, (rowIndex + 1) * celHeightBM), paint);
        }

        view.invalidate();

        if (isGameOver()) {
            init();
            Log.i("GAME_STATUS", "over");
            return true;
        }

        int count = getCurrentDept();
        final int currentDepth = rowQty * colQty - count;

        final Record record = negamaxing.negamaxing(
                ChessBoard.this,
                rowQty * colQty,
                currentDepth,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE);

        Log.d("TRUNG", "onTouch: " + record.move.toString());

        makeMove(record.move);

        if (currentPlayer == PLAYER_HUMAN) {
            canvas.drawBitmap(bitmapCross, new Rect(0, 0, bitmapCross.getWidth(), bitmapCross.getHeight()),
                    new Rect(record.move.colIndex * celWidthBM, record.move.rowIndex * celHeightBM, (record.move.colIndex + 1) * celWidthBM, (record.move.rowIndex + 1) * celHeightBM), paint);
        } else {
            canvas.drawBitmap(bitmapTick, new Rect(0, 0, bitmapTick.getWidth(), bitmapTick.getHeight()),
                    new Rect(record.move.colIndex * celWidthBM, record.move.rowIndex * celHeightBM, (record.move.colIndex + 1) * celWidthBM, (record.move.rowIndex + 1) * celHeightBM), paint);
        }
        view.invalidate();

        return true;
    }

    public List<Move> getMoves() {
        List<Move> moveList = new ArrayList<>();
        for (int i = 0; i < rowQty; i++) {
            for (int j = 0; j < colQty; j++) {
                if (board[i][j] == EMPTY) {
                    moveList.add(new Move(i, j));
                }
            }
        }
        return moveList;
    }

    public void makeMove(Move move) {
        board[move.rowIndex][move.colIndex] = currentPlayer;
        currentMove = move;
        currentPlayer = (currentPlayer == PLAYER_HUMAN) ? PLAYER_BOT : PLAYER_HUMAN;
    }

    public int currentPlayer() {
        return currentPlayer;
    }

    public int evaluate() {
        if (winner == -1) {
            return 0;
        }
        if (winner == currentPlayer) {
            return 1;
        } else {
            return -1;
        }
    }

    private boolean checkWin(int player) {
        int count = 0;
        for (int i = 0; i < rowQty; i++) {
            if (board[i][currentMove.colIndex] == player) {
                count++;
                if (count == COUNT_WIN) {
                    winner = player;
                    return true;
                }
            } else {
                count = 0;
            }
        }

        count = 0;
        for (int i = 0; i < colQty; i++) {
            if (board[currentMove.rowIndex][i] == player) {
                count++;
                if (count == COUNT_WIN) {
                    winner = player;
                    return true;
                }
            } else {
                count = 0;
            }
        }

        count = 0;
        int delta = currentMove.rowIndex - currentMove.colIndex;
        for (int i = 0; i < rowQty; i++) {
            for (int j = 0; j < colQty; j++) {
                if (i - j == delta) {
                    if (board[i][j] == player) {
                        count++;
                        if (count == COUNT_WIN) {
                            winner = player;
                            return true;
                        }
                    } else {
                        count = 0;
                    }
                }
            }
        }

        count = 0;
        delta = currentMove.rowIndex + currentMove.colIndex;
        for (int i = 0; i < rowQty; i++) {
            for (int j = 0; j < colQty; j++) {
                if (i + j == delta) {
                    if (board[i][j] == player) {
                        count++;
                        if (count == COUNT_WIN) {
                            winner = player;
                            return true;
                        }
                    } else {
                        count = 0;
                    }
                }
            }
        }

        winner = -1;
        return false;
    }

    public boolean isGameOver() {
        if (checkWin(PLAYER_HUMAN) || checkWin(PLAYER_BOT)) {
            return true;
        }
        for (int i = 0; i < rowQty; i++) {
            for (int j = 0; j < colQty; j++) {
                if (board[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        winner = -1;
        return true;
    }

    public int getCurrentDept() {
        int count = 0;
        for (int i = 0; i < rowQty; i++) {
            for (int j = 0; j < colQty; j++) {
                if (board[i][j] == EMPTY) {
                    count++;
                }
            }
        }
        return count;
    }
}
