package com.company;

import java.io.IOException;
import java.net.Socket;

public class TCPThread extends Thread {

    @Override
    public void run() {
        /*System.out.println("Mes:TCP等待远程连接");
        while(true)
        {
            try {
                System.out.println("0000000000");
                Socket socket = Main.messageServerSocket.accept();
                MessageNetThread mnt = new MessageNetThread(socket);
                Main.executor.execute(mnt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}
