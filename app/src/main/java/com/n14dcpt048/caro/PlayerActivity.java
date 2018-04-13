package com.n14dcpt048.caro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.n14dcpt048.caro.broad.ChessBoard;
import com.n14dcpt048.caro.callbacks.OnTouchCallback;
import com.n14dcpt048.caro.socket.SocketAsyncTask;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity implements OnTouchCallback {

    private SocketAsyncTask socketAsyncTask;
    private ImageView imageView;
    private ChessBoard chessBoard;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        socketAsyncTask = new SocketAsyncTask(this,  new ProgressDialog(this));
        socketAsyncTask.execute("10.0.2.2", "8080");

        chessBoard = new ChessBoard(this, 800, 800, 8, 8);
        chessBoard.init();
        textView = findViewById(R.id.sttText);
        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(chessBoard.drawBoard());
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return chessBoard.onTouchPlayingWithPlayer(v, event);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_reset:
                Intent intent = new Intent(PlayerActivity.this, PlayerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTouch(final int colIndex, final int rowIndex) {
        imageView.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketAsyncTask.sendStep(colIndex, rowIndex);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onReceive(final int colIndex, final int rowIndex) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chessBoard.onDrawOpposite(imageView, colIndex, rowIndex);
                imageView.setEnabled(true);
            }
        });
    }

    @Override
    public void onReceiveStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setEnabled(false);
                textView.setText(message);
            }
        });
    }

    public void enable(boolean enable){
        imageView.setEnabled(enable);
    }
}
