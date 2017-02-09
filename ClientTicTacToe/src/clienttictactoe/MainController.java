package clienttictactoe;

import java.net.URL;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.media.AudioClip;
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
    
    boolean offLineMode = false;
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
    ListView<Label> news;

    @FXML
    Label turnStatus;

    
    @FXML
    ImageView profilePic;
    @FXML 
    Label playerName, playerScore;

    final AudioClip ac = new AudioClip(MainController.class.getResource("note-high.wav").toString());
    final AudioClip ab = new AudioClip(MainController.class.getResource("note-low.wav").toString());
    final AudioClip ad = new AudioClip(MainController.class.getResource("game-over.wav").toString());
    final AudioClip ae = new AudioClip(MainController.class.getResource("game-over-tie.wav").toString());
    
    public static ObservableList<User> availableUsers = FXCollections.observableArrayList();
    ObservableList<Label> chatInstance = FXCollections.observableArrayList();
    ObservableList<Label> newsInstance = FXCollections.observableArrayList();
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
    Timer timer;
    ComputerWithGui computerWithGui = new ComputerWithGui();
    Board board = new Board();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        gridPane.setPrefHeight(454.0);
        gridPane.setPrefWidth(473.0);
        gridPane.setGridLinesVisible(true);

    OPic = new Image(getClass().getResourceAsStream("O.png"));

    XPic = new Image(getClass().getResourceAsStream("X.png"));


        for (int i = 0; i < 9; i++) {
              labels[i / 3][i % 3] = new Label();
            
                   labels[i / 3][i % 3].setPrefSize(160, 155);
                   labels[i / 3][i % 3].setAlignment(Pos.CENTER);
            gridPane.add(labels[i / 3][i % 3], i % 3, i / 3);
            labels[i / 3][i % 3].setUserData(i);
            
            labels[i / 3][i % 3].setOnMouseClicked(event -> {
                
                if(offLineMode){
                    

                int position = Integer.parseInt(((Label) event.getSource()).getUserData().toString());
                ///  click in an empty position 
                if (xo[position] == -1) {
                    counter++;
                    
                    if (!playDisable) {
                        xo[position] = playerChar_X_OR_O;
                        for (int j = 0; j <9; j++) {
                            System.out.println("xo :"+xo[j]);
                        }
                        
                        board.placeAMove(new Point(position / 3, position % 3), 2);
                        
                        board.displayBoard();/// print to console
                        ((Label) event.getSource()).setGraphic((playerChar_X_OR_O == 0) ? new ImageView(OPic) : new ImageView(XPic));

                        int next = board.returnNextMove();
                        System.out.println(" next move ::: "+next);
                        if (next != -1) {   //If the game isn't finished yet!   
                            int indexCell = next;
                            xo[indexCell] = 1;
                            ac.play();
                            Timer timer = new Timer();
                            timer.schedule(
                                    new java.util.TimerTask() {
                                        @Override
                                        public void run() {
                                            Platform.runLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(offLineMode){
                                                        System.out.println("go to next move runLater ");
                                                        labels[indexCell/3][indexCell%3].setGraphic(new ImageView(XPic));
    //                                                    ((Label) event.getSource()).setGraphic((playerChar_X_OR_O == 0) ? new ImageView(OPic) : new ImageView(XPic));
                                                        playDisable = false;
                                                        ab.play();
                                                        turnStatus.setText("your Turn");
                                                    }
                                                }

                                            });

                                        }
                                    },
                                    1500
                            );

                            board.placeAMove(new Point(indexCell / 3, indexCell % 3), 1);

                        playDisable = true;
                        turnStatus.setText("computer's Turn");
                                                                  
                        }
                        
                        if (board.isGameOver()) {
                             
                            ad.play();
                            ae.play();
                            if (board.hasXWon()) {
                                showDialog(Setting.LOSE_MSG);
                            }else {
                                showDialog(Setting.DRAW_MSG);
                              }
                            
                            resetGame();
                            board.resetBoard();
                           
                        }

                   
                }
                    
                
                }
                    return;
                
                }
                
                
                
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
                        ac.play();
                        ab.play();
                    playDisable = true;
                    turnStatus.setText(remotePlayer.getName()+"'s Turn");
                       
                   
                        
                        ((Label) event.getSource()).setGraphic((playerChar_X_OR_O == 0) ? new ImageView(OPic) : new ImageView(XPic));
                    }

                   
                }
                
            });
            
        }

       // bp_GameBoard.setCenter(gridPane);

        // TODO
        chatArea.setItems(chatInstance);
        lv_players.setItems(availableUsers); 
        news.setItems(newsInstance);
       // lv_players.setPrefHeight(50);
        lv_players.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new XCell();
            }
        });
        

        lv_players.setOnMouseClicked((MouseEvent event) -> {
            
            System.out.println("clicked");

            remotePlayer= lv_players.getSelectionModel().getSelectedItem();
 
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
       
    }
    
//////////////////////////////////////////////////////////////////////////////////////////////
    
    public void playOff() {
     
        offLineMode = !offLineMode;
        resetGame();
        btnGoOffLine.setText((offLineMode)?"GO ONLINE":"GO OFFLINE");
        Request request = new Request();
        request.setType(Setting.UPDATEPLAYER);
        player.setStatus((player.getStatus()==Setting.AVAILABLE)? Setting.OFFLINE : Setting.AVAILABLE);
        request.setObject(player);
        Client.sendRequest(request);
        
        lv_players.setDisable(offLineMode);
        btnGoOffLine.setText((player.getStatus()==Setting.AVAILABLE)? Setting.GOOFLINE : Setting.GOOFLINE);
        
    }   
        

    public void updateCell(int[] xo) {
        playDisable = false;
        turnStatus.setText("Your Turn");
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
        board.resetBoard();
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
            playDisable = true;
            return res;
    }
    
    @FXML
    public void sendBt(){
        String myText = chatField.getText();
        if (myText != "" &&  remotePlayer!= null){
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
