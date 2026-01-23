
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class StockManager {

    private static Map<String, String> env;

    public static void main(String[] args) throws Exception {
        env = EnvLoader.loadEnv("./.env");
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Database connection successful!");
            showProducts(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showProducts(Connection conn) throws SQLException {
        String tableName = env.get("TABLE_NAME");
        String query = String.format("SELECT id, name, quantity, price FROM %s", tableName);
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {

            System.out.println("Stock List:");
            System.out.println("-----------------------------------------------");
            System.out.printf("%-5s | %-20s | %-8s | %-10s%n", "ID", "Name", "Quantity", "Price");
            System.out.println("-----------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                BigDecimal price = rs.getBigDecimal("price");

                System.out.printf("%-5d | %-20s | %-8d | %-10.2f%n", id, name, quantity, price);
            }
        }
    }
}
