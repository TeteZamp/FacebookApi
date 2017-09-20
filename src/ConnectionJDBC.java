
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefania
 */
public class ConnectionJDBC {

    protected static java.sql.Connection conn; //atributo conexao

    protected static Connection returnConn() throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        try {

            Class.forName("com.mysql.jdbc.Driver");//registra o driver no projeto

            conn = DriverManager.getConnection("jdbc:mysql://localhost/facebookapi?user=stefania&password=ste123&verifyServerCertificate=false&useSSL=false&requireSSL=false&useUnicode=true");
            //realiza a conexao com o banco desabilitando ssl e habilitando caracteres unicode
            return conn;

        } catch (SQLException ex) { //trata os erros de conexao
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());

        }
        return null;
    }

    protected static boolean createDB() {

        try {

            conn = ConnectionJDBC.returnConn();

            Statement statement = conn.createStatement();
            //select verifica se a tabela posts existe no schema facebookapi - 
            //retorna 0 se nao existir; 
            //retorna 1 se existir
            String sql = "SELECT count(*) FROM information_schema.TABLES WHERE (TABLE_SCHEMA = 'facebookapi') AND (TABLE_NAME = 'posts');";

            ResultSet result = statement.executeQuery(sql);
            result.first();

            if (result.getString(1).equals("1")) {
                sql = "DROP TABLE `posts`;"; //se a tabela existir apaga e recria 
                statement.execute(sql);
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

    protected static boolean insertPost(FacePosts thepost) throws ParseException {

        try {

            conn = ConnectionJDBC.returnConn();

            String sql = "insert into posts(post_id,message,created_time) values (?,?,?);";

            PreparedStatement statement = conn.prepareStatement(sql);
            //prepara a declaracao sql para receber os placeholders(?)

            statement.setString(1, thepost.getPost_id()); //insere no placeholder 1 o id
            statement.setString(2, thepost.getMessage());  //insere no placeholder 2 a mensagem
            statement.setString(3, thepost.getCreated_time()); //insere no placeholder 3 a hora da criacao

            statement.executeUpdate();

            //verifica se inseriu os posts na tabela do banco 
            /*if (rowsInserted > 0) {
                System.out.println("A new post was inserted successfully!");
                conn.close();
            } else {
                System.out.println("Post was not successfully inserted, please check!");
                conn.close();
            }*/
            return true;

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(PostsConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
