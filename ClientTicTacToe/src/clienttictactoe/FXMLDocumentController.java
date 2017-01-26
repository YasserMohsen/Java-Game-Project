/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttictactoe;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Request;
import model.User;

/**
 *
 * @author kazafy
 */
public class FXMLDocumentController implements Initializable {
    
    User user ;
    Client client;
    
    @FXML
    private TextField name,email,rePassword,password;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
    }
    
    @FXML
    public void btnActionSignUp(){
        
        try {
            user = new User(name.getText(),email.getText(),password.getText());
            Request request = new Request();
            request.setType(1);
            request.setObject(user);
           
            
            client.ous.writeObject(request);
            client.ous.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        client = new Client();
    }    
    
    
    
    class Client {

        Socket mySocket;
        ObjectOutputStream ous;
        ObjectInputStream ois;
        PrintStream ps;

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
                         ois.readObject();
//                        System.out.println("read "+user.getName());

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

    
}
