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
    Thread thread;
    public static void sendRequest(User user, int type) {

        try {
            ous.flush();
            ous.reset();

            if (type == Setting.LOGIN) {
                System.out.println("" + user.getEmail());
                request.setClientID(user.getEmail());
            }
            System.out.println("" + request.getClientID());
               
            
            System.out.println("before sent"+request.getClientID());
            request.setType(type);
            System.out.println("before sent"+request.getType());
            request.getType();
            request.setObject(user);

            System.out.println("user ::" + user.getName());
            ous.writeObject(request);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void sendRequest(Request request) {

        try {
            ous.writeObject(request);
            ous.flush();
            ous.reset();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

////////////////////////////////////////////////////////////////////////////////        
    public Client() {

        try {
            mySocket = new Socket("127.0.0.1", 5005);
            ois = new ObjectInputStream(mySocket.getInputStream());
            ous = new ObjectOutputStream(mySocket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {

                        Request request = (Request) ois.readObject();
                        System.out.println("req type " + request.getType());
//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////// switch ////////////////////////////////////////////////
                        switch (request.getType()) {
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.REG_OK:
                                Object[] objects = (Object[]) request.getObject();
                                List<User> availablePlayerList = (ArrayList) objects[0];
                                
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        try {
                                            MainController.availableUsers.addAll(availablePlayerList);
                                            for (User user : availablePlayerList) {
                                                System.out.println(" u id :"+user.getId());
                                                System.out.println(" u na:"+user.getName());
                                                System.out.println(" u em:"+user.getEmail());
                                            }
                                            
                                            ClientTicTacToe.replaceSceneContent(ClientTicTacToe.MAIN_XML,"Chat Menu");
                                            ClientTicTacToe.mainController.setPlayer((User)objects[1]);
                                        } catch (Exception ex) {
                                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                });
                                break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.REG_NO:

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        String myError = (String) request.getObject();
                                        ClientTicTacToe.registerController.error.setVisible(true);
                                        ClientTicTacToe.registerController.errorText.setVisible(true);
                                        ClientTicTacToe.registerController.errorText.setText(myError);
                                        System.out.println(myError);    
                                    }
                                });
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
                                String myError = (String) request.getObject();
                                System.out.println(myError);
                                    break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.ADD_PLAYER_TO_AVAILABLE_LIST:
                                User user = (User) request.getObject();
                                Platform.runLater(new Runnable() {
                                    public void run() {
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
                                objects = (Object[]) request.getObject();
                                User remotePlayer = (User) objects[0];
                                ClientTicTacToe.mainController.setRemotePlayer(remotePlayer);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {   
                                        
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Invitation Request");
                                        alert.setContentText("اسطى "+remotePlayer.getName() +" عايز يلعب معاك");
                                        Optional<ButtonType> result = alert.showAndWait();
                                        
                                        if (result.isPresent() && result.get() == ButtonType.OK) {
                                            System.out.println("playing "+remotePlayer.getName());
                                            request.setObject(objects);
                                            request.setType(Setting.ACCEPT_INVITATION);
                                            Client.sendRequest(request);                                            
                                            ClientTicTacToe.mainController.playerChar_X_OR_O = 0;
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
                                objects = (Object[]) request.getObject();
                                remotePlayer = (User) objects[1];                                
                                ClientTicTacToe.mainController.setRemotePlayer(remotePlayer);                                
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {   
                                        
                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Invitation Request");
                                        alert.setContentText("البرنس "+remotePlayer.getName() +" وافق انه يلعب معاك");
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
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.MOVEBACK:
                                System.out.println(".MOVEBACK()");
                                ArrayList xo = (ArrayList) request.getObject();
                                System.out.println(".MOVEBACK()" + xo.size());
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        try {
                                            ClientTicTacToe.mainController.updateCell(xo);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                break;
//////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.WINNER:
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        try {
                                            ClientTicTacToe.mainController.showDialog(Setting.WIN_MSG);
                                        } catch (Exception e) {
                                            e.printStackTrace();
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
        });

    }
}
