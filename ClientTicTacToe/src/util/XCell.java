/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import clienttictactoe.ClientTicTacToe;
import clienttictactoe.Setting;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.color;
import static javafx.scene.paint.Color.color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import model.User;

/**
 *
 * @author kazafy
 */
public class XCell extends ListCell<User> {

    /*
    
    
  
//    ☐ rekt
//    ☐ not rekt
//    ☑ tyrannosaurus rekt 
ImagePattern imagePattern = new ImagePattern(image);
rekt.setFill(imagePattern);

ImageView imageView = new ImageView(image);
    */
    Image image = new Image(getClass().getResourceAsStream("MyImage.png"));
    //ImageView img=new ImageView(image);
    
    HBox hbox = new HBox();
    Label label = new Label("(empty)");
    //Pane pane = new Pane();
    Circle cstatus = new Circle();
    Circle cimage = new Circle();
   // Button button = new Button("(>)");
    String lastItem;
    Label label2 = new Label(".");
    
    public XCell() {
        super();
        String css = XCell.class.getResource("cell.css").toExternalForm();
        hbox.getStylesheets().add(css);
        hbox.setId("cellinfo");
        
        
       // c.applyCss();
        //System.out.print("styleeee"+hbox.getStylesheets());
        hbox.getChildren().addAll(cstatus, cimage,label);
        //HBox.set
        HBox.setHgrow(label, Priority.ALWAYS);

    }

    @Override
    protected void updateItem(User item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);  // No text in label of super class
        if (empty) {
            lastItem = null;
            setGraphic(null);
        } else {
           
            lastItem = item.toString();
           // label2.setPrefSize(20, 20);
           // c.fillProperty(color.white);
            cstatus.setRadius(5);
            cimage.setRadius(10);
            cimage.setFill(new ImagePattern(image));
           if(item.getStatus()==Setting.AVAILABLE){
               cstatus.setFill(Color.CHARTREUSE);
            // c.getStyle().add();
           //  c.setId("online");
          // c.setStyle("-fx-background-color: #BADA55;");
          // setStyle("-fx-background-color: #ccc;");
           //System.out.print("stylee"+c.getStyle());
           //  c.setStyle("-fx-border-radius: 10px; -fx-background: #BADA55;");
           }
           else if(item.getStatus()==Setting.BUSY){
               cstatus.setFill(Color.DARKORANGE);
               //c.setId("offline");
           }
           else if(item.getStatus()==Setting.OFFLINE)
           {
               cstatus.setFill(Color.DARKGREY);
           }
           //label.setTextFill(red);
           //label.setStyle("-fx-color: #BADA55;");
            label.setText(item.toString() != null ? item.toString() : "<null>");
            setGraphic(hbox);
        }
    }
}
