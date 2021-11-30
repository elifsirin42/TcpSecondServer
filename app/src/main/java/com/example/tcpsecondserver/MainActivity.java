package com.example.tcpsecondserver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;

public class MainActivity extends AppCompatActivity {

    private ServerSocket serverSocket;

    Handler UIHandler;

    Thread Thread1 = null;

    private EditText EDITTEXT;

    public static final int SERVERPORT = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EDITTEXT = findViewById(R.id.edittext);

        UIHandler = new Handler();

        this.Thread1 = new Thread(new Thread1());
        this.Thread1.start();
    }

    class Thread1 implements Runnable {

        @Override
        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();

                    Thread2 commThread = new Thread2(socket);
                    new Thread(commThread).start();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread2 implements Runnable {

        private  Socket clientSocket;
        private BufferedReader input;

        public  Thread2(Socket clientSocket) {
            this.clientSocket = clientSocket;

            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                //System.out.println("Input" + this.input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = input.readLine();
                    System.out.println(read);
                    //System.out.println("VOLUME UPPP! ");



                    if(read != null) {
                        UIHandler.post(new updateUIThread(read));
                    }else {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class updateUIThread implements Runnable {

        private  String msg;
        public updateUIThread(String str) {this.msg = str;}

        @Override
        public void run() {
            EDITTEXT.setText(EDITTEXT.getText().toString() +  "Client Says : " + msg + "\n");
        }
    }
}