/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilServer;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import model.MyImage;
import model.User;
import servertictacserver.Setting;

/**
 *
 * @author yasser
 */
public class YCell extends ListCell<User>{
    /*
    
    
  
//    ☐ rekt
//    ☐ not rekt
//    ☑ tyrannosaurus rekt 
ImagePattern imagePattern = new ImagePattern(image);
rekt.setFill(imagePattern);

ImageView imageView = new ImageView(image);
    */
    Image image = new Image(getClass().getResourceAsStream("male.jpg"));
    //ImageView img=new ImageView(image);
    GridPane grid = new GridPane();
    //HBox hbox = new HBox();
    Label name = new Label("(empty)");
    Label email = new Label("(empty)");
    
    //Pane pane = new Pane();
    Circle cstatus = new Circle();
    Circle cimage = new Circle();
   // Button button = new Button("(>)");
    String lastItem;
    Label score = new Label("(empty)");

    public YCell() {
        super();
        String css = YCell.class.getResource("cell.css").toExternalForm();
        grid.getStylesheets().add(css);
        grid.setId("cellinfo");
        grid.setPrefHeight(30);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(5);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(10);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(50);
        ColumnConstraints col5 = new ColumnConstraints();
        col5.setPercentWidth(10);
        grid.getColumnConstraints().addAll(col1,col2,col3,col4,col5);
        grid.add(cstatus, 0, 0);
        grid.add(cimage,1,0);
        grid.add(name,2,0);
        grid.add(email,3,0);
        grid.add(score,4,0);
        //grid.getChildren().addAll(cstatus, cimage,name,email);
        //grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(20);
        //hbox.setStyle("-fx-background-color:paleturquoise;-fx-background-radius:20;-fx-padding:3;");
        //HBox.setHgrow(label, Priority.ALWAYS);


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
            cimage.setRadius(15);
//            MyImage i = item.getSerializedImg();
//             Image ii = i.getImage();
            System.out.println(item.getImgLink());
          cimage.setFill(new ImagePattern(image));
           if(item.getStatus()==Setting.AVAILABLE){
               cstatus.setVisible(true);
               cstatus.setFill(Color.CHARTREUSE);
            // c.getStyle().add();
           //  c.setId("online");
          // c.setStyle("-fx-background-color: #BADA55;");
          // setStyle("-fx-background-color: #ccc;");
           //System.out.print("stylee"+c.getStyle());
           //  c.setStyle("-fx-border-radius: 10px; -fx-background: #BADA55;");
           }
           else if(item.getStatus()==Setting.BUSY){
               cstatus.setVisible(true);
               cstatus.setFill(Color.DARKORANGE);
               //c.setId("offline");
           }
           else if(item.getStatus()==Setting.OFFLINE)
           {
               cstatus.setVisible(true);
               cstatus.setFill(Color.DARKGREY);
           }
           else if(item.getStatus()==Setting.OUT){
               cstatus.setVisible(false);
           }
           //label.setTextFill(red);
           //label.setStyle("-fx-color: #BADA55;");

            name.setText(item.getName() != null ? item.getName() : "<null>");
            email.setText(item.getEmail());
            score.setText(item.getScore()+"");
            setGraphic(grid);
        }
    }
}
