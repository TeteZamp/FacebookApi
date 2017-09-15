
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

/**
 *
 * @author Stefania
 */
public class ConnectionJDBC {

    protected static Connection returnConn() throws ClassNotFoundException, InstantiationException, IllegalAccessException {

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

    protected static boolean createDB() {

        try {

            java.sql.Connection conn = ConnectionJDBC.returnConn();

            Statement statement = conn.createStatement();

            String sql = "SELECT count(*) FROM information_schema.TABLES WHERE (TABLE_SCHEMA = 'facebookapi') AND (TABLE_NAME = 'posts');";

            ResultSet result = statement.executeQuery(sql);
            result.first();
            System.out.println(result.getString(1));
            if (result.getString(1).equals("1")) {
                sql = "DROP TABLE `posts`;";

                statement.execute(sql);
            } else {
            }

            sql = "CREATE TABLE `posts` (\n"
                    + "  `post_id` varchar(35) DEFAULT NULL,\n"
                    + "  `message` mediumtext CHARACTER SET utf8mb4 NOT NULL,\n"
                    + "  `created_time` datetime DEFAULT NULL\n"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";

            statement.execute(sql);
            
            return true;

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(PostsConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
