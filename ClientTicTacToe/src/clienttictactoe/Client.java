/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttictactoe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.Request;
import model.User;

/**
 *
 * @author kazafy
 */
public class Client {
    

        static Socket mySocket;
        static ObjectOutputStream ous;
        static ObjectInputStream ois;
        static PrintStream ps;
        static Request request = new Request();
        
        
        public static void sendRequest(User user , int type){
        
        try {
            ous.flush();
            ous.reset();
            
            if (type==Setting.LOGIN) {
                System.out.println(""+user.getEmail());
                request.setClientID(user.getEmail());   
            }
            System.out.println("before sent"+request.getClientID());
            request.setType(type);
            System.out.println("before sent"+request.getType());
            request.getType();
            request.setObject(user);
            ous.writeObject(request);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        
        }

        public Client() {

        try {
                    mySocket = new Socket("127.0.0.1", 5005);
                    ois = new ObjectInputStream(mySocket.getInputStream());
                    ous = new ObjectOutputStream(mySocket.getOutputStream());                
                } 
            catch (IOException ex) {
                    ex.printStackTrace();
            }                
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while(true){
                    try {
                       
                        Request request =  (Request) ois.readObject();
                        
                        System.out.println("after recieve"+request.getClientID());
//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////// switch ////////////////////////////////////////////////
                         switch(request.getType()){
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.REG_OK: 
                                Platform.runLater(new Runnable(){
                                    public void run(){
                                        try {
                                            List l = (ArrayList) request.getObject();
                                            MainController.availableUsers.addAll(l);
                                            ClientTicTacToe.replaceSceneContent(ClientTicTacToe.MAIN_XML,"windowTitle");
                                        } catch (Exception ex) {
                                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                });
                                break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.REG_NO:
                                    break;
                                
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.LOGIN_OK:
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            List l = (ArrayList) request.getObject();
                                            
                                            MainController.availableUsers.addAll(l);
                                            ClientTicTacToe.replaceSceneContent(ClientTicTacToe.MAIN_XML, request.getClientID());
                                        } catch (Exception ex) {
                                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                });
                                
                                break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.LOGIN_NO:
                                    break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.ADD_PLAYER_TO_AVAILABLE_LIST: 
                                User user = (User)request.getObject();
                                Platform.runLater(new Runnable(){
                                    public void run(){
                                        try {
                                        MainController.availableUsers.add(user);
                                        } catch (Exception ex) {
                                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                });

                            break;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.SEND_INVITATION_FOR_PLAYING:
                                System.out.println("SEND_INVITATION_FOR_PLAYING Client");
                                System.out.println("playing "+request.getClientID());
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {   
                                        
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Invitation Request");
                                        alert.setContentText("اسطى "+request.getClientID() +" عايز يلعب معاك");
                                        Optional<ButtonType> result = alert.showAndWait();
                                        
                                        if (result.isPresent() && result.get() == ButtonType.OK) {
                                            String swapEmail;
                                            User user = (User)request.getObject(); 
                                            System.out.println("playing "+user.getEmail());
                                            swapEmail = request.getClientID();
                                            request.setClientID(user.getEmail());
                                            user.setEmail(swapEmail);
                                            request.setObject(user);
                                            Client.sendRequest(user, Setting.ACCEPT_INVITATION);
                                            
                                        }
                                        else{
                                            //////////////logic here///////////////////////
                                        }
                                    }
                                });
                                break;
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                case Setting.ACCEPT_INVITATION:
                                System.out.println("ACCEPT_INVITATION Client");
                                System.out.println("playing "+request.getClientID());
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {   
                                        
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Invitation Request");
                                        alert.setContentText("البرنس "+request.getClientID() +" وافق انه يلعب معاك");
                                        Optional<ButtonType> result = alert.showAndWait();
                                        
                                        if (result.isPresent() && result.get() == ButtonType.OK) {
//                                            String swapEmail;
//                                            User user = (User)request.getObject(); 
//                                            System.out.println("playing "+user.getEmail());
//                                            swapEmail = request.getClientID();
//                                            request.setClientID(user.getEmail());
//                                            user.setEmail(swapEmail);
//                                            request.setObject(user);
//                                            Client.sendRequest(user, Setting.ACCEPT_INVITATION);
//                                            
                                        }
                                        else{
                                            //////////////logic here///////////////////////
                                        }
                                    }
                                });
                                break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                         }
/////////////////////////////////// end switch ////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
                         
                         

                    } catch (Exception ex) {
                        System.out.println("lol");
                        ex.printStackTrace();
                        try {
                            
                            ois.close();
                            mySocket.close();
                            break;
                        } catch (IOException ex1) {
                            ex.printStackTrace();
                        }


                    }

                }
            }
        }).start();

        
    }
  }

