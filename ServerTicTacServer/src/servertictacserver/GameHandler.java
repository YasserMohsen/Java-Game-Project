/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertictacserver;

import control.UserController;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import model.Request;

import model.User;

/**
 * @author kazafy
 */
class GameHandler extends Thread {

    ObjectInputStream ois;
    ObjectOutputStream ous;
    User user = null;
    static HashMap<Integer, Integer> playersHashMap = new HashMap<>();
    static Vector<GameHandler> clientsVector = new Vector<>();

    //construct the gamehandler by checking the coming request (login or register)
    //check with database
    //if it succeded, add the client to the available list and start the thread to handle the coming requests
    //else, send the error message and close the socket
    public GameHandler(Socket cs) {
        try {
            ous = new ObjectOutputStream(cs.getOutputStream());
            ois = new ObjectInputStream(cs.getInputStream());
            try {
                Request request = (Request) ois.readObject();
                if (request.getType() == Setting.REG || request.getType() == Setting.LOGIN || request.getType() == Setting.FBLOG) {
                    user = (User) request.getObject();
                    if (request.getType() == Setting.LOGIN) {
                        user = UserController.login(user);

                    } else if (request.getType() == Setting.REG) {
                        user = UserController.register(user);

                    } else if (request.getType() == Setting.FBLOG) {
                        user = UserController.fbLogin(user);
                        System.out.println("" + user.getImg());
                        Image image = new Image(user.getImg());
                        saveToFile(image, "" + image.toString(), user);
                    }

                    if (user.getId() != 0 && !checkAlreadyLogin(user)) {

                        clientsVector.add(this);
                        // if register or login is ok send list off available players to client                                
                        this.ous.flush();
                        this.ous.reset();
                        user.setStatus(Setting.AVAILABLE);
                        //////////// Update server list ////////////////
                        if (request.getType() == Setting.REG) {
                            Platform.runLater(() -> {
                                try {
                                    ServerTicTacServer.items.add(user);
                                } catch (Exception e) {
                                    System.out.println("servertictacserver items ");
                                    e.printStackTrace();
                                }
                            });
                        }
                        if (request.getType() == Setting.LOGIN) {
                            ServerTicTacServer.updateServerList(user);
                        }
                        ServerTicTacServer.usersList.refresh();
                        ////////////////////////////////////////////////
                        request.setType(Setting.REG_OK);

                        List availablePlayerList = new ArrayList<User>();
                        for (GameHandler gameHandler : clientsVector) {
                            if (gameHandler.user != null && gameHandler.user.getId() != user.getId()) {
                                availablePlayerList.add(gameHandler.user);
                            }
                        }
                        Object[] objects = {availablePlayerList, user};
                        request.setObject(objects);
                        System.out.println("ssssssssssssss :" + user.getName());
                        this.ous.writeObject(request);
                        this.ous.flush();
                        this.ous.reset();
                        request.setType(Setting.ADD_PLAYER_TO_AVAILABLE_LIST);
                        request.setObject(user);
                        brodCast(request);
                        start();
                    } else {

                        // error in registration or login >> send to client error message
                        if (request.getType() == Setting.REG) {

                            request.setType(Setting.REG_NO);
                            request.setObject("email already exist");
                        } else if (checkAlreadyLogin(user)) {
                            request.setType(Setting.LOGIN_NO);
                            request.setObject("That account is already login");
                        } else if (request.getType() == Setting.LOGIN) {
                            request.setType(Setting.LOGIN_NO);
                            request.setObject("incorrect username or passsword");

                        }

                        this.ous.writeObject(request);
                        this.ous.flush();
                        this.ous.reset();
                        ous.close();
                        ois.close();
                        cs.close();
                    }
                }
            } catch (ClassNotFoundException ex) {
                System.out.println("construct gamehandler exception");
                //Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void run() {
        while (true) {

            Request request;
            try {
                request = (Request) ois.readObject();
                System.out.println("my type is : " + request.getType());
//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////// switch ////////////////////////////////////////////////
                switch (request.getType()) {
//////////////////////////////////////////////////////////////////////////////////////////////////
                    case Setting.MOVE:
                        Object[] objects = (Object[]) request.getObject();
                        User senderPlayer = (User) objects[0];
                        User receiverPlayer = (User) objects[1];
                        System.out.println("s id " + senderPlayer.getId());
                        System.out.println("r id " + receiverPlayer.getId());
                        int[] xo = (int[]) objects[2];
                        boolean win = false;
                        boolean draw = false;

                        if (checkWins(xo)) {
                            win = true;
                        } else if (checkDraw(xo)) {
                            draw = true;
                        }

                        if (win || draw) {

                            for (GameHandler ch : clientsVector) {
                                if (ch.user.getId() == senderPlayer.getId() || ch.user.getId() == receiverPlayer.getId()) {
                                    ch.user.setStatus(Setting.AVAILABLE);
                                    ServerTicTacServer.updateServerList(ch.user);

                                    ServerTicTacServer.usersList.refresh();
                                    if (ch.user.getId() == receiverPlayer.getId()) {
                                        request.setType((win) ? Setting.LOSER : Setting.DRAW);
                                        request.setObject(xo);
                                        ch.ous.writeObject(request);
                                        ch.ous.flush();
                                        ch.ous.reset();
                                    }
                                    if (ch.user.getId() == senderPlayer.getId()) {
                                        if (win) {
                                            ch.user.setScore((win) ? ch.user.getScore() + Setting.POINTS : ch.user.getScore());
                                            UserController.saveScore(ch.user);
                                            objects[0] = ch.user;
                                            ServerTicTacServer.updateServerListScore(ch.user);
                                            ServerTicTacServer.usersList.refresh();
                                        }

                                    }

                                }
                            }
                            //senderPlayer.setScore((win)?senderPlayer.getScore()+Setting.POINTS:senderPlayer.getScore());
                            //UserController.saveScore(senderPlayer);
                            request.setObject(objects);
                            request.setType((win) ? Setting.WINNER : Setting.DRAW);
                            this.ous.writeObject(request);
                            this.ous.flush();
                            this.ous.reset();

                            request.setObject(senderPlayer);
                            request.setType(Setting.UPDATE_PLAYER_IN_PLAYER_LIST);

                            brodCast(request);

                            this.ous.flush();
                            this.ous.reset();

                            request.setObject(receiverPlayer);
                            request.setType(Setting.UPDATE_PLAYER_IN_PLAYER_LIST);
                            brodCast(request);
                            this.ous.flush();
                            this.ous.reset();
                            senderPlayer.setStatus(Setting.AVAILABLE);
                            //////////////////////////////////////////////
                            //update news field
                            request.setType(Setting.UPDATE_NEWS);
                            if (win) {
                                request.setObject(senderPlayer.getName() + " WINS " + receiverPlayer.getName());
                            } else {
                                request.setObject(senderPlayer.getName() + " draw " + receiverPlayer.getName());
                            }
                            brodCastAll(request);
                        } else if (checkDraw(xo)) {

                            // draw
                        } else {

                            request.setType(Setting.MOVEBACK);
                            request.setObject(xo);

                            for (GameHandler ch : clientsVector) {
                                if (ch.user.getId() == receiverPlayer.getId()) {
                                    ch.ous.writeObject(request);
                                    ch.ous.flush();
                                    ch.ous.reset();
                                }
                            }
                        }

                        break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                    case Setting.MESSAGE:
                        objects = (Object[]) request.getObject();
                        senderPlayer = (User) objects[0];
                        receiverPlayer = (User) objects[1];
                        String message = (String) objects[2];

                        request.setType(Setting.RECIEVE_MESSAGE);
                        Object[] obj = {senderPlayer, message};
                        request.setObject(obj);
                        for (GameHandler ch : clientsVector) {
                            if (ch.user.getId() == senderPlayer.getId() || ch.user.getId() == receiverPlayer.getId()) {
                                ch.ous.writeObject(request);
                            }
                        }
                        break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                    case Setting.SELECT_PLAYER_FROM_AVAILABLE_LIST:

                        System.out.println("SELECT_PLAYER_FROM_AVAILABLE_LIST");

                        objects = (Object[]) request.getObject();
                        senderPlayer = (User) objects[0];
                        receiverPlayer = (User) objects[1];

                        request.setType(Setting.SEND_INVITATION_FOR_PLAYING);
//                            User user = (User)request.getObject();

                        for (GameHandler ch : clientsVector) {
                            if (ch.user.getId() == receiverPlayer.getId()) {
                                System.out.print("hi from  " + receiverPlayer.getName());
                                ch.ous.writeObject(request);
                            }
                        }
                        break;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    case Setting.ACCEPT_INVITATION:

                        System.out.println("ACCEPT_INVITATION");
                        objects = (Object[]) request.getObject();
                        senderPlayer = (User) objects[1];
                        receiverPlayer = (User) objects[0];
                        System.out.print("keyid  " + senderPlayer.getId() + " name :" + senderPlayer.getName());
                        System.out.print("valueid  " + receiverPlayer.getId() + " name :" + receiverPlayer.getName());

                        playersHashMap.put(senderPlayer.getId(), receiverPlayer.getId());
                        for (GameHandler ch : clientsVector) {
                            if (ch.user.getId() == receiverPlayer.getId()) {
                                ch.user.setStatus(Setting.BUSY); /// update player status in vector
                                request.setType(Setting.ACCEPT_INVITATION);
                                ch.ous.writeObject(request);
                                ch.ous.flush();
                                ch.ous.reset();
                            } else if (ch.user.getId() == senderPlayer.getId()) {
                                ch.user.setStatus(Setting.BUSY); /// update player status in vector
                            }

                            ((User) objects[1]).setStatus(Setting.BUSY);
                            ((User) objects[0]).setStatus(Setting.BUSY);
                            senderPlayer.setStatus(Setting.BUSY);
                            receiverPlayer.setStatus(Setting.BUSY);
                            ServerTicTacServer.updateServerList(senderPlayer);
                            ServerTicTacServer.updateServerList(receiverPlayer);
                            ServerTicTacServer.usersList.refresh();
                            ///////////////// update server list /////////////////
//                            for (User u : ServerTicTacServer.items){
//                                if (senderPlayer.getId() == u.getId() || receiverPlayer.getId() == u.getId()){
//                                    u.setStatus(Setting.BUSY);
//                                }                                   
//                            }

                            //////////////////////////////////////////////////////
                            //  System.out.println("Busy ::" +((User) objects[0]).getStatus());
                            // System.out.println("Busy ::" +((User) objects[1]).getStatus());
                            request.setType(Setting.UPDATE_2PLAYER_IN_PLAYER_LIST);
                            brodCastAll(request);
                        }
                            break;

//////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        
                        case Setting.REFUSE_INVITATION:

                        System.out.println("REFUSE_INVITATION");
                        objects = (Object[]) request.getObject();
                        senderPlayer = (User) objects[1];
                        receiverPlayer = (User) objects[0];

                        for (GameHandler ch : clientsVector) {
                            if (ch.user.getId() == receiverPlayer.getId()) {
                                request.setType(Setting.REFUSE_INVITATION);
                                ch.ous.writeObject(request);
                            }
                        }
                        break;

//////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                            case Setting.UPDATEPLAYER:
                            senderPlayer = (User) request.getObject();
                            this.user.setStatus(senderPlayer.getStatus());
                            ServerTicTacServer.updateServerList(senderPlayer);
                            ServerTicTacServer.usersList.refresh();
                            request.setType(Setting.UPDATE_PLAYER_IN_PLAYER_LIST);
                            brodCast(request);
                            break;
//////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                            case Setting.WIN_BY_WITHDRAWS:
//                            System.out.println("WIN_BY_WITHDRAWS");
//                            objects = (Object[]) request.getObject();
//                            User winner = (User) objects[0];
//                            User looser = (User) objects[1];
//                            for (GameHandler ch : clientsVector) {
//                                if(ch.user.getId() == winner.getId())
//                                {
//                                    ch.user.setStatus(Setting.AVAILABLE);
//                                    request.setType(Setting.WINNER);
//                                    request.setObject(winner);
//                                    winner = (User) request.getObject();
//                                    System.out.println("WIN :" +request.getType());
//                                    ch.ous.writeObject(request);                          
//                                }
//                                
//                                if(ch.user.getId() == looser.getId())
//                                {
//                                    looser = (User) request.getObject();
//                                    System.out.println("looser  :" +request.getType());
//                                    clientsVector.remove(ch);
////                                    ch.ous.close();
////                                    ch.ois.close();
//                                } 
//                            }
//                            break;

//////////////////////////////////////////////////////////////////////////////////////////////////
                    default:
                        System.out.println("defualt");
                }
/////////////////////////////////// end switch ////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////

                if (request == null) {

                            clientsVector.remove(this);
                            System.out.println("null this :" + clientsVector.size());
                            ous.close();
                            ois.close();
                            break;

                        }
//                    sendMessageToAll(request);      
                }catch (Exception ex) {
                try {
                    clientsVector.remove(this);
                    ous.close();
                    ois.close();
                    Request request1 = new Request();
                    request1.setType(Setting.DELETE_PLAYER_FROM_AVAILABLE_LIST);
                    this.user.setStatus(Setting.OUT);
                    ServerTicTacServer.updateServerList(this.user);
                    ServerTicTacServer.usersList.refresh();
                    request1.setObject(this.user);
                    brodCast(request1);
                    System.out.println(" hash map :" + playersHashMap.size());
                    System.out.println("deleted user : " + this.user.getName() + "" + this.user.getId());
                    System.out.println("player user : " + playersHashMap.get(this.user.getId()));
                    int remotePlayer = 0;
                    try {
                        if (playersHashMap.get(this.user.getId()) == (int) playersHashMap.get(this.user.getId())) {
                            remotePlayer = playersHashMap.get(this.user.getId());
                            playersHashMap.remove(remotePlayer);
                        }
                    } catch (Exception e) {
                        Set set = playersHashMap.entrySet();
                        Iterator i = set.iterator();
                        while (i.hasNext()) {
                            Map.Entry me = (Map.Entry) i.next();
                            System.out.print(me.getKey() + ": ");
                            System.out.println("" + me.getValue());
                            System.out.print(me.getKey().toString() + ": ");
                            System.out.println("" + me.getValue().toString());
                            if (Integer.parseInt(me.getValue().toString()) == this.user.getId()) {
                                remotePlayer = Integer.parseInt(me.getKey().toString());
                                playersHashMap.remove(Integer.parseInt(me.getKey().toString()));
                            }
                        }
                        System.out.println(" hash map :" + playersHashMap.size());
                    }

                    if (remotePlayer != 0) {
                        for (GameHandler ch : clientsVector) {
                            if (ch.user.getId() == remotePlayer) {
                                ch.user.setStatus(Setting.AVAILABLE);
                                request1.setObject(ch.user);
                                request1.setType(Setting.UPDATE_PLAYER_IN_PLAYER_LIST);
                                brodCast(request1);
                                Request request2 = new Request();
                                Object[] objects = {ch.user,this.user};
                                request2.setObject(objects);
                                request2.setType(Setting.WINNER);
                                ch.ous.writeObject(request2);
                            }

                        }

                    }

                    System.out.println(" this :" + clientsVector.size());
                    break;
                    //ex.printStackTrace();
                } catch (IOException ex1) {
                    ex1.printStackTrace();
                    Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            }
        }

        void brodCast
        (Request request) throws IOException {
            for (GameHandler ch : clientsVector) {
                if (ch.user.getId() != ((User) request.getObject()).getId()) {
                    ch.ous.writeObject(request);
                    ch.ous.flush();
                    ch.ous.reset();
                }
            }
        }

        void brodCastAll
        (Request request) throws IOException {
            for (GameHandler ch : clientsVector) {
                ch.ous.writeObject(request);
                ch.ous.flush();
                ch.ous.reset();
            }
        }

    

    private boolean checkWins(int[] x) {

        return ((x[0] == x[1] && x[0] == x[2] && x[0] != -1)
                || (x[0] == x[3] && x[0] == x[6] && x[0] != -1)
                || (x[0] == x[4] && x[0] == x[8] && x[0] != -1)
                || (x[6] == x[7] && x[6] == x[8] && x[6] != -1)
                || (x[6] == x[4] && x[6] == x[2] && x[6] != -1)
                || (x[2] == x[5] && x[2] == x[8] && x[2] != -1)
                || (x[1] == x[4] && x[1] == x[7] && x[1] != -1)
                || (x[3] == x[4] && x[3] == x[5] && x[3] != -1));
    }

    private boolean checkDraw(int[] x) {
        boolean draw = true;
        for (int i = 0; i < x.length; i++) {
            if (x[i] == -1) {
                draw = false;
            }
        }

        return draw;
    }


    public static void saveToFile(Image image, String name, User user) {
        File outputFile = new File("/home/kazafy/" + name);
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
            UserController.updateImage("/home/kazafy/" + name, user.getFbId());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean checkAlreadyLogin(User u) {
        for (GameHandler gameHandler : clientsVector) {
            if (gameHandler.user.getId() == u.getId()) {
                return true;
            }
        }
        return false;
    }
}

