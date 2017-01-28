/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertictacserver;

import control.UserController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import model.Request;
import model.User;

/**
 *
 * @author kazafy
 */
    class GameHandler extends Thread {

        ObjectInputStream ois;
        ObjectOutputStream ous;
        User user = null;
        
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
//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////// switch ////////////////////////////////////////////////
                    switch(request.getType()){
//////////////////////////////////////////////////////////////////////////////////////////////////
                        case Setting.REG:
                            user = (User)request.getObject();
                            if(UserController.register(user)){ 
                                // if register ok send list off available players to client
                                user.setStatus(Setting.AVAILABLE);
                                request.setType(Setting.REG_OK);                                
                                List l = new ArrayList<User>();                                
                                for (GameHandler gameHandler : clientsVector){
                                    if(gameHandler.user.getStatus()== Setting.AVAILABLE)
                                        l.add(gameHandler.user);
                                    System.out.println(""+gameHandler.user.getName());
                                }
                                request.setObject(l);
                                this.ous.writeObject(request);
                                this.ous.flush();
                                this.ous.reset();
                                request.setType(Setting.ADD_PLAYER_TO_AVAILABLE_LIST);
                                request.setObject(user);
                                brodCast(request);
                            } 
                            else{
                                // error in registration  send to client error message
//                                
                            }
                            break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                        case Setting.LOGIN:
                            user = (User)request.getObject();
                            if(UserController.login(user)){ 
                                // if login ok send list off available players to client
                                user.setStatus(Setting.AVAILABLE);
                                request.setType(Setting.LOGIN_OK);                                
                                List l = new ArrayList<User>();                                
                                for (GameHandler gameHandler : clientsVector){
                                    if(gameHandler.user.getStatus()== Setting.AVAILABLE && gameHandler.user != user)
                                        l.add(gameHandler.user);                                    
                                }
                                request.setObject(l);
                                System.out.println(""+request.getObject());
                                this.ous.writeObject(request);
                                this.ous.flush();
                                this.ous.reset();
                                request.setType(Setting.ADD_PLAYER_TO_AVAILABLE_LIST);
                                request.setObject(user);
                                brodCast(request);

                            } 
                            else{
                                // error in registration  send to client error message
                                this.ous.writeObject(request);
                            }
                            break;
                            
//////////////////////////////////////////////////////////////////////////////////////////////////
                        default:
                            System.out.println("defualt");
                    }
/////////////////////////////////// end switch ////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
                    
                    if(request == null)
                    {
                        
                        clientsVector.remove(this);
                        System.out.println("null this :"+clientsVector.size());
                        ous.close();
                        ois.close();
                        break;
                    
                    }
//                    sendMessageToAll(request);      
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
        void brodCast(Request request) throws IOException {
            for (GameHandler ch : clientsVector) {
                if(ch.user != request.getObject())
                ch.ous.writeObject(request);         
            }
        }
    }
