/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import com.mysql.jdbc.Driver;
import java.sql.* ;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yasser
 */
public class DBConnection {
    
    public static Connection openConnection(){
        Connection con = null;
        try {
            DriverManager.registerDriver(new Driver());
            con = DriverManager.getConnection("jdbc:mysql://160.153.129.233:3306/TicTacTeo","javatictac", "javatictac");
            
        } catch (SQLException ex) {
            System.out.println("Go to hell!");
        }
        return con;
    }
}
