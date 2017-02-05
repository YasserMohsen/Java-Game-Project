/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttictactoe;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.Request;
import model.User;

/**
 * FXML Controller class
 *
 * @author kazafy
 */
public class MainController implements Initializable {

    @FXML
    private ListView<User> lv_availableUsers;

    @FXML
    private BorderPane bp_GameBoard;
    @FXML
    TextArea chatArea;
    @FXML
    TextField chatField;
    

    public static ObservableList<User> availableUsers = FXCollections.observableArrayList();

    private boolean playDisable = false;
    private Button[][] buttons = new Button[3][3];
    /// init array of empty play board
    private int[] xo = {-1, -1, -1, -1, -1, -1, -1, -1, -1};

    private int counter = 0;
    private boolean isFinish = false;

    int playerChar_X_OR_O;

    private User player;
    private User remotePlayer;

    @FXML
    private void selectUser() {

        remotePlayer = lv_availableUsers.getSelectionModel().getSelectedItem();
        Request request = new Request();
        request.setType(Setting.SELECT_PLAYER_FROM_AVAILABLE_LIST);
        Object[] objects = {player, remotePlayer};
        request.setObject(objects);
        Client.sendRequest(request);
        bp_GameBoard.setDisable(true);
        playerChar_X_OR_O = 1;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        GridPane gridPane = new GridPane();

        for (int i = 0; i < 9; i++) {
            buttons[i / 3][i % 3] = new Button("");
            gridPane.add(buttons[i / 3][i % 3], i % 3, i / 3);
            buttons[i / 3][i % 3].setUserData(i);
            buttons[i / 3][i % 3].setOnAction((ActionEvent event) -> {

                if (remotePlayer == null) {
                    showDialog("please select player first");
                    return;
                    }

                int position = Integer.parseInt(((Button) event.getSource()).getUserData().toString());
                ///  click in an empty position 
                if (xo[position] == -1) {
                    counter++;
                    if (!playDisable) {
                        xo[position] = playerChar_X_OR_O;

///////////////////////////////////////// prepare move request ///////////////////////////////// 
                        Request request = new Request();
                        request.setType(Setting.MOVE);
                        Object[] objects = {player, remotePlayer, xo};
                        System.out.println(" id r :" + player.getId());
                        System.out.println(" id n :" + remotePlayer.toString());
                        request.setObject(objects);
                        Client.sendRequest(request);
                        ((Button) event.getSource()).setText((playerChar_X_OR_O == 0) ? "O" : "X");

///////////////////////////////////////////////////////////////////////////////////////////////////
                    }
//                        if (checkWins()){
//                            isFinish=true;
//                            System.out.println(" plyer number "+((playDisable)?" 1 ":" 2 ") +" win");
//                        }                        

                    playDisable = true;
                }
            });
        }

        bp_GameBoard.setCenter(gridPane);

        // TODO
        lv_availableUsers.setItems(availableUsers);

//        ////////////////set which property will be render in List View/////////////////////////////////
//        lv_availableUsers.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
//            @Override
//            public ListCell<User> call(ListView<User> lv) {
//                return new ListCell<User>() {
//                    @Override
//                    public void updateItem(User user, boolean empty) {
//                        super.updateItem(user, empty);
//                        if (user == null) {
//                            setText(null);
//                        } else {
//                            // assume MyDataType.getSomeProperty() returns a string
//                            setText(user.getEmail());
//                        }
//                    }
//                };
//            }
//        });
    }

    public void updateCell(int[] xo) {
        playDisable = false;
        for (int i = 0; i < 9; i++) {
            this.xo[i] = (xo[i]);
            if (xo[i] == 1) {
                buttons[i / 3][i % 3].setText("x");
            } else if (xo[i] == 0) {
                buttons[i / 3][i % 3].setText("o");
            }
        }
    }

    public void showDialog(String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invitation Request");
        alert.setContentText(message);
        alert.show();

    }

    public void setDisable_Enable_MainView(boolean bool) {
        bp_GameBoard.setDisable(bool);
    }

    public void setDisable_Enable_ListView(boolean bool) {
        lv_availableUsers.setDisable(bool);
    }
    public void setDisable_Enable_ChatView(boolean bool) {
        chatArea.clear();
        chatField.clear();
        chatArea.setDisable(bool);
        chatField.setDisable(bool);
    }
    public void setPlayer(User player) {
        this.player = player;
    }

    public void setRemotePlayer(User player) {
        this.remotePlayer = player;
    }
    public User getRemotePlayer() {
        return remotePlayer;
    }

    public void setPlayerId(int id) {
        this.player.setId(id);
    }
    
    public User getPlayer(){
        return player;
    }

    void resetGame() {
        for (int i = 0; i < xo.length; i++) {
            xo[i]=-1;
            buttons[i / 3][i % 3].setText("");
            
        }
        
    }

    int showWinDialog(String m) {
            int res = 0;
                
            Alert alert= ConfirmDialoge.createCustomDialog("",m,"");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ConfirmDialoge.buttonTypeOne) {                
                res = 1;
            } else if (result.isPresent() && result.get() == ConfirmDialoge.buttonTypeTwo)  {
                res = 2;
            }
            ClientTicTacToe.mainController.setDisable_Enable_ListView(false);
            ClientTicTacToe.mainController.setDisable_Enable_ChatView(true);
            ClientTicTacToe.mainController.resetGame();
            remotePlayer= null;
            return res;
    }
    
    @FXML
    public void sendBt(){
        String myText = chatField.getText();
        if (myText != "" && remotePlayer != null){
            chatField.clear();
            Request request = new Request();
            request.setType(Setting.MESSAGE);
            Object[] objects = {player, remotePlayer, myText};
            request.setObject(objects);
            Client.sendRequest(request);
        }
    }
    
    

}
