/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttictactoe;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.Label ; 
import model.User;

/**
 *
 * @author Habib
 */
public class LoginController implements Initializable {
    
    User user;
    
    @FXML
    TextField email,password;
    
    @FXML
    public Label errorsalma;
    @FXML
    public void loginBt(){
        user = new User(email.getText(), password.getText());
        //create a new client and send the first request (login) in its constructor
        Client c = new Client(user , Setting.LOGIN);       
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
      
}
