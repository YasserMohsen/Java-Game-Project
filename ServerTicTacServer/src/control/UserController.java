/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import model.MyImage;
import model.User;
import servertictacserver.Setting;

/**
 *
 * @author kazafy
 */
public class UserController {
    
    public static User register( User user){
        int id = 0;
        FileInputStream fis = null;
        try {
            
            Connection con = DBConnection.openConnection();
            con.setAutoCommit(false);
            PreparedStatement stmt = con.prepareStatement("INSERT INTO user (name,email,password,score,image) VALUES(?,?,?,0,?);", Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, Setting.DEFAULT_IMAGE);
            stmt.executeUpdate();
            con.commit();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next())
            {
                id =rs.getInt(1);
                user.setId(id);

                
                user.setImgLink(Setting.DEFAULT_IMAGE);
                
                MyImage s = new MyImage();
                Image i = new Image(UserController.class.getResourceAsStream(Setting.DEFAULT_IMAGE));
                s.setImage(i);
                user.setSerializedImg(s);
                
                
            }
            con.close();
        }catch (SQLException ex) {
            ex.printStackTrace();
            int error = ex.getErrorCode();
            user.setId(id);
            System.out.println("my id: " + id);
        } 
        return user;
        
        
    }
    public static User login(User user){
        int id = 0;
        String name = "";
        String imgLink = Setting.DEFAULT_IMAGE;
        try {
            Connection con = DBConnection.openConnection();
            System.out.println("Connected for login");
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM user WHERE email=? AND password=?;");
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();
            //con.close();
            if (rs.next()){
                id =rs.getInt(1);
                user.setId(id);
                
                name = rs.getString(2);
                user.setName(name);
                
                user.setScore(rs.getInt(5));
                if (rs.getString(6) != null){
                    imgLink = rs.getString(6);
                }
                user.setImgLink(imgLink);
                
                MyImage s = new MyImage();
                Image i = new Image(UserController.class.getResourceAsStream(Setting.DEFAULT_IMAGE));
                s.setImage(i);
                user.setSerializedImg(s);
                
            }
            con.close();
        } catch (SQLException ex) {
            int error = ex.getErrorCode();
            System.out.println("error in login code number :" + error);
            
        }
        return user;
        
    }
    
    /*
    *facebook login
    */
    public static User fbLogin(User user){
        int id = 0;
        String name = "";
        try {
            Connection con = DBConnection.openConnection();
            System.out.println("Connected for login");
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM user WHERE fb_id=?;");
            stmt.setLong(1, user.getFbId());
            ResultSet rs = stmt.executeQuery();
            //con.close();
            if (rs.next()){
                id =rs.getInt(1);
                user.setId(id);
                name = rs.getString(2);
                user.setName(name);
            }
            else{
                System.out.println("i need to insert " );
                stmt = con.prepareStatement("INSERT INTO user (name,email,password,score,fb_id,img) VALUES(?,?,?,0,?,?);", Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setLong(3, user.getFbId());
                stmt.setLong(4, user.getFbId());
                stmt.setString(5, user.getImg());
                stmt.executeUpdate();
                rs = stmt.getGeneratedKeys();
                if(rs.next())
                {
                    id =rs.getInt(1);
                    user.setId(id);
                }
                
            }
            con.close();
        } catch (SQLException ex) {
            ex.getSQLState();
            int error = ex.getErrorCode();
            System.out.println("error in login code number :" +  ex.getSQLState()+ex.getMessage());
            
        }
        return user;
        
    }
    public static boolean logout(User user){
        return true;
    }
    public static void saveScore(User user){
        try {
            Connection con = DBConnection.openConnection();
            PreparedStatement stmt = con.prepareStatement("UPDATE user SET score=? WHERE id=?;");
            stmt.setInt(1, user.getScore());
            stmt.setInt(2, user.getId());
            stmt.executeUpdate();
            
            
        } catch (SQLException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
    public static void loadUsers(ObservableList<User> ol){
        try {
            ol.clear();
            //User u = new User();
            Connection con = DBConnection.openConnection();
            System.out.println("Connected for loading users in server");
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM user;");
            ResultSet rs = stmt.executeQuery();
            //con.close();
            while (rs.next()){
                User u = new User();
                u.setName(rs.getString(2));
                u.setId(rs.getInt(1));
                u.setEmail(rs.getString(3));
                u.setScore(rs.getInt(5));
                u.setImgLink(rs.getString(6));
                u.setStatus(Setting.OUT);
                ol.add(u);
                
            }
            con.close();
        } catch (SQLException ex) {
            int error = ex.getErrorCode();
            System.out.println("error in login code number :" + error);
            
        }
    }
}
