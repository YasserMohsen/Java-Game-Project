/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;


import java.sql.* ;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sqlite.SQLiteConfig;

/**
 *
 * @author yasser
 */
public class DBConnection {

    
    public static final String DB_URL = "jdbc:sqlite:Tic.db";  
    public static final String DRIVER = "org.sqlite.JDBC"; 
      

    public static Connection openConnection(){

        
    Connection connection = null;
    try { 
    Class.forName(DRIVER);  
 
        SQLiteConfig config = new SQLiteConfig();  
        config.enforceForeignKeys(true);  
         connection = DriverManager.getConnection(DB_URL,config.toProperties());  
        } 
    catch (SQLException | ClassNotFoundException ex) {  
        Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
    }  
    return connection; 
}
}
