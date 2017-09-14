
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

/**
 *
 * @author Stefania
 */


public class ConnectionJDBC {


    protected static Connection returnConn() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        
        try {  

            Class.forName("com.mysql.jdbc.Driver").newInstance(); 
            
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/facebookapi?user=stefania&password=ste123&verifyServerCertificate=false&useSSL=false&requireSSL=false&useUnicode=true");
            return conn;  

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            
        }
        return null;
    }

    
}
