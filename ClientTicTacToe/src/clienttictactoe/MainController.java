/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttictactoe;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import model.User;

/**
 * FXML Controller class
 *
 * @author kazafy
 */
public class MainController implements Initializable {

    @FXML
    ListView<User> lv_availableUsers;
    
    public static ObservableList<User> availableUsers = FXCollections.observableArrayList();
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lv_availableUsers.setItems(availableUsers);
    }    
    
}
