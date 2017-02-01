/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttictactoe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author kazafy
 */
public class ClientTicTacToe extends Application {

    private static Stage stage;
    public static final String LOGIN_XML="login.fxml";
    public static final String MAIN_XML="main.fxml";
    
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("register.fxml"));
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
    
    public static Parent replaceSceneContent(String fxml , String windowTitle) throws Exception {
        Parent page = FXMLLoader.load(ClientTicTacToe.class.getResource(fxml));
        Scene scene = stage.getScene();
        stage.setTitle(windowTitle);
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
