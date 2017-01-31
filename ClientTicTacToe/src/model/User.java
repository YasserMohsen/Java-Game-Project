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
    String name;
    String email;
    String password;
    String repassword;
    
    /*
    *constructor for regestration;
    */

    public User(String name, String email, String password, String repassword) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.repassword = repassword;
    }
    
    
    /*
    *constructor for login;
    */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRepassword() {
        return repassword;
    }

    public void setRepassword(String repassword) {
        this.repassword = repassword;
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

    @Override
    public String toString() {
        return this.name ;
    }
    
    
    
}