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
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
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
    
    @FXML
    private void selectUser(){
       User user =  lv_availableUsers.getSelectionModel().getSelectedItem();
        System.out.println("user ::"+user.getName());
       Client.sendRequest(user, Setting.SELECT_PLAYER_FROM_AVAILABLE_LIST);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lv_availableUsers.setItems(availableUsers);
        
        ////////////////set which property will be render in List View/////////////////////////////////
        lv_availableUsers.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> lv) {
                return new ListCell<User>() {
                    @Override
                    public void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        if (user == null) {
                            setText(null);
                        } else {
                            // assume MyDataType.getSomeProperty() returns a string
                            setText(user.getEmail());
                        }
                    }
                };
            }
        });
    }    
    
}
