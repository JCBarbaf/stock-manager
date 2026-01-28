import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

public class TableCreation {

    private static Map<String, String> env;

    public static void main(String[] args) throws Exception{
        env = EnvLoader.loadEnv("./.env");

        String dbName = env.get("DB_NAME");
        String tableName = env.get("TABLE_NAME");

        try (Connection conn = DatabaseConnection.getConnection()) {

            System.out.println("Database connection successful!");

            String createTableSql = """
                CREATE TABLE IF NOT EXISTS `%s` (
                    `id` INT NOT NULL AUTO_INCREMENT,
                    `name` VARCHAR(255) DEFAULT NULL,
                    `quantity` INT DEFAULT 0,
                    `price` DECIMAL(8,2) DEFAULT 0.00,
                    PRIMARY KEY (`id`),
                    CONSTRAINT `chk_quantity` CHECK (`quantity` >= 0)
                );
            """.formatted(tableName);

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(createTableSql);
            }

            System.out.println("Table created successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
