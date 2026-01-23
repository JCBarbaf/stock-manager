import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
public class DatabaseConnection {
    public static Connection getConnection() throws Exception{

        Map<String,String> env = EnvLoader.loadEnv("./.env");
        String host = env.get("DB_HOST");
        String port = env.get("DB_PORT");
        String dbName = env.get("DB_NAME");
        String user = env.get("DB_USER");
        String password = env.get("DB_PASSWORD");

        String url = String.format("jdbc:mariadb://%s:%s/%s", host, port, dbName);

        return DriverManager.getConnection(url, user, password);
    }
}
