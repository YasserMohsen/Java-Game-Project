/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertictacserver;


import control.UserController;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Reflection;

import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


import javafx.stage.Stage;
import javafx.util.Callback;
import model.User;
import utilServer.YCell;

/**
 *
 * @author kazafy
 */
public class ServerTicTacServer extends Application {

    Server s;
     int flag = 0;
     static ObservableList<User> items = FXCollections.observableArrayList ();
     
     static ListView<User> usersList;
    @Override
    public void start(Stage stage) throws Exception {
       // Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        stage.setResizable(false);
        Stop[] stops = new Stop[]{new Stop(0, javafx.scene.paint.Color.DARKORANGE), new Stop(1, javafx.scene.paint.Color.DARKSLATEGRAY)};
        LinearGradient lg1 = new LinearGradient(0, 0, 300, 250, false, CycleMethod.REPEAT, stops);
        Reflection rf = new Reflection();
        Rectangle r =new Rectangle(0,0,300, 250);
        Text t = new Text("TicTacToe Server");
        Text show=new Text("Waiting For Choice");
        Button start = new Button("Start The Server");
        Button stop = new Button("Stop The Server");
        Button sort = new Button("sort scores");
        Button sort2 = new Button("sort status");
        Pane root = new Pane();
        ScrollPane pane = new ScrollPane();
        root.setId("pane");
        

        usersList=new ListView<>();
        UserController.loadUsers(items);
        usersList.setItems(items);    
        usersList.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                
                return new YCell();
            }
        });
      //  Collections.sort(items, Comparator.comparing(s -> s.getScore()));
        final AudioClip ac = new AudioClip(ServerTicTacServer.class.getResource("btnclick.wav").toString());
        pane.prefWidthProperty().bind(usersList.widthProperty());
        pane.prefHeightProperty().bind(usersList.heightProperty());
        pane.setContent(usersList);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        
        r.setFill(lg1);
        
        rf.setFraction(0.2);
  
        t.setEffect(rf);
        t.setLayoutX(325);
        t.setLayoutY(35);
        t.setStyle("-fx-font: 16 arial;");
        
        show.setLayoutX(338);
        show.setLayoutY(100);
        
        pane.setLayoutX(100);
        pane.setLayoutY(110);
        usersList.setMinSize(600, 80);
        
        start.setLayoutX(265);
        start.setLayoutY(50);
        stop.setLayoutX(405);
        stop.setLayoutY(50);
        sort.setLayoutX(605);
        sort.setLayoutY(50);
         sort2.setLayoutX(100);
         sort2.setLayoutY(50);
        
        
        
        
        sort.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Collections.sort(items, Comparator.comparing(s -> s.getScore()));
                Collections.reverse(items);
            }
        });
        sort2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Collections.sort(items, Comparator.comparing(s -> s.getStatus()));
                Collections.reverse(items);
            }
        });

        start.setOnAction(new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            
            ac.play();
            
            if(flag == 0){
                    s = new Server();
                    s.start();
                    flag = 1;
                    show.setText("The Server Is On");
                }
                else{
                    System.out.println("the server already started");
                }
            }
    });
        
        stop.setOnAction(new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            
            ac.play();
            
            if(flag == 1){
                    try {
                        s.serverSocket.close();
                        s.stop();
                        for (GameHandler ch : GameHandler.clientsVector) {
                            ch.user.setStatus(Setting.OUT);
                            ServerTicTacServer.updateServerList(ch.user);
                            ServerTicTacServer.usersList.refresh();
                            ch.ois.close();
                            ch.ous.close();
                            ch.stop();
                        }
                        GameHandler.clientsVector.clear();
  
                        flag = 0;
                        show.setText("The Server Is Off");
                    } catch (IOException ex) {
                        System.out.println("close server exception");
                    }
                }
                else{
                    System.out.println("the server already stopped");
                }
            }
    });
        
        
        
        
        root.getChildren().addAll(start, stop,sort,sort2, t, show, usersList, pane);
        Scene scene = new Scene(root, 800, 520);
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    public static void updateServerList(User user){
        for (User u : items){
            if (user.getId() == u.getId()){
                u.setStatus(user.getStatus());
                //u.setScore(user.getScore());
            }                                   
        }
    }
    public static void updateServerListScore(User user){
        for (User u : items){
            if (user.getId() == u.getId()){
                //u.setStatus(user.getStatus());
                u.setScore(user.getScore());
            }                                   
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      //  Server lol = new Server();
       
      //  lol.start();
        Application.launch(args);
    }
    
}
