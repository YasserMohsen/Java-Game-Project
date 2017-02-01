/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertictacserver;

import java.util.concurrent.ThreadLocalRandom;
import control.UserController;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javafx.scene.control.Button;
import model.Request;
import model.User;

/**
 * @author kazafy
 *
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
                switch (request.getType()) {
//////////////////////////////////////////////////////////////////////////////////////////////////
                    case Setting.REG:
                        user = (User) request.getObject();
                        if (UserController.register(user)) {

                            // if register ok send list off available players to client
                            user.setStatus(Setting.AVAILABLE);
                            user.setId(ThreadLocalRandom.current().nextInt(1, 1000 + 1));
                            user.setId(2);
                            System.out.println("user ::" + user.getName());
                            System.out.println("user id ::" + user.getId());
                            request.setType(Setting.REG_OK);
                            List l = new ArrayList<User>();
                            for (GameHandler gameHandler : clientsVector) {
                                if (gameHandler.user.getStatus() == Setting.AVAILABLE) {
                                    l.add(gameHandler.user);
                                }
                                System.out.println("" + gameHandler.user.getName());
                            }
                            request.setObject(l);
                            this.ous.writeObject(request);
                            this.ous.flush();
                            this.ous.reset();
                            request.setType(Setting.ADD_PLAYER_TO_AVAILABLE_LIST);
                            request.setObject(user);
                            brodCast(request);
                        } else {
                            // error in registration  send to client error message
//                                
                        }
                        break;
//////////////////////////////////////////////////////////////////////////////////////////////////
                    case Setting.LOGIN:
                        user = (User) request.getObject();
                        if (UserController.login(user)) {
                            // if login ok send list off available players to client
                            request.setClientID(user.getEmail());
                            user.setStatus(Setting.AVAILABLE);
                            request.setType(Setting.LOGIN_OK);
                            List<User> l = new ArrayList<>();
                            for (GameHandler gameHandler : clientsVector) {
                                if (gameHandler.user.getStatus() == Setting.AVAILABLE && gameHandler.user != user) {
                                    l.add(gameHandler.user);
                                }
                            }

                            request.setObject(l);
                            System.out.println("" + request.getObject());

                            this.ous.writeObject(request);
                            this.ous.flush();
                            this.ous.reset();
                            request.setType(Setting.ADD_PLAYER_TO_AVAILABLE_LIST);
                            request.setObject(user);
                            brodCast(request);

                        } else {
                            // error in registration  send to client error message
                            this.ous.writeObject(request);
                        }
                        break;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    case Setting.SELECT_PLAYER_FROM_AVAILABLE_LIST:
                        System.out.println("SELECT_PLAYER_FROM_AVAILABLE_LIST");
                        request.setType(Setting.SEND_INVITATION_FOR_PLAYING);
                        System.out.println("" + request.getClientID());
                        User user = (User) request.getObject();
                        System.out.println("" + user.getEmail());
                        for (GameHandler ch : clientsVector) {
                            if (ch.user.getId() == ((User) request.getObject()).getId()) {
                                System.out.println("" + ch.user.getEmail());
                                ch.ous.writeObject(request);
                            }
                        }

                        break;
//////////////////////////////////////////////////////////////////////////////////////////////////

                    case Setting.MOVE:
                        Object[] objects = (Object[]) request.getObject();
                        User senderPlayer = (User) objects[0];
                        User receiverPlayer = (User) objects[1];
                        System.out.println("s id "+senderPlayer.getId());
                        System.out.println("r id "+receiverPlayer.getId());
                        ArrayList<Integer> xo = (ArrayList<Integer>) objects[2];
                        this.ous.flush();
                        this.ous.reset();
                        
                        System.out.println("xo " + xo.size());
                        if (checkWins(xo)) {

                            for (GameHandler ch : clientsVector) {
                                if (ch.user.getId() == senderPlayer.getId() || ch.user.getId() == receiverPlayer.getId()) {
                                    ch.user.setStatus(Setting.AVAILABLE);

                                    if (ch.user.getId() == receiverPlayer.getId()) {
                                        request.setType(Setting.LOSER);

                                        ch.ous.writeObject(request);
                                        ch.ous.flush();
                                        ch.ous.reset();
                                    }

                                }
                            }
                            System.out.println("win");
                            request.setType(Setting.WINNER);

                            this.ous.writeObject(request);
                            this.ous.flush();
                            this.ous.reset();

                            UserController.saveScore(senderPlayer);
                            
                            request.setObject(senderPlayer);
                            request.setType(Setting.ADD_PLAYER_TO_AVAILABLE_LIST);                            
                            
                            brodCast(request);
                            
                            this.ous.flush();
                            this.ous.reset();
                            
                            request.setObject(receiverPlayer);
                            request.setType(Setting.ADD_PLAYER_TO_AVAILABLE_LIST);
                            brodCast(request);

                            senderPlayer.setStatus(Setting.AVAILABLE);
                        } else {
                            
                            request.setType(Setting.MOVEBACK);
                            request.setObject(xo);
                            System.out.println("lose");

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
            } catch (Exception ex) {
                try {
                    clientsVector.remove(this);
                    System.out.println(" this :" + clientsVector.size());
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
            if (ch.user.getId() != ((User) request.getObject()).getId()) {
                ch.ous.writeObject(request);
            }
        }
    }

    private boolean checkWins(ArrayList<Integer> x) {
        return (   x.get(0) == x.get(1) && x.get(0) == x.get(2)&& x.get(0) != -1
                || x.get(0) == x.get(3) && x.get(0) == x.get(6)&& x.get(0) != -1
                || x.get(0) == x.get(4) && x.get(0) == x.get(8)&& x.get(0) != -1
                || x.get(6) == x.get(7) && x.get(6) == x.get(8)&& x.get(6) != -1
                || x.get(6) == x.get(4) && x.get(6) == x.get(2)&& x.get(6) != -1
                || x.get(2) == x.get(5) && x.get(2) == x.get(8)&& x.get(2) != -1
                || x.get(1) == x.get(4) && x.get(1) == x.get(7)&& x.get(1) != -1
                || x.get(3) == x.get(4) && x.get(3) == x.get(5)&& x.get(3) != -1);
    }

}
