/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertictacserver;


import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.Reflection;

import javafx.scene.layout.Pane;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


import javafx.stage.Stage;

/**
 *
 * @author kazafy
 */
public class ServerTicTacServer extends Application {
    Server s;
     int flag=0;
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
        Pane root = new Pane();
        ScrollPane pane = new ScrollPane();
        root.setId("pane");
        
        ListView<String> list = new ListView<String>();
        ObservableList<String> items =FXCollections.observableArrayList (
        "sada","jlkjlk" , "uiooij","fdsf", "sdfsfs", "fdsfs","sada", "fdsf", "sdfsfs", "fdsfs","sada", "fdsf", "sdfsfs", "fdsfs","sada", "fdsf", "sdfsfs", "fdsfs");
        list.setItems(items);
        
        
        
        pane.prefWidthProperty().bind(list.widthProperty());
        pane.prefHeightProperty().bind(list.heightProperty());
        pane.setContent(list);
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
        list.setMinSize(600, 80);
        
        start.setLayoutX(265);
        start.setLayoutY(50);
        stop.setLayoutX(405);
        stop.setLayoutY(50);
         
        
        
        start.setOnAction(new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
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
                if(flag == 1){
                    try {
                        s.serverSocket.close();
                        s.stop();
                        for (GameHandler ch : GameHandler.clientsVector) {
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
        
        
        
        
        root.getChildren().addAll(start, stop, t, show, list, pane);
        Scene scene = new Scene(root, 800, 520);
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
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
