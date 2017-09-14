
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Stefania
 */
public class FacePosts {
    /*1 - Faça um projeto em Java que faça conexão à Graph API (API do Facebook) e recupere os posts dos últimos X dias de uma página Y.
 X e Y são parâmetros que serão informados pelos desenvolvedores de teste na hora da utilização.*/
    
    
    private String post_id;
    private String message;
    private String created_time;       
    

    /**
     * @return the post_id
     */
    public String getPost_id() {
        return post_id;
    }

    /**
     * @param post_id the post_id to set
     */
    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the created_time
     */
    public String getCreated_time() {
        return created_time;
    }

    /**
     * @param created_time the created_time to set
     */
    public void setCreated_time(Date created_time) throws ParseException {
               
        //System.out.println("Unformatted Date: " + created_time);
             
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        String to_write_date = formatter.format(created_time);
        //System.out.println("Formatted Date: " + to_write_date);

        this.created_time = to_write_date;
    }
    
}
