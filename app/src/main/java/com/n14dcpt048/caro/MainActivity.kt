package com.n14dcpt048.caro

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.n14dcpt048.caro.broad.ChessBoard
import com.n14dcpt048.caro.socket.SocketAsyncTask
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnTouchListener {

    lateinit var chessBoard: ChessBoard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chessBoard = ChessBoard(this, 800, 800, 8, 8)
        chessBoard.init()
        chessboardImageView.setImageBitmap(chessBoard.drawBoard())
        chessboardImageView.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            return chessBoard.onTouch(v, event)
        }
        return false
    }


}
