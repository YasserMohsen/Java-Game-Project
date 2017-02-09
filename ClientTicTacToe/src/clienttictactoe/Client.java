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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.color;
import model.MyImage;
import model.Request;
import model.User;

/**
 *
 * @author kazafy
 */
public class Client extends Thread {

    static Socket mySocket;
    static ObjectOutputStream ous;
    static ObjectInputStream ois;
    static PrintStream ps;
    static Request request = new Request();
    //boolean flag=true;
    static boolean conn=false;

    final AudioClip ad = new AudioClip(MainController.class.getResource("game-over.wav").toString());
    final AudioClip ae = new AudioClip(MainController.class.getResource("game-over-tie.wav").toString());
//  boolean flag=true;

    public static void sendRequest(User user, int type) {

        try {
            if (ous != null) {
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
            boolean flag=true;
            mySocket = new Socket("127.0.0.1", 5005);
            ois = new ObjectInputStream(mySocket.getInputStream());
            ous = new ObjectOutputStream(mySocket.getOutputStream());
            conn = true;
            this.sendRequest(u, t);
            Request request = (Request) ois.readObject();
            if (request.getType() == Setting.REG_OK || request.getType() == Setting.LOGIN_OK) {
              
                   // if(flag){
                Object[] objects = (Object[]) request.getObject();
                List<User> availablePlayerList = (ArrayList) objects[0];
                User player = (User) objects[1];
                Platform.runLater(() -> {
                    try {

                        MainController.availableUsers.addAll(availablePlayerList);
                        ClientTicTacToe.replaceSceneContent(ClientTicTacToe.main_XML, "Chat Menu");
                        ClientTicTacToe.mainController.setPlayer(player);
                        
                        MyImage s = player.getSerializedImg();
                        ClientTicTacToe.mainController.setMyImage(s.getImage());
                        ClientTicTacToe.mainController.setMyName(player.getName());
                        System.out.println(player.getScore());
                        ClientTicTacToe.mainController.setMyScore(player.getScore());
                    } catch (Exception ex) {
                        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                start();

              //    }
            } else if (request.getType() == Setting.REG_NO || request.getType() == Setting.LOGIN_NO) {
                Platform.runLater(() -> {
                    String myError = (String) request.getObject();
                    if (request.getType() == Setting.REG_NO) {
                        
                        ClientTicTacToe.home.registerController.mailerror.setText(myError);
                        ClientTicTacToe.home.registerController.mailerror.setVisible(true);
                        ClientTicTacToe.home.registerController.mailerror.setTextFill(Color.RED);
//                             ClientTicTacToe.home.registerController.reggrid.add(l,1,0);
//<Label fx:id="mailerror" text="" prefHeight="36.0" prefWidth="316.0" visible="false" GridPane.columnIndex="2" GridPane.valignment="BOTTOM" />
                    } else {
                        ClientTicTacToe.home.loginController.errorsalma.setText(myError);
                        ClientTicTacToe.home.loginController.errorsalma.setVisible(true);
                        ClientTicTacToe.home.loginController.errorsalma.setTextFill(Color.RED);
                        ClientTicTacToe.home.loginController.email.setText("");
                        ClientTicTacToe.home.loginController.password.setText("");
                    }

                });
                this.ois.close();
                this.ous.close();
                this.mySocket.close();
            }

        } catch (IOException ex) {
            System.out.println("my IOException");
            if (ClientTicTacToe.home.registerController != null) {
                ClientTicTacToe.home.registerController.repasserror.setVisible(true);
                ClientTicTacToe.home.registerController.repasserror.setTextFill(Color.RED);
                ClientTicTacToe.home.registerController.repasserror.setText("Server DOWN! :( come back later");
            }
            if (ClientTicTacToe.home.loginController != null) {
                ClientTicTacToe.home.loginController.errorsalma.setVisible(true);
                ClientTicTacToe.home.loginController.errorsalma.setTextFill(Color.RED);
                ClientTicTacToe.home.loginController.errorsalma.setText("Server DOWN! :( come back later");
            }
            //ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
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
                                            ClientTicTacToe.mainController.setDisable_Enable_ChatView(false);
                                            ClientTicTacToe.mainController.btnGoOffLine.setDisable(true);
                                            ClientTicTacToe.mainController.btnLogout.setDisable(true);
                                            

                                        } else {
                                            //////////////logic here///////////////////////
                                            request.setObject(objects);
                                            request.setType(Setting.REFUSE_INVITATION);
                                            Client.sendRequest(request);
                                            ClientTicTacToe.mainController.setDisable_Enable_MainView(false);
                                        }
                                        ClientTicTacToe.mainController.playDisable = true;

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
                                        ClientTicTacToe.mainController.setDisable_Enable_ChatView(false);
                                        ClientTicTacToe.mainController.playDisable = false;
                                        ClientTicTacToe.mainController.btnGoOffLine.setDisable(true);
                                        ClientTicTacToe.mainController.btnLogout.setDisable(true);
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
                                String refuseMessage = "معلش يابرنس " + remotePlayer.getName() + " فكسلك :( ";
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
                                    Object[] obj = (Object[]) request.getObject();
                                    User winner = (User) obj[0];
                                        ad.play();
                                        ae.play();
                                    int result = ClientTicTacToe.mainController.showWinDialog(Setting.WIN_MSG);
                                    ClientTicTacToe.mainController.setMyScore(winner.getScore());
                                    System.out.println(ClientTicTacToe.mainController.getPlayer().getScore());    
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

                            case Setting.DRAW:
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        try {
                                            int result = ClientTicTacToe.mainController.showWinDialog(Setting.DRAW_MSG);
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
                                            for (User u : ClientTicTacToe.mainController.availableUsers) {
                                                if(u.getId() == user.getId())
                                                    u.setStatus(user.getStatus());
                                                    u.setScore(user.getScore());

                                            }
                                            ClientTicTacToe.mainController.lv_players.refresh();
                                            
                                        } catch (Exception ex) {
                                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }

                        });
                        break;
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.UPDATE_2PLAYER_IN_PLAYER_LIST:
                                objects = (Object[]) request.getObject();
                                User player1 = (User) objects[0];
                                User player2 = (User) objects[1];
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        try {
                                            for (User u : ClientTicTacToe.mainController.availableUsers) {
                                                if(u.getId() == player1.getId()){
                                                    u.setStatus(player1.getStatus());
                                                    u.setScore(player1.getScore());
                                                }
                                                else if (u.getId() == player2.getId()){
                                                    u.setStatus(player2.getStatus());
                                                    u.setScore(player2.getScore());
                                                }
                                            }
                                            ClientTicTacToe.mainController.lv_players.refresh();
//                                            for (User user : ClientTicTacToe.mainController.lv_players.getItems()) {
//                                                System.out.println("playerId:"+user.getId());
//                                                System.out.println("playername:"+user.getName());
//                                                System.out.println("playerEmail:"+user.getEmail());
//                                                System.out.println("playerStatus:"+user.getStatus());
//                                                System.out.println("playerScore:"+user.getScore());
//
//                                            }
                                            
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                    }
                        });
                        break;
////////////////////////////////////////////////
                            case Setting.RECIEVE_MESSAGE:
                                objects = (Object[]) request.getObject();
                                User sender = (User) objects[0];
                                String message = (String) objects[1];
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        try {
                                            Label l = new Label();
                                            l.setPrefWidth(160);
                                            l.setMaxHeight(400);
                                            
                                            if (sender.getId() != ClientTicTacToe.mainController.getPlayer().getId()){
                                                l.setStyle("-fx-background-color:darkgray;-fx-background-radius:20;-fx-padding:10;-fx-font-weight:bold;");
                                                l.setText(sender.getName() + ":\n" + message);
                                                l.setWrapText(true);
                                            }
                                            else{
                                                l.setStyle("-fx-background-color:#1ab188;-fx-background-radius:20;-fx-padding:10;-fx-font-weight:bold;");
                                                l.setTranslateX(56);
                                                l.setText(message);
                                                l.setWrapText(true);
                                            }
                                            ClientTicTacToe.mainController.chatInstance.add(l);
                                            
                                            //ClientTicTacToe.mainController.chatArea.appendText(sender.getName() + ":\n" + message + "\n");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                    case Setting.UPDATE_NEWS:
                        String myNew = (String) request.getObject();
                        Platform.runLater(new Runnable() {
                            public void run() {
                                try {
                                    Label l = new Label();
                                    l.setText(myNew);
                                        
                              //-fx-background-color:lightblue; 
                                    l.setStyle("-fx--moz-border-radius:4px;-fx-font-weight:bold;-fx--webkit-border-radius: 4px;display:block;");
                                    //l.setStyle("-fx-border-style: solid; -fx--moz-border-radius: 4px; -fx-color: #468847;-fx--webkit-border-radius: 4px; -fx-font-size: 14; background-color: #dff0d8;");
                                    l.setTextFill(Color.LIGHTSEAGREEN);
                                    ClientTicTacToe.mainController.newsInstance.add(l);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                    case Setting.DELETE_PLAYER_FROM_AVAILABLE_LIST:
                        user = (User) request.getObject();
                        System.out.println("hi :"+user.getName());
                        Platform.runLater(new Runnable() {
                            public void run() {
                                try {
                                    
                                    for (Iterator<User> iterator = ClientTicTacToe.mainController.availableUsers.iterator(); iterator.hasNext();) {
                                        User nextUser = iterator.next();
                                        if (nextUser.getId() == user.getId()) {
                                            ClientTicTacToe.mainController.availableUsers.remove(nextUser);
                                        }
                                    }
                                    ClientTicTacToe.mainController.lv_players.refresh();
                                } catch (Exception ex) {
                                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }
                        });
                        break;
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
