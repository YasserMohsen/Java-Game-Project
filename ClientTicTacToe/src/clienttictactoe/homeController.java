/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clienttictactoe;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label ; 
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import model.FacebookApi;
import model.User;
/**
 *
 * @author salma
 */
public class homeController implements Initializable{
    
    @FXML
    BorderPane border;
    
    @FXML
    Pane salmaborder;
    
    @FXML
    Pane kazafyborder;
    
    @FXML
    Pane j;
    
    @FXML
    Button off;
    
    @FXML
    Button log;
    
    @FXML
    Button reg;
    
    @FXML
    Button fbLog;

    User user;
    
    public homeController(){
        try {
            
           // ClientTicTacToe.replaceSceneContent("homepage.fxml", "homepage");
//                salma.getTabs().add(new Tab("login"));
            
            
        } catch (Exception ex) {
            Logger.getLogger(homeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       // hover();
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
//    public void hover(){
//       // reg.onMouseEnteredProperty().addListener(listener);
//    reg.onMouseEnteredProperty().addListener(l->{
//        reg.getStyleClass().clear();
//        reg.getStyleClass().add("selected");
//    });
//    
//    reg.onMouseExitedProperty().addListener(l->{
//        reg.getStyleClass().clear();
//        reg.getStyleClass().add("unselected");
//    });
//    
//    }
    
   
   public void signupBtn(){
            
            reg.getStyleClass().removeAll("unselected","selected");
            reg.getStyleClass().add("selected");
            
            log.getStyleClass().removeAll("unselected","selected");;
            log.getStyleClass().add("unselected");
            
            off.getStyleClass().removeAll("unselected","selected");;
            off.getStyleClass().add("unselected");
            
            try {
            
            FXMLLoader loader = new FXMLLoader(homeController.class.getResource("register.fxml"));
            Parent page = loader.load();
            
            border.setCenter(null);
            border.setCenter(page);
            //ClientTicTacToe.replaceSceneContent("login.fxml", "login");
        } catch (IOException ex) {
            Logger.getLogger(homeController.class.getName()).log(Level.SEVERE, null, ex);
        }
}
   public void loginBtn(){
        try {
            log.getStyleClass().removeAll("unselected","selected");
            log.getStyleClass().add("selected");
            
            reg.getStyleClass().removeAll("unselected","selected");
            reg.getStyleClass().add("unselected");
            
            off.getStyleClass().removeAll("unselected","selected");
            off.getStyleClass().add("unselected");
            
            FXMLLoader loader = new FXMLLoader(homeController.class.getResource("login.fxml"));
            Parent page = loader.load();
            
            border.setCenter(null);
            border.setCenter(page);
            //ClientTicTacToe.replaceSceneContent("login.fxml", "login");
        } catch (IOException ex) {
            Logger.getLogger(homeController.class.getName()).log(Level.SEVERE, null, ex);
        }
   
   }
   public void offlineBtn(){
        try {
            
            off.getStyleClass().removeAll("unselected","selected");
            off.getStyleClass().add("selected");
            log.getStyleClass().removeAll("unselected","selected");
            log.getStyleClass().add("unselected");
            reg.getStyleClass().removeAll("unselected","selected");
            reg.getStyleClass().add("unselected");
            FXMLLoader loader = new FXMLLoader(homeController.class.getResource("login.fxml"));
            Parent page = loader.load();
            border.setCenter(null);
            border.setCenter(page);
        } catch (IOException ex) {
            Logger.getLogger(homeController.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   
   
   /*
   *facebook Login
   */
   public void fbLogin(){
        FacebookApi facebookApi = new FacebookApi();        
   }
    
}


