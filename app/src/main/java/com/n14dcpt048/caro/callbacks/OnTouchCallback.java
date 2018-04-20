package com.n14dcpt048.caro.callbacks;

/**
 * Created by silent on 4/13/2018.
 */
public interface OnTouchCallback {

    void onTouch(int colIndex, int rowIndex);
    void onOppReceive(int colIndex, int rowIndex);
    void onPlayerReceive(int colIndex, int rowIndex);
    void onFailedMakeMove();
    void onReceiveStatus(String message);
}
