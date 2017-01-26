/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertictacserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Request;
import model.User;

/**
 *
 * @author kazafy
 */
    class GameHandler extends Thread {

        ObjectInputStream ois;
        ObjectOutputStream ous;
        static Vector<GameHandler> clientsVector = new Vector<>();
        public GameHandler(Socket cs) {
            try {
                ous = new ObjectOutputStream(cs.getOutputStream());   
                ois = new ObjectInputStream(cs.getInputStream());                             
                clientsVector.add(this);
                start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        public void run() {
            while (true) {

                Request request;
                try {
                    request = (Request) ois.readObject();
                    System.out.println("req"+request);
                    switch(request.getType()){
                        case Setting.REG:
                            System.out.println(""+((User)request.getObject()).getName());
                            System.out.println(""+((User)request.getObject()).getEmail());
                            System.out.println(""+((User)request.getObject()).getPassword());
                            break;
                        default:
                            System.out.println("defualt");
                    }
                    
                    if(request == null)
                    {
                        
                        clientsVector.remove(this);
                        System.out.println("null this :"+clientsVector.size());
                        ous.close();
                        ois.close();
                        break;
                    
                    }
                    sendMessageToAll(request);      
                } catch (Exception ex) {
                    try {
                        clientsVector.remove(this);
                        System.out.println(" this :"+clientsVector.size());
                        ous.close();
                        ois.close();
                        ex.printStackTrace();
                        break;
                    } catch (IOException ex1) {
                        ex1.printStackTrace();
                    }
                }
            }
        }
        void sendMessageToAll(Request msg) throws IOException {
            for (GameHandler ch : clientsVector) {
                ch.ous.writeObject(new Request(5));
            }
        }
    }