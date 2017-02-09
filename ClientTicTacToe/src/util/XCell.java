/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import clienttictactoe.ClientTicTacToe;
import clienttictactoe.Setting;
import javafx.geometry.Pos;
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
import model.MyImage;
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
    //Image image = new Image(getClass().getResourceAsStream("MyImage.png"));
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
        hbox.setPrefHeight(30);
        hbox.getChildren().addAll(cstatus, cimage,label);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color:paleturquoise;-fx-background-radius:20;-fx-padding:3;");
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
            cstatus.setRadius(5);
            
            cimage.setRadius(15);
             MyImage i = item.getSerializedImg();
          //   Image ii = i.getImage();
          cimage.setFill(new ImagePattern(i.getImage()));
          //cimage.setFill(new ImagePattern(image));
          
           if(item.getStatus()==Setting.AVAILABLE){
               cstatus.setFill(Color.CHARTREUSE);
           }
           else if(item.getStatus()==Setting.BUSY){
               cstatus.setFill(Color.DARKORANGE);
               //c.setId("offline");
           }
           else if(item.getStatus()==Setting.OFFLINE)
           {
               cstatus.setFill(Color.DARKGREY);
           }

            label.setText(item.toString() != null ? item.toString() : "<null>");
            label.setStyle("-fx-font-weight: bold;");
            setGraphic(hbox);
        }
    }
}
