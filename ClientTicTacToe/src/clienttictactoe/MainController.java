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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    public static ObservableList<User> availableUsers = FXCollections.observableArrayList();

    private boolean playDisable = false;
    private Label[][] labels = new Label[3][3];
    /// init array of empty play board
    private int[] xo = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
    Image OPic = new Image(getClass().getResourceAsStream("O.png"));
    Image XPic = new Image(getClass().getResourceAsStream("X.png"));
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
        Label   cell1, cell2, cell3,
                cell4, cell5, cell6,
                cell7, cell8, cell9;
        Label[] cells;

        cell1 = new Label();
        cell2 = new Label();
        cell3 = new Label();
        cell4 = new Label();
        cell5 = new Label();
        cell6 = new Label();
        cell7 = new Label();
        cell8 = new Label();
        cell9 = new Label();
        cells = new Label[]{cell1, cell2, cell3,
            cell4, cell5, cell6,
            cell7, cell8, cell9};
        
        for (Label cell : cells) {
            cell.setMinSize(128, 128);
            boolean isUsed = false;
            cell.setUserData(isUsed);
        }
        gridPane.addRow(0, cell1, cell2, cell3);
        gridPane.addRow(1, cell4, cell5, cell6);
        gridPane.addRow(2, cell7, cell8, cell9);
        
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setMaxSize(800, 800);
        gridPane.setGridLinesVisible(true);
        gridPane.setId("board");
        
        
        cell1.setGraphic(new ImageView(XPic));

       for (int i = 0; i < 9; i++) {
            labels[i / 3][i % 3] = new Label("");
            gridPane.add(labels[i / 3][i % 3], i % 3, i / 3);
            labels[i / 3][i % 3].setUserData(i);
            labels[i / 3][i % 3].setOnMouseClicked((event -> {

                if (remotePlayer == null) {
                    showDialog("please select player first");
                    return;
                    }

                int position = Integer.parseInt(((Label) event.getSource()).getUserData().toString());
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
                        ((Label) event.getSource()).setGraphic((playerChar_X_OR_O == 0) ? new ImageView(XPic) : new ImageView(OPic));
                      
///////////////////////////////////////////////////////////////////////////////////////////////////
                    }
//                        if (checkWins()){
//                            isFinish=true;
//                            System.out.println(" plyer number "+((playDisable)?" 1 ":" 2 ") +" win");
//                        }                        

                    playDisable = true;
                }
            }));
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
                labels[i / 3][i % 3].setGraphic(new ImageView(XPic));
            } else if (xo[i] == 0) {
                labels[i / 3][i % 3].setGraphic(new ImageView(OPic));
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
    public void setPlayer(User player) {
        this.player = player;
    }

    public void setRemotePlayer(User player) {
        this.remotePlayer = player;
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
            labels[i / 3][i % 3].setGraphic(new ImageView());
            
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
            ClientTicTacToe.mainController.resetGame();
            remotePlayer= null;
            return res;
    }
    
    

}
