
import com.restfb.json.JsonArray;
import java.text.ParseException;
import java.util.Scanner;

/**
 *
 * @author Stefania
 */
public class Main {

    private static boolean isdbcreated;

    public static void main(String[] args) throws ParseException {

        isdbcreated = ConnectionJDBC.createDB(); //cria o banco

        if (isdbcreated == true) {

            Scanner reader = new Scanner(System.in);

            System.out.println("Enter an access Token: ");
            String accessToken = reader.next(); //le o token

            System.out.println("Enter the last X days to fetch posts: ");
            int lastXdays = reader.nextInt(); //le X dias a ser buscado

            System.out.println("Enter the Facebook page or ID to fetch the posts: ");
            String facebookPg = reader.next(); //le a pagina a ser buscada

            //chamada do método que busca os posts e insere no banco
            PostsConsumer.fetchFacePosts(lastXdays, facebookPg, accessToken);

            System.out.println("Enter the date interval to fetch the posts from the table (SINCE then UNTIL): ");

            System.out.println("TYPE SINCE: ");
            String since = reader.next();

            System.out.println("TYPE UNTIL: ");
            String until = reader.next();

            JsonArray myjsonarray = PostsConsumer.getPosts(since, until);
            // myjsonarray recebe o retorno do metodo getPosts

            System.out.println("Output 1 -> " + myjsonarray.toString());
            //imprime o conteudo do array

            myjsonarray = PostsConsumer.getVolume(since, until);
            //myjsonarray recebe o retorno do metodo getVolume

            System.out.println("Output 2 -> " + myjsonarray.toString());
            //imprime o conteudo do array

        } else {
            System.out.println("DB has not been created successfully!!");
        }
    }

}
