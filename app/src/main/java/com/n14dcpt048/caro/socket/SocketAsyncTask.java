package com.n14dcpt048.caro.socket;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import com.n14dcpt048.caro.PlayerActivity;
import com.n14dcpt048.caro.callbacks.OnTouchCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by silent on 4/10/2018.
 */
public class SocketAsyncTask extends AsyncTask<String, Integer, Integer> {

    private ProgressDialog progressDialog;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private Context context;

    private OnTouchCallback callback;


    public SocketAsyncTask(Context context, ProgressDialog progressDialog) {
        this.progressDialog = progressDialog;
        progressDialog.setMessage("Connect Server...");
        this.context = context;
        this.callback = (OnTouchCallback) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected Integer doInBackground(String... strings) {
        Socket socket;
        try {
            socket = new Socket(strings[0], Integer.parseInt(strings[1]));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            publishProgress(1);
            requestFindingPlayer();
            // block
            int stt = acceptChallenge();
            Log.d("TRUNG", "role: " + stt);
            return stt;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        switch (values[0]) {
            case 1:
                progressDialog.setMessage("Find a player...");
                break;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        progressDialog.dismiss();
        switch (integer) {
            case -1:
                Toast.makeText(context, "connected fail", Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();
                break;
            case 1:
                ((PlayerActivity) context).enable(true);
                Toast.makeText(context, "Find a player ! Let's playing...", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String message = null;
                        int stt;
                        while (true) {
                            try {
                                stt = bufferedReader.read();
                                message = bufferedReader.readLine();
                                switch (stt) {
                                    case 1:
                                        Log.d("TRUNG", "receive step " + message);
                                        String[] arr = message.split(",");
                                        callback.onReceive(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
                                        break;
                                    case 2:
                                        callback.onReceiveStatus(message);
                                        break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                break;
            case 2:
                ((PlayerActivity) context).enable(false);
                Toast.makeText(context, "Find a player ! Waiting for first step...", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String message = null;
                        int stt;
                        while (true) {
                            try {
                                stt = bufferedReader.read();
                                message = bufferedReader.readLine();
                                switch (stt) {
                                    case 1:
                                        Log.d("TRUNG", "receive step " + message);
                                        String[] arr = message.split(",");
                                        callback.onReceive(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
                                        break;
                                    case 2:
                                        callback.onReceiveStatus(message);
                                        break;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                break;
        }
    }

    private void requestFindingPlayer() throws IOException {
        bufferedWriter.write(1);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private int acceptChallenge() throws IOException {
        int stt = bufferedReader.read();
        bufferedReader.readLine();
        return stt;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public void setBufferedWriter(BufferedWriter bufferedWriter) {
        this.bufferedWriter = bufferedWriter;
    }

    public void sendStep(int colIndex, int rowIndex) throws IOException {
        bufferedWriter.write(2);
        Log.d("TRUNG", "send step " + colIndex + ", " + rowIndex);
        bufferedWriter.write(colIndex + "," + rowIndex);
        bufferedWriter.newLine();
        bufferedWriter.flush();

    }
}
