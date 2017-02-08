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
import javafx.stage.Stage;

/**
 *
 * @author kazafy
 */
public class ClientTicTacToe extends Application {

    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }
    public static final String LOGIN_XML="login.fxml";
    public static final String MAIN_XML="main.fxml";
    public static final String main_XML="FXML.fxml";
    
    
    //public static RegisterController registerController;
    public static MainController mainController;
   //public static LoginController loginController;
     public static HomeController home;
    public static OfflineController offlineController;
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClientTicTacToe.class.getResource("homepage.fxml"));      
        Parent root = loader.load();
        home = (HomeController)loader.getController();
        
        //registerController.email.setText("lol");

        this.stage = stage;
        Scene scene = new Scene(root);
        String css = ClientTicTacToe.class.getResource("clientcss.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);
        
        this.stage.setScene(scene);
        this.stage.hide();
        this.stage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public static Parent replaceSceneContent(String fxml , String windowTitle) throws Exception {
        FXMLLoader loader = new FXMLLoader(ClientTicTacToe.class.getResource(fxml));      
        Parent page = loader.load();

        Scene scene = stage.getScene();
        String css = ClientTicTacToe.class.getResource("clientcss.css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);
//        if(fxml=="login.fxml")
//        {
//            loginController = (LoginController)loader.getController();
//            //stage.setTitle(loginController.user.getName());
//        }

        if(fxml=="homepage.fxml")
        {
            home = (HomeController)loader.getController();
        }
//        else if(fxml=="register.fxml"){
//            registerController = (RegisterController)loader.getController();
//            
//        }
        
        else if(fxml=="offline.fxml"){
          
        }

        else if(fxml==main_XML){
            mainController = (MainController)loader.getController();
//            System.out.print("homeeeeeee"+registerController.email.getText());
        }
            //stage.setTitle(registerController.user.getName());
        

        
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
