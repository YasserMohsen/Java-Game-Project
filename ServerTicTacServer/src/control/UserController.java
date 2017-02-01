/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

/**
 *
 * @author kazafy
 */
public class UserController {
    
    public static int register( User user){
        int id = 0;
        try {
            Connection con = DBConnection.openConnection();
            PreparedStatement stmt = con.prepareStatement("INSERT INTO user (name,email,password,score) VALUES(?,?,?,0);", Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next())
            {
                id =rs.getInt(1);
//              user.setId(rs.getInt(1));
//              user.setScore(rs.getInt(5));
            }
            con.close();
        } catch (SQLException ex) {
            //ex.printStackTrace();
            int error = ex.getErrorCode();
            
        }
        return id;
        
        
    }
    public static boolean login(User user){
        try {
            Connection con = DBConnection.openConnection();
            System.out.println("Connected for login");
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM user WHERE email=? AND password=?;");
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();
            //con.close();
            if (rs.next()){
                return true;
            }
            else{
                return false;
            }
        } catch (SQLException ex) {
            int error = ex.getErrorCode();
            System.out.println("error in login code number :" + error);
            return false;
        }
        
    }
    public static boolean logout(User user){
        return true;
    }
}
