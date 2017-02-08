package clienttictactoe;

import com.restfb.types.ProfilePictureSource;
import java.net.URL;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Optional;
import java.util.ResourceBundle;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import model.FacebookApi;
import model.Request;
import model.User;
import util.XCell;

/**
 * FXML Controller class
 *
 * @author kazafy
 */
public class MainController implements Initializable {

       
    Background background;
        
    @FXML
    GridPane gridPane; 
    
    
    @FXML
    public ListView<User> lv_players;
    
    @FXML
    Button btnGoOffLine; 
//    
//    @FXML
//    private TableColumn<User,String>tc_name;

    @FXML
    private BorderPane bp_GameBoard;
    
    //ListView<String> lv_chat;
    @FXML
    ListView<Label> chatArea;
    @FXML
    TextField chatField;
    @FXML
    TextArea news;
    
    @FXML
    ImageView profilePic;
    @FXML 
    Label playerName, playerScore;

    public static ObservableList<User> availableUsers = FXCollections.observableArrayList();
    ObservableList<Label> chatInstance = FXCollections.observableArrayList();

    boolean playDisable = false;
    
    private Label[][] labels = new Label[3][3];
    /// init array of empty play board
    private int[] xo = {-1, -1, -1, -1, -1, -1, -1, -1, -1};

    private int counter = 0;
    private boolean isFinish = false;
    private int status = Setting.AVAILABLE;
    
    int playerChar_X_OR_O;
    Image OPic;
    Image XPic;
    private User player;
    private User remotePlayer;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        gridPane.setPrefHeight(454.0);
        gridPane.setPrefWidth(473.0);
        gridPane.setGridLinesVisible(true);
     //   gridPane.setStyle("-fx-background-color: white;");

        

    OPic = new Image(getClass().getResourceAsStream("O.png"));
    XPic = new Image(getClass().getResourceAsStream("X.png"));
    
    //profilePic.setImage(new Image(getClass().getResourceAsStream("male.jpg")));
//        cell1 = new Label();
//        cell2 = new Label();
//        cell3 = new Label();
//        cell4 = new Label();
//        cell5 = new Label();
//        cell6 = new Label();
//        cell7 = new Label();
//        cell8 = new Label();
//        cell9 = new Label();

//        cells = new Label[]{cell1, cell2, cell3,
//            cell4, cell5, cell6,
//            cell7, cell8, cell9};
        //GridPane gridPane = new GridPane();

        for (int i = 0; i < 9; i++) {
              labels[i / 3][i % 3] = new Label();
            //System.out.print("salma"+i/3); 0,0 0,1 0,2 
            //System.out.print("salmaa"+i%3);
            
                   labels[i / 3][i % 3].setPrefSize(160, 155);
                   labels[i / 3][i % 3].setAlignment(Pos.CENTER);
            gridPane.add(labels[i / 3][i % 3], i % 3, i / 3);
            labels[i / 3][i % 3].setUserData(i);
            labels[i / 3][i % 3].setOnMouseClicked(event -> {
//                if (isFinish) {
//                    return;
//                }
           // buttons[i / 3][i % 3].setOnAction((ActionEvent event) -> {

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
                        
                       
                   
                        
                        ((Label) event.getSource()).setGraphic((playerChar_X_OR_O == 0) ? new ImageView(OPic) : new ImageView(XPic));

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

       // bp_GameBoard.setCenter(gridPane);

        // TODO
        chatArea.setItems(chatInstance);
        lv_players.setItems(availableUsers);        
        lv_players.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new XCell();
            }
        });
        

        lv_players.setOnMouseClicked((MouseEvent event) -> {
            
            System.out.println("clicked");

            remotePlayer= lv_players.getSelectionModel().getSelectedItem();

            System.out.println("not avai" + remotePlayer.getStatus());
            System.out.println("playerId:"+remotePlayer.getId());
            System.out.println("playername:"+remotePlayer.getName());
            System.out.println("playerEmail:"+remotePlayer.getEmail());
            System.out.println("playerStatus:"+remotePlayer.getStatus());
            System.out.println("playerScore:"+remotePlayer.getScore());

 
                    if (remotePlayer.getStatus() != Setting.AVAILABLE)
                        return;

                    Request request = new Request();
                    request.setType(Setting.SELECT_PLAYER_FROM_AVAILABLE_LIST);
                    Object[] objects = {player, remotePlayer};
                    request.setObject(objects);
                    Client.sendRequest(request);
                    bp_GameBoard.setDisable(true);
                    playerChar_X_OR_O = 1;
            
            
          });
        
       
//        tc_name.setCellValueFactory(new PropertyValueFactory("name"));
//        tv_Players.setOnMouseClicked(event -> {
//                    remotePlayer= tv_Players.getSelectionModel().getSelectedItem();
// 
//                    if (remotePlayer.getStatus() != Setting.AVAILABLE)
//                        return;
//
//                    Request request = new Request();
//                    request.setType(Setting.SELECT_PLAYER_FROM_AVAILABLE_LIST);
//                    Object[] objects = {player, remotePlayer};
//                    request.setObject(objects);
//                    Client.sendRequest(request);
//                    bp_GameBoard.setDisable(true);
//                    playerChar_X_OR_O = 1;
//                    
//        });
//        tv_Players.setRowFactory( tv -> {
//            TableRow<User> row = new TableRow<>();
//            row.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
//                    User user = row.getItem();
//                    
//                    remotePlayer= user;
//                    
//                    System.out.println("user name   :: "+user.getName());
//                    System.out.println("user status :: "+user.getStatus());
// 
//                    if (remotePlayer.getStatus() != Setting.AVAILABLE)
//                        return;
//
//                    Request request = new Request();
//                    request.setType(Setting.SELECT_PLAYER_FROM_AVAILABLE_LIST);
//                    Object[] objects = {player, remotePlayer};
//                    request.setObject(objects);
//                    Client.sendRequest(request);
//                    bp_GameBoard.setDisable(true);
//                    playerChar_X_OR_O = 1;                    
//                }
//            });
//            return row ;
//        });

        
        ////////////////set which property will be render in List View/////////////////////////////////
//        lv_players.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
//            @Override
//            public ListCell<User> call(ListView<User> lv) {
//                ImageView imageView = new ImageView(); 
//                return new ListCell<User>() {
//                    @Override
//                    public void updateItem(User user, boolean empty) {
//                        super.updateItem(user, empty);
//                        if (user == null) {
//                            setText(null);
//                        } else {
//                            // assume MyDataType.getSomeProperty() returns a string
//                            Image image = new Image(user.getImg());
//                            imageView.setImage(image);
//                        }
//                    }
//                };
//            }
//        });
    }

                     

//         if(playerChar_X_OR_O == 0){
//                           ((Label) event.getSource()).setGraphic(new ImageView(OPic)); 
//                        }
//                        else{
//                            ((Label) event.getSource()).setGraphic(new ImageView(XPic)); 
//                        }

    
    
//////////////////////////////////////////////////////////////////////////////////////////////
    
    public void playOff() {
     
        
        new ComputerWithGui().start(ClientTicTacToe.getStage());
    }   
    
    public void btnActionChangeStatus(){
            
            chatField.clear();
            Request request = new Request();
            request.setType(Setting.UPDATEPLAYER);
            player.setStatus((player.getStatus()==Setting.AVAILABLE)? Setting.OFFLINE : Setting.AVAILABLE);
            request.setObject(player);
            Client.sendRequest(request);

            btnGoOffLine.setText((player.getStatus()==Setting.AVAILABLE)? Setting.GOOFLINE : Setting.GOOFLINE);
    
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
        alert.showAndWait();

    }

    public void setDisable_Enable_MainView(boolean bool) {
        bp_GameBoard.setDisable(bool);
    }

    public void setDisable_Enable_ListView(boolean bool) {
        lv_players.setDisable(bool);
    }
    public void setDisable_Enable_ChatView(boolean bool) {
        chatInstance.clear();
        chatField.clear();
        chatArea.setDisable(bool);
        chatField.setDisable(bool);
    }
    public void setMyImage(Image i){
        profilePic.setImage(i);
    }
    public void setMyName(String name){
        playerName.setText(name);
    }
    public void setMyScore(int score){
        playerScore.setText(score + "");
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
            labels[i / 3][i % 3].setGraphic(null);
            
        }
    }

    int showWinDialog(String m) {
            int res = 0;
                
            Alert alert= ConfirmDialoge.createCustomDialog("",m,"");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ConfirmDialoge.buttonTypeOne) {                
                res = 1;
            } else if (result.isPresent() && result.get() == ConfirmDialoge.buttonTypeTwo)  {
                FacebookApi.shareMSG("quote", "http://stackoverflow.com/questions/997482/does-java-support-default-parameter-values", "name", "desc", "caption", "popup", "https://scontent-cai1-1.xx.fbcdn.net/v/t1.0-9/282864_10200182327274552_1193770825_n.jpg?oh=76c7d186e66cbd88db52240420715116&oe=59111D89");
                res = 2;
            }
            else if (result.isPresent() && result.get() == ConfirmDialoge.buttonTypeCancel)  {
//            getPlayer().setStatus(Setting.AVAILABLE);
//            getRemotePlayer().setStatus(Setting.AVAILABLE);
////            lv_players.refresh();
//                lv_players.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
//            @Override
//            public ListCell<User> call(ListView<User> param) {
//                return new XCell();
//            }
//        });
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
    
    @FXML
    public void changePic(){
        
    }
    
    public void selectUser(){
        


        
    }
    
    

}
