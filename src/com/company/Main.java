package com.company;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Main {

    public static DataBase db = new DataBase();
    public static ServerSocket loginServerSocket;
    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    3,
                    20,
                    200,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
    public static final int loginNetThreadPort = 13080;

    public static void main(String[] args) {
        try {
            loginServerSocket = new ServerSocket(loginNetThreadPort);
            loginServerSocket.setSoTimeout(100000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread t = new LoginNetThread();
        t.start();
    }
}
