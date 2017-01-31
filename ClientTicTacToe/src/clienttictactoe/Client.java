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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
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
        
        
        public static void sendRequest(User user , int type){
        
        try {
            Request request = new Request();
            request.setType(type);
            request.setObject(user);
            ous.writeObject(request);
            ous.flush();
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
                        System.out.println("req type "+request.getType());
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
                                            ClientTicTacToe.replaceSceneContent(ClientTicTacToe.MAIN_XML);
                                        } catch (Exception ex) {
                                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                });
                                break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.REG_NO:
                                String myError = (String) request.getObject();
                                System.out.println(myError);
//                                Platform.runLater(new Runnable(){
//                                    public void run(){
//                                        try {
//                                            String myError = (String) request.getObject();
////                                            RegisterController.error.setVisible(true);
////                                            RegisterController.errorText.setText(myError);
////                                            RegisterController.errorText.setVisible(true);
////                                            List l = (ArrayList) request.getObject();
////                                            MainController.availableUsers.addAll(l);
////                                            ClientTicTacToe.replaceSceneContent(ClientTicTacToe.MAIN_XML);
//                                        } catch (Exception ex) {
//                                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
//                                        }
//
//                                    }
//                                });
                                    break;
                                
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.LOGIN_OK:
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            List l = (ArrayList) request.getObject();
                                            
                                            MainController.availableUsers.addAll(l);
                                            ClientTicTacToe.replaceSceneContent(ClientTicTacToe.MAIN_XML);
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

