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
public class Client extends Thread{

    static Socket mySocket;
    static ObjectOutputStream ous;
    static ObjectInputStream ois;
    static PrintStream ps;
    static Request request = new Request();

    public static void sendRequest(User user, int type) {

        try {
            if (ous != null){
                ous.flush();
                ous.reset();    
            }
            if (type == Setting.LOGIN) {
                System.out.println("" + user.getEmail());
                request.setClientID(user.getEmail());
            }
            System.out.println("" + request.getClientID());

            System.out.println("before sent" + request.getClientID());
            request.setType(type);
            System.out.println("before sent" + request.getType());
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
    //construct the client with creating a socket to send the first request which is login or register
    //if it succeded, start the thread of the coming requests
    //else, close the socket 
    public Client(User u, int t) {

        try {
            mySocket = new Socket("127.0.0.1", 5005);
            ois = new ObjectInputStream(mySocket.getInputStream());
            ous = new ObjectOutputStream(mySocket.getOutputStream());
            this.sendRequest(u, t);
            Request request = (Request) ois.readObject();
            if (request.getType() == Setting.REG_OK || request.getType() == Setting.LOGIN_OK){
                Object[] objects = (Object[]) request.getObject();
                List<User> availablePlayerList = (ArrayList) objects[0];

                Platform.runLater(new Runnable() {
                    public void run() {
                        try {
                            MainController.availableUsers.addAll(availablePlayerList);
                            ClientTicTacToe.replaceSceneContent(ClientTicTacToe.MAIN_XML, "Chat Menu");
                            ClientTicTacToe.mainController.setPlayer((User) objects[1]);
                        } catch (Exception ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                });
                start();
            }
            else if (request.getType() == Setting.REG_NO || request.getType() == Setting.LOGIN_NO){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String myError = (String) request.getObject();
                        if (request.getType() == Setting.REG_NO){
                            ClientTicTacToe.registerController.error.setVisible(true);
                            ClientTicTacToe.registerController.errorText.setVisible(true);
                            ClientTicTacToe.registerController.errorText.setText(myError);
                        }
                        else{
                            ClientTicTacToe.loginController.errorsalma.setText(myError);
                            ClientTicTacToe.loginController.errorsalma.setVisible(true);    
                        }
                        System.out.println(myError);
                    }
                });
                this.ois.close();
                this.ous.close();
                this.mySocket.close();
            }
        }catch (IOException ex) {
            System.out.println("my IOException");
            if (ClientTicTacToe.registerController != null){
                ClientTicTacToe.registerController.errorText.setVisible(true);
                ClientTicTacToe.registerController.errorText.setText("Server DOWN! :( come back later");    
            }
            if (ClientTicTacToe.loginController != null){
                ClientTicTacToe.loginController.errorsalma.setVisible(true);
                ClientTicTacToe.loginController.errorsalma.setText("Server DOWN! :( come back later");
            }
            //ex.printStackTrace();
        }catch (ClassNotFoundException ex) {
            System.out.println("my ClassNotFoundException");
        }
    }

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
                                Object[] objects = (Object[]) request.getObject();
                                User remotePlayer = (User) objects[0];
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        ClientTicTacToe.mainController.setRemotePlayer(remotePlayer);
                                        ClientTicTacToe.mainController.setDisable_Enable_MainView(true);

                                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                        alert.setTitle("Invitation Request");
                                        alert.setContentText("اسطى " + remotePlayer.getName() + " عايز يلعب معاك");
                                        Optional<ButtonType> result = alert.showAndWait();

                                        if (result.isPresent() && result.get() == ButtonType.OK) {
                                            request.setObject(objects);
                                            request.setType(Setting.ACCEPT_INVITATION);
                                            Client.sendRequest(request);
                                            ClientTicTacToe.mainController.playerChar_X_OR_O = 0;
                                            ClientTicTacToe.mainController.setDisable_Enable_MainView(false);
                                            ClientTicTacToe.mainController.setDisable_Enable_ListView(true);

                                        } else {
                                            //////////////logic here///////////////////////
                                            request.setObject(objects);
                                            request.setType(Setting.REFUSE_INVITATION);
                                            Client.sendRequest(request);
                                            ClientTicTacToe.mainController.setDisable_Enable_MainView(false);

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
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {

                                        ClientTicTacToe.mainController.setRemotePlayer(remotePlayer);
                                        ClientTicTacToe.mainController.setDisable_Enable_MainView(false);
                                        ClientTicTacToe.mainController.setDisable_Enable_ListView(true);

                                    }
                                });
                                break;
////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.REFUSE_INVITATION:
                                System.out.println("REFUSE_INVITATION Client");
                                objects = (Object[]) request.getObject();
                                remotePlayer = (User) objects[1];
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        String refuseMessage= "معلش يابرنس "+remotePlayer.getName()+" فكسلك :( ";
                                        ClientTicTacToe.mainController.showDialog(refuseMessage);
                                        ClientTicTacToe.mainController.setDisable_Enable_MainView(false);

                                    }
                                });
                                break;
//////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.MOVEBACK:
                                int[] xo = (int[]) request.getObject();
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
                                            int result = ClientTicTacToe.mainController.showWinDialog(Setting.WIN_MSG);
                                            
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                break;
//////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.LOSER:
                                xo = (int[]) request.getObject();
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        try {
                                            ClientTicTacToe.mainController.updateCell(xo);
                                            ClientTicTacToe.mainController.showWinDialog(Setting.LOSE_MSG);  
                                            ClientTicTacToe.mainController.resetGame();
                                            
                                            ClientTicTacToe.mainController.setDisable_Enable_MainView(false);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                break;
//////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.UPDATE_PLAYER_IN_PLAYER_LIST:
                                user = (User) request.getObject();
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        try {
                                            for (User u : MainController.availableUsers) {
                                                if(u.getId() == user.getId())
                                                    u = user;
                                            }
                                            
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
        

    
}
