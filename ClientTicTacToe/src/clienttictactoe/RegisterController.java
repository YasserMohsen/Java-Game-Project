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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.User;

/**
 *
 * @author kazafy
 */
public class RegisterController implements Initializable {
    
    User user ;
    
    @FXML
    private TextField name,email,rePassword,password;
    @FXML
    public Label error, errorText;
    
    
    @FXML
    public void btnActionSignUp(){
        user = new User(name.getText(),email.getText(),password.getText(),rePassword.getText());
        Client.sendRequest(user , Setting.REG);
    }
    
    @FXML
    public void btnActionLoginIn(){
        try {
            ClientTicTacToe.replaceSceneContent("login.fxml");
        } catch (Exception ex) {
            Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
    
    

    
}
