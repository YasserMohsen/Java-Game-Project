/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author kazafy
 */
public class ClientTicTacToe extends Application {

    private static Stage stage;
    public static final String LOGIN_XML="login.fxml";
    public static final String MAIN_XML="main.fxml";
    
    public static RegisterController registerController;
    
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClientTicTacToe.class.getResource("register.fxml"));      
        Parent root = loader.load();
        registerController  = (RegisterController)loader.getController();
        //registerController.email.setText("lol");

        this.stage = stage;
        Scene scene = new Scene(root);
        
        this.stage.setScene(scene);
        this.stage.hide();
        this.stage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Client();
        launch(args);
    }
    
    public static Parent replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClientTicTacToe.class.getResource(fxml));      
        Parent page = loader.load();
        //MainController controller = (MainController)loader.getController();
        //controller.email.setText("lol");
    
        Scene scene = stage.getScene();
        if (scene == null) {
            scene = new Scene(page, 700, 450);
//            scene.getStylesheets().add(ClientTicTacToe.class.getResource("demo.css").toExternalForm());
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(page);
        }
        stage.sizeToScene();
        

        return page;
    }
        
        

}
