/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertictacserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author kazafy
 */
public class Server extends Thread {

    ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(5005,50);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void run() {
        
        while (true) {
            try {
                while (true) {
                    Socket s = serverSocket.accept();
                    new GameHandler(s);
                    System.out.println("connection come");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

}
