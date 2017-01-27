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
//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////// switch ////////////////////////////////////////////////
                         switch(request.getType()){
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.REG_OK: 
                                List l = (ArrayList) request.getObject();
                                for (Object user : l) {
                                    System.out.println(((User)user).getName());
                                }

                                ClientTicTacToe.replaceSceneContent(ClientTicTacToe.MAIN_XML);
                                
                                break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.REG_NO:
                                    break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.LOGIN_OK:
                                    break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.LOGIN_NO:
                                    break;
                         }
/////////////////////////////////// end switch ////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
                         
                         

                    } catch (Exception ex) {
                        System.out.println("lol   ");
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

