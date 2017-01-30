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
import model.User;

/**
 *
 * @author Habib
 */
public class LoginController implements Initializable {
    
    private User user;
    
    @FXML
    private TextField email,password;
    
    
    @FXML
    public void loginBt(){
        

        user = new User(email.getText(), password.getText(),"","");
        Client.sendRequest(user, Setting.LOGIN);
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
    
    

    
}
