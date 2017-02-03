/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servertictacserver;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.layout.Pane;


import javafx.stage.Stage;

/**
 *
 * @author kazafy
 */
public class ServerTicTacServer extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
       // Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        int flag=0;
        Server s = new Server();
        Button start = new Button("Start The Server");
        Button stop = new Button("Stop The Server");
        start.setLayoutX(15);
        start.setLayoutY(100);
        stop.setLayoutX(155);
        stop.setLayoutY(100);
        
        start.setOnAction(new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
          s.start();    
        }
    });
        
        stop.setOnAction(new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent event) {
            s.stop();
        }
    });
        
        
        Pane root = new Pane();
        
        root.getChildren().add(start);
        root.getChildren().add(stop);
        
        
        stage.setScene(new Scene(root, 300, 250));
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
