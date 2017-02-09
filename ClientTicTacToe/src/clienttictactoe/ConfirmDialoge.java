/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttictactoe;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

/**
 *
 * @author kazafy
 */
public class ConfirmDialoge extends Alert{
    
    public static ButtonType buttonTypeOne = new ButtonType("play again");
    public static ButtonType buttonTypeTwo = new ButtonType("share");
    public static ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    public ConfirmDialoge(AlertType alertType) {
        super(alertType);
        
    }

    public static Alert createCustomDialog(String title , String headerText , String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        // Create the custom dialog.

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);
      
        return alert;

    }

    
}
