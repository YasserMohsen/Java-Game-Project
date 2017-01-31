/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictac;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author kazafy
 */
public class TicTac extends Application {
    
    private boolean player1 = true ;
    private Button [][]labels = new Button[3][3];
    private int counter = 0;
    private boolean isFinish=false;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        GridPane gridPane = new GridPane();
        for (int i = 0; i < 9; i++) {
                labels[i/3][i%3] = new Button("-");
                gridPane.add(labels[i/3][i%3], i%3, i/3);
                labels[i/3][i%3].setOnAction((ActionEvent event) -> {
                    if(isFinish)
                        return;
                    if(((Button)event.getSource()).getText().equals("-")){
                        counter++;

                        if(player1){
                            ((Button)event.getSource()).setText("X");
                        }else{
                            ((Button)event.getSource()).setText("O");
                        }    
                        if (checkWins()){
                            isFinish=true;
                            System.out.println(" plyer number "+((player1)?" 1 ":" 2 ") +" win");
                        }                        
                        
                        player1 = !player1;                        
                    }                    
                });
            }
        
        
        BorderPane root =  new BorderPane();
        root.setCenter(gridPane);
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }
    private  boolean checkWins(){
        return (comb(labels[0][0],labels[0][1])&& comb(labels[0][0],labels[0][2])||
                comb(labels[1][0],labels[1][1])&& comb(labels[1][0],labels[1][2])||
                comb(labels[2][0],labels[2][1])&& comb(labels[2][0],labels[2][2])||
                comb(labels[0][0],labels[0][1])&& comb(labels[0][0],labels[0][2])||
                comb(labels[0][1],labels[1][1])&& comb(labels[1][0],labels[2][1])||
                comb(labels[0][2],labels[1][2])&& comb(labels[0][0],labels[2][2])||
                comb(labels[0][0],labels[1][1])&&comb(labels[0][0], labels[2][2])||
                comb(labels[0][2],labels[1][1])&&comb(labels[0][2], labels[2][0]));
    }
    private boolean comb(Button x ,Button y){
        
        return  x.getText().equals(y.getText())&& ! x.getText().equals("-");
    } 
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
