/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttictactoe;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import model.FacebookApi;
import model.User;

/**
 *
 * @author kazafy
 */
public class RegisterController implements Initializable {
    
    User user ;
   // String s="sss";
    @FXML
    public GridPane reggrid; 
    @FXML
    public Label errorText,nameerror, mailerror,passerror,repasserror;
    @FXML
    public TextField name,email;
    @FXML
    public PasswordField password,repassword;
    @FXML
    public Label errorsalma;
    @FXML
    public void btnActionSignUp(){
       //System.out.print("emo"+email.getText());
       if(!password.getText().equals(repassword.getText())){
           ClientTicTacToe.home.registerController.repasserror.setVisible(true);
                        repasserror.setTextFill(Color.RED);
                        repasserror.setText("*Passwords aren't matching");
                        password.setText("");
                        repassword.setText("");
           return;
       }
       else{
        user = new User(name.getText(),email.getText(),password.getText());
            
        //create a new client and send the first request (register) in its constructor
        Client c = new Client(user , Setting.REG);
    }
    }
    @FXML
    public void btnActionLoginIn(){
        try {
            ClientTicTacToe.replaceSceneContent("login.fxml","login");
        } catch (Exception ex) {
            Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
    
    public void changeText(){
        
        name.setText(" ggooo ");
        System.out.println("clienttictactoe.RegisterController.changeText()");
    
    }
    public void erasemail(){
        mailerror.setVisible(false);
        System.out.print("lalala"+mailerror.getText());
    }
     public void erasename(){
        nameerror.setVisible(false);
    }
      public void erasepass(){
        passerror.setVisible(false);
    }
       public void eraserepass(){
        repasserror.setVisible(false);
    }
}
