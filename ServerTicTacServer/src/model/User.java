/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javafx.scene.image.Image;

/**
 *
 * @author kazafy
 */
public class User implements Serializable{
    private static final long serialVersionUID = 2L;
    private int id ;
    private int status;
    String name;
    String email;
    String password;
    long fbId;
    String img; 
    //Image img;
    MyImage serializedImg;
    String imgLink;
    private int score;

    public User(int id, int status, String name, String email, String password, int score) {
        this.id = id;
        this.status = status;
        this.name = name;
        this.email = email;
        this.password = password;
        this.score = score;
    }
    

    public User(String name, String email, String password, int score) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.score = score;
    }
    public User(){
        
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setFbId(int fbId) {
        this.fbId = fbId;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public long getFbId() {
        return fbId;
    }

    public String getImg() {
        return img;
    }

//    public Image getImg() {
//        return img;
//    }
//
//    public void setImg(Image img) {
//        this.img = img;
//    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }

    public MyImage getSerializedImg() {
        return serializedImg;
    }

    public void setSerializedImg(MyImage serializedImg) {
        this.serializedImg = serializedImg;
    }
    
    
    
    
}
