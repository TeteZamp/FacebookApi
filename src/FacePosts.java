
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Stefania
 */
public class FacePosts {

    private String post_id;
    private String message;
    private String created_time;

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_time() {
        return created_time;
    }

    //metodo modificado para transformar a data no formato do banco
    public void setCreated_time(Date created_time) throws ParseException {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String to_write_date = formatter.format(created_time);

        this.created_time = to_write_date;
    }

}
