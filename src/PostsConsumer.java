
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Page;
import com.restfb.types.Post;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Stefania
 */
public class PostsConsumer {
    
    private static FacePosts thepost;
    
    // 3 - Crie uma classe Java com dois métodos (getPosts e getVolume) que consuma os dados dessa tabela : 

    protected static void getPosts(String since, String until) {

        /* 3.1 - Dados brutos (getPosts): O usuário passará como parâmetro "since" (uma data) e o "until" (outra data).
 O método deverá retornar todos os posts, com todos os campos que são capturados, contemplados dentro desse período.
 
        O retorno do método deve ser um objeto do tipo JSON. 

Exemplo de chamada: getPosts("20170101","20171231");
Exemplo de retorno: [ {"id": "123123123123", "content": "abcd"}, {"id": "123123123124", "content": "bcad"} ]*/
        
        //DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        
        

               
        try {
            
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date sincedb = formatter.parse(since);
            Date untildb = formatter.parse(until);
            
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            
            since = formatter.format(sincedb);
            until = formatter.format(untildb);
            
            System.out.println("FORMATTED SINCE: " + since);
            System.out.println("FORMATTED UNTIL: " + until);
            
            
            java.sql.Connection conn = ConnectionJDBC.returnConn();
            
            String sql = "SELECT * FROM posts where created_time between '"+ since +"' and '" + until +" 23:59:00';";
            
            System.out.println("MY SQL: " + sql);

 
            Statement statement = conn.createStatement();
            ResultSet result;            
            
            result = statement.executeQuery(sql);
            
            //int count = 0;
            
            PostsConsumer.thepost = new FacePosts();

            while (result.next()){
                
                thepost.setPost_id(result.getString(1));
                thepost.setMessage(result.getString(2));
                thepost.setCreated_time(result.getDate(3));              
                
                System.out.println("FROM ID: " + thepost.getPost_id());
                System.out.println("-->" + thepost.getMessage());
                System.out.println("WHEN: " + thepost.getCreated_time());

            }          
            

        } catch (ParseException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(PostsConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
              
        
    }

    protected static void getVolume(String since, String until) {

        /* 3.2 - Dados consolidados (getVolume): O usuário passará como parâmetro "since" (uma data) e o "until" (outra data).
 O método deverá retornar, para cada dia nesse intervalo, a quantidade de posts que foi coletada.

Exemplo de chamada de 01 de janeiro de 2017 a 05 de janeiro de 2017: getVolume("20170101","20170105");
Exemplo de retorno: OLHAR EMAIL! */
        
        try {
            
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date sincedb = formatter.parse(since);
            Date untildb = formatter.parse(until);
            
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            
            since = formatter.format(sincedb);
            until = formatter.format(untildb);
            
                   
            
            java.sql.Connection conn = ConnectionJDBC.returnConn();
            
            String sql = "SELECT created_time, count(*) FROM posts where created_time \n" +
            "between '" + since + "' and '" + until + " 23:59:00' group by DAY(created_time), MONTH(created_time), YEAR(created_time)";
            
            System.out.println("MY SQL: " + sql);

 
            Statement statement = conn.createStatement();
            ResultSet result;            
            
            result = statement.executeQuery(sql);
            
            //int count = 0;
            
            PostsConsumer.thepost = new FacePosts();

            while (result.next()){
                
             /*   thepost.setPost_id(result.getString(1));
                thepost.setMessage(result.getString(2));
                thepost.setCreated_time(result.getDate(3));              
                
                System.out.println("FROM ID: " + thepost.getPost_id());
                System.out.println("-->" + thepost.getMessage());
                System.out.println("WHEN: " + thepost.getCreated_time());*/

            }          
            

        } catch (ParseException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(PostsConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    

    protected static void fetchFacePosts(int lastXdays, String facebookPg, String accessToken) throws ParseException {

        FacebookClient fbClient = new DefaultFacebookClient(accessToken, Version.LATEST);

        Page page = fbClient.fetchObject(facebookPg, Page.class);

        Calendar cal = Calendar.getInstance();
        cal.setTime(Calendar.getInstance().getTime());
        Date sincedt = cal.getTime();
        
        System.out.println("Current Date: " + cal.getTime());
        System.out.println("Unix Time Date: " + sincedt.getTime() / 1000);

        cal.add(Calendar.DATE, -lastXdays);
        sincedt = cal.getTime(); //has the old date
        
        
        System.out.println("Old Date: " + cal.getTime());
        System.out.println("Unix Time Old Date: " + sincedt.getTime() / 1000);
              
        Long sinceunixtime = sincedt.getTime() / 1000;

        Connection<Post> postFeed = fbClient.fetchConnection(page.getId()+ "/feed", Post.class, Parameter.with("since", sinceunixtime.toString()), Parameter.with("fields", "from,message,created_time"));

        PostsConsumer.thepost = new FacePosts();
        
        for (Post aPost : postFeed.getData()){
        
                thepost.setPost_id(aPost.getId());             
                thepost.setMessage(aPost.getMessage());
                thepost.setCreated_time(aPost.getCreatedTime());
                
                /*System.out.println("FROM: " + aPost.getFrom().getName());
                System.out.println("-->" + aPost.getMessage());
                System.out.println("ID: " + aPost.getId());
                System.out.println("WHEN: " + aPost.getCreatedTime());*/
                
                
                insertPost(thepost);            
            
        }   
        
    }
    
    
    protected static boolean insertPost(FacePosts thepost) throws ParseException{
        
        try {
            
            java.sql.Connection conn = ConnectionJDBC.returnConn();
            
            String sql = "insert into posts(post_id,message,created_time) values (?,?,?);";
       
            PreparedStatement statement = conn.prepareStatement(sql);
            
            statement.setString(1, thepost.getPost_id());
            statement.setString(2, thepost.getMessage());
            
            statement.setString(3, thepost.getCreated_time());

            int rowsInserted = statement.executeUpdate();
            
            if (rowsInserted > 0) {
                System.out.println("A new post was inserted successfully!");
                conn.close();
            }else{
                System.out.println("Post was not successfully inserted, please check!");
                conn.close();
            }
            return true;
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(PostsConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return false;
        
    }
        
       

}
