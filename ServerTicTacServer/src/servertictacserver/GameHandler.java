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
 * @author kazafy
 */
class GameHandler extends Thread {

    ObjectInputStream ois;
    ObjectOutputStream ous;
    User user = null;

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
                if (request.getType() == Setting.REG || request.getType() == Setting.LOGIN || request.getType() == Setting.FBLOG ){
                    user = (User) request.getObject();
                    if (request.getType()==Setting.LOGIN) {
                        user = UserController.login(user);
                    }
                    else if(request.getType()==Setting.REG){
                        user = UserController.register(user);
                    }
                    else if(request.getType()==Setting.FBLOG){
                        user = UserController.fbLogin(user);
                    }
                    
                    if(user.getId() != 0){
                        clientsVector.add(this);
                        // if register or login is ok send list off available players to client                                
                        this.ous.flush();
                        this.ous.reset();
                        user.setStatus(Setting.AVAILABLE);
                        request.setType(Setting.REG_OK);  
                        
                        List availablePlayerList = new ArrayList<User>();                                
                        for (GameHandler gameHandler : clientsVector){
                            if(gameHandler.user != null && gameHandler.user.getId()!=user.getId())
                                availablePlayerList.add(gameHandler.user);
                        }
                        Object [] objects = {availablePlayerList,user};
                        request.setObject(objects);
                        this.ous.writeObject(request);
                        this.ous.flush();
                        this.ous.reset();
                        request.setType(Setting.ADD_PLAYER_TO_AVAILABLE_LIST);
                        request.setObject(user);
                        brodCast(request);
                        start();
                    } 
                    else{

                        // error in registration or login >> send to client error message
                        if(request.getType()== Setting.REG){
                            
                        request.setType(Setting.REG_NO);                                    
                        request.setObject("email already exist");
                        }
                        else if(request.getType()== Setting.LOGIN){
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
                System.out.println("my type is : "+ request.getType());
//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////// switch ////////////////////////////////////////////////
                switch (request.getType()) {
//////////////////////////////////////////////////////////////////////////////////////////////////
                    case Setting.MOVE:
                        Object[] objects = (Object[]) request.getObject();
                        User senderPlayer = (User) objects[0];
                        User receiverPlayer = (User) objects[1];
                        System.out.println("s id "+senderPlayer.getId());
                        System.out.println("r id "+receiverPlayer.getId());
                        int []xo =  (int[]) objects[2];
                        boolean win  = false;
                        boolean draw = false;
                        
                        if(checkWins(xo)){
                            win = true;
                        }else if(checkDraw(xo)){
                            draw = true;
                        }
                        
                        if (win || draw) {

                            for (GameHandler ch : clientsVector) {
                                if (ch.user.getId() == senderPlayer.getId() || ch.user.getId() == receiverPlayer.getId()) {
                                    ch.user.setStatus(Setting.AVAILABLE);
                                    if (ch.user.getId() == receiverPlayer.getId()) {
                                        request.setType((win)?Setting.LOSER:Setting.DRAW);
                                        request.setObject(xo);
                                        ch.ous.writeObject(request);
                                        ch.ous.flush();
                                        ch.ous.reset();
                                    }

                                }
                            }
                            request.setType((win)?Setting.WINNER:Setting.DRAW);
                            this.ous.writeObject(request);
                            this.ous.flush();
                            this.ous.reset();

                            senderPlayer.setScore((win)?senderPlayer.getScore()+Setting.POINTS:senderPlayer.getScore());

                            UserController.saveScore(senderPlayer);
                            
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
                            if(win)
                                request.setObject("**" + senderPlayer.getName() + " WINS " + receiverPlayer.getName() + "\n ================= \n");
                            else
                                request.setObject("**" + senderPlayer.getName() + " draw " + receiverPlayer.getName()+"\n ================= \n");
                            brodCastAll(request);
                        } else if(checkDraw(xo)){
                            
                            // draw
                            }
                        else{
                            
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
                                if(ch.user.getId()== receiverPlayer.getId())
                                {
                                    System.out.print("hi from  "+receiverPlayer.getName());
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
                            
                            for (GameHandler ch : clientsVector) {
                                if(ch.user.getId() == receiverPlayer.getId())
                                {
                                    ch.user.setStatus(Setting.BUSY); /// update player status in vector
                                    request.setType(Setting.ACCEPT_INVITATION);
                                    ch.ous.writeObject(request);
                                    ch.ous.flush();
                                    ch.ous.reset();                            
                                }else if(ch.user.getId() == senderPlayer.getId()){
                                    ch.user.setStatus(Setting.BUSY); /// update player status in vector
                                }       
                            }
                            ((User) objects[1]).setStatus(Setting.BUSY);
                            ((User) objects[0]).setStatus(Setting.BUSY);
                            
                          //  System.out.println("Busy ::" +((User) objects[0]).getStatus());
                           // System.out.println("Busy ::" +((User) objects[1]).getStatus());
                            request.setType(Setting.UPDATE_2PLAYER_IN_PLAYER_LIST);
                            brodCastAll(request);
                            
                            break;
                            

//////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            case Setting.REFUSE_INVITATION:
                            
                            System.out.println("REFUSE_INVITATION");
                            objects = (Object[]) request.getObject();
                            senderPlayer = (User) objects[1];
                            receiverPlayer = (User) objects[0];
                            
                            for (GameHandler ch : clientsVector) {
                                if(ch.user.getId() == receiverPlayer.getId())
                                {
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
                            request.setType(Setting.UPDATE_PLAYER_IN_PLAYER_LIST);
                            brodCast(request);
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
                    //ex.printStackTrace();
                    break;
                } catch (IOException ex1) {
                    System.out.println("try to close failed");
                    ex1.printStackTrace();
                }
            }
        }
    }

    void brodCast(Request request) throws IOException {
        for (GameHandler ch : clientsVector) {
            if (ch.user.getId() != ((User) request.getObject()).getId()) {
                ch.ous.writeObject(request);
                ch.ous.flush();
                ch.ous.reset();
            }
        }
    }
    void brodCastAll(Request request) throws IOException {
        for (GameHandler ch : clientsVector) {
            ch.ous.writeObject(request);
            ch.ous.flush();
            ch.ous.reset();
        }
    }

    private boolean checkWins(int [] x) {
        
        return (   (x[0] == x[1] && x[0] == x[2] && x[0] != -1)
                || (x[0] == x[3] && x[0] == x[6] && x[0] != -1)
                || (x[0] == x[4] && x[0] == x[8] && x[0] != -1)
                || (x[6] == x[7] && x[6] == x[8] && x[6] != -1)
                || (x[6] == x[4] && x[6] == x[2] && x[6] != -1)
                || (x[2] == x[5] && x[2] == x[8] && x[2] != -1)
                || (x[1] == x[4] && x[1] == x[7] && x[1] != -1)
                || (x[3] == x[4] && x[3] == x[5] && x[3] != -1)
                );
    }
    private boolean checkDraw(int []x){
        boolean draw =true;
        for (int i=0;i<x.length;i++)
        {
            if (x[i]==-1)
                draw=false;
        }
    
        return draw;
    }
    
}
