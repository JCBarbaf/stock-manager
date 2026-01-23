import java.sql.DriverManager;
import java.util.Map;
public class DatabaseConnection {
    public static void main(String[] args) throws Exception{

        Map<String,String> dotenv = EnvLoader.loadEnv("./.env");
        String host = dotenv.get("DB_HOST");
        String port = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");

        String url = "jdbc:mariadb://" + host + ":"+ port + "/" + dbName;
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        try {
            DriverManager.getConnection(url, user, password);
            System.out.println("Connected!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
