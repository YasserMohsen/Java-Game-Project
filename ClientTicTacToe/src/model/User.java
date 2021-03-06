/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author kazafy
 */
public class User implements Serializable{
    private static final long serialVersionUID = 2L;
    private int id ;
    private int status;
    String email;
    String name;
    String password;
    int score;
    long fbId;
    String img;
    
    MyImage serializedImg;
    String imgLink;
    /*
    *constructor for regestration;
    */

    public User(int id, int status, String name) {
        this.id = id;
        this.status = status;
        this.name = name;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
    
    
    /*
    *constructor for login;
    */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.name = "";
        this.score = 0;
    }
    
    /*
    *constructor for Facebook Login
    */
    public User(long fbId,String name, String email,String img) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.fbId = fbId;
        this.img = img;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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

    public long getFbId() {
        return fbId;
    }

    public String getImg() {
        return img;
    }

    public void setFbId(int fbId) {
        this.fbId = fbId;
    }

    public void setImg(String img) {
        this.img = img;
    }
    

    @Override
    public String toString() {
        return this.name ;
    }
    

    public void change(String img) {
        this.img = img;
    }
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
