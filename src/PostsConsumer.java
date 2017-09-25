
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Page;
import com.restfb.types.Post;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stefania
 */
public class PostsConsumer {

    private static JsonArray myjsonarray;//define um array do tipo Json
    private static JsonObject myjsonobj; //define obj do tipo Json

    private static FacePosts thepost; //objeto do tipo post com 3 atributos(post_id,message,created_time)

    //metodo que busca os posts com base no numero de dias desejados de uma determinada pagina
    //e insere na tabela do banco
    protected static void fetchFacePosts(int lastXdays, String facebookPg, String accessToken) throws ParseException {

        FacebookClient fbClient = new DefaultFacebookClient(accessToken, Version.LATEST);
        //instancia um obj do tipo facebook client, verifica o token e define a versao da api

        Page page = fbClient.fetchObject(facebookPg, Page.class);//instancia um obj do tipo facebook Page que 
        //recebe uma pagina do facebook via metodo fetchObjetc da api

        Calendar cal = Calendar.getInstance(); //instancia um obj do tipo calendario
        cal.setTime(Calendar.getInstance().getTime());//configura a data de hoje no objeto
        Date sincedt;//instancia um obj do tipo date

        //System.out.println("Current Date: " + cal.getTime());
        //System.out.println("Unix Time Date: " + sincedt.getTime() / 1000);
        cal.add(Calendar.DATE, -lastXdays);//modifica o obj calendario subtraindo X dias
        sincedt = cal.getTime(); //atribui a data subtraida ao obj do tipo date

        //System.out.println("Old Date: " + cal.getTime());
        //System.out.println("Unix Time Old Date: " + sincedt.getTime() / 1000);
        Long sinceunixtime = sincedt.getTime() / 1000;//getTime do obj sincedt retorna a data em UnixTime em milisegundos.
        //a divisao por mil transforma a data em UnixTime em segundos que e o padrao aceito pela api do facebook

        Connection<Post> postFeed = fbClient.fetchConnection(page.getId() + "/feed", Post.class, Parameter.with("since", sinceunixtime.toString()), Parameter.with("fields", "from,message,created_time"));
        //instancia uma lista de connections do facebook do tipo Post que receberá os posts retornados pelo metodo
        //fetchconnection da api

        thepost = new FacePosts();

        //transforma em obj post para ter acesso aos atributos de um post.
        for (Post aPost : postFeed.getData()) {
            //populando o objeto thepost e inserindo no banco para cada objeto na lista de connections postFeed

            thepost.setPost_id(aPost.getId());
            thepost.setMessage(aPost.getMessage());
            thepost.setCreated_time(aPost.getCreatedTime());

            ConnectionJDBC.insertPost(thepost);//insere na tabela do banco
        }
    }

    protected static JsonArray getPosts(String since, String until) {

        myjsonarray = new JsonArray();//instancia um novo obj myjsonarray

        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");//cria uma forma como yyyyMMDD
            Date sincedb = formatter.parse(since);//converte since e until para o tipo Date conforme a forma acima
            Date untildb = formatter.parse(until);

            formatter = new SimpleDateFormat("yyyy-MM-dd");//modifica a forma para o novo formato aceito no banco

            since = formatter.format(sincedb);//atribui a data formatada pela nova forma às strings since e until
            until = formatter.format(untildb);

            //System.out.println("FORMATTED SINCE: " + since);
            //System.out.println("FORMATTED UNTIL: " + until);
            java.sql.Connection conn = ConnectionJDBC.returnConn();

            String sql = "SELECT * FROM posts where created_time between '" + since + "' and '" + until + " 23:59:00';";
            //seleciona todos os posts dentro do periodo de datas selecionado

            //System.out.println("MY SQL: " + sql);
            Statement statement = conn.createStatement();
            ResultSet result;

            result = statement.executeQuery(sql);

            thepost = new FacePosts();

            while (result.next()) {

                thepost.setPost_id(result.getString(1));
                thepost.setMessage(result.getString(2));

                myjsonobj = new JsonObject();
                //instancia um novo obj json
                myjsonobj.put("id", thepost.getPost_id().substring(thepost.getPost_id().indexOf('_') + 1, thepost.getPost_id().length()));
                //quebra o id apartir do _ para pegar somente o id do post e preenche o json 
                myjsonobj.put("content", thepost.getMessage());
                //preenche o Json com a mensagem
                myjsonarray.put(myjsonobj);
                //joga cada Json criado para o array
            }

        } catch (ParseException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(PostsConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return myjsonarray;//retorna o array completo

    }

    protected static JsonArray getVolume(String since, String until) {

        myjsonarray = new JsonArray(); //instancia um novo obj myjsonarray

        try {

            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");//cria uma forma como yyyyMMDD
            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");//cria uma forma como yyyyMMDD

            Date sincedb = formatter1.parse(since);//converte since e until para o tipo Date conforme a forma acima
            Date untildb = formatter1.parse(until);

            since = formatter2.format(sincedb);//atribuo a data formatada pela nova forma às strings since e until
            until = formatter2.format(untildb);

            java.sql.Connection conn = ConnectionJDBC.returnConn();

            String sql = "SELECT created_time, count(*) FROM posts where created_time \n"
                    + "between '" + since + "' and '" + until + " 23:59:00' group by DAY(created_time), MONTH(created_time), YEAR(created_time)";
            //agrupa por dia a quantidade de posts selecionados no intervalo de datas

            System.out.println("MY SQL: " + sql);
            Statement statement = conn.createStatement();
            ResultSet result;

            result = statement.executeQuery(sql);

            long numDays = TimeUnit.MILLISECONDS.toDays(untildb.getTime() - sincedb.getTime());

            Calendar cal = Calendar.getInstance(); //instancia um obj do tipo calendario
            cal.setTime(sincedb);
            Date dt;
            String newdt1,newdt2;


            System.out.println(numDays);
            result.first();

            for (int i = 0; i <= numDays; i++) {

                System.out.println(result.getDate(1));
                cal.add(Calendar.DATE, +1);
                dt = cal.getTime();

                newdt1 = formatter1.format(dt); //yyyyMMdd
                newdt2 = formatter2.format(dt);  //yyyy-MM-dd

                if (!result.getDate(1).toString().equals(newdt2)) {
                    myjsonobj = new JsonObject();
                    //instancia um novo obj json
                    myjsonobj.put("date", newdt1);
                    //preenche o json com a data formatada novamente
                    
                   // String quotes = "0";

                    myjsonobj.put("sum_posts",  "0");
                    //preenche o json com o count dos posts por dia
                    myjsonarray.put(myjsonobj);
                    //joga cada Json criado para o array
                } else {
                    myjsonobj = new JsonObject();
                    //instancia um novo obj json
                    myjsonobj.put("date", formatter1.format(result.getDate(1)));
                    //preenche o json com a data formatada novamente
                    myjsonobj.put("sum_posts", result.getString(2));
                    //preenche o json com o count dos posts por dia
                    myjsonarray.put(myjsonobj);
                    result.next();

                    //joga cada Json criado para o array
                }

            }

        } catch (ParseException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(PostsConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return myjsonarray;//retorna o array completo
    }
}
