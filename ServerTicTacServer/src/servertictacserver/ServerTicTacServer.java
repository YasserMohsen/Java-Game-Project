/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertictacserver;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.Scene;
import javafx.scene.control.Button;
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
        Button start = new Button("Start The Server");
        Button stop = new Button("Stop The Server");
        Pane root = new Pane();
        root.setId("pane");
        
        r.setFill(lg1);
        
        rf.setFraction(0.7);
  
        t.setEffect(rf);
        t.setLayoutX(90);
        t.setLayoutY(60);
        
        start.setLayoutX(15);
        start.setLayoutY(100);
        stop.setLayoutX(155);
        stop.setLayoutY(100);
        
        start.setOnAction(new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
                if(flag == 0){
                    s = new Server();
                    s.start();
                    flag = 1;
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
                    } catch (IOException ex) {
                        System.out.println("close server exception");
                    }
                }
                else{
                    System.out.println("the server already stopped");
                }
            }
    });
        
        
        
        
        root.getChildren().addAll(start, stop, t);
        Scene scene = new Scene(root, 300, 250);
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
