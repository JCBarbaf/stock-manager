
import java.io.Console;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StockManager {
    private static Map<String, String> env;
    private static Scanner scanner;
    private static String dbName;

    public static void main(String[] args) throws Exception {
        env = EnvLoader.loadEnv("./.env");
        dbName = env.get("DB_NAME").replace("_", " ");
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Database connection successful!");
            System.out.printf("\n ------ Wellcome to %s ------ \n", dbName);
            scanner = new Scanner(System.in);
            MainMenu(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void MainMenu(Connection conn) throws Exception{
        boolean repeat = true;
        while (repeat) {
            System.out.printf("""

--- %s ---
1- See all products
2- Search a product
3- Add new product
4- Change a product
5- Delete a product
6- EXIT
        """, dbName.substring(0, 1).toUpperCase() + dbName.substring(1));
        System.out.print("What do you want to do? ");
        String userResponse = scanner.nextLine();
        System.out.println("\n");
        boolean exit;
        String id;
        boolean validID;
        switch (userResponse) {
            case "1":
                showProducts(conn, null, null);
                break;
            case "2":
                System.out.print("What do you want to search? ");
                String filter = scanner.nextLine();
                showProducts(conn, filter, null);
                break;
            case "3":
                System.out.print("New product name: ");
                String name = scanner.nextLine();
                String quantity;
                do {
                    System.out.print("New product quantity: ");
                    quantity = scanner.nextLine();
                    exit = quantity.matches("\\d+");
                    if(!exit) {
                        System.out.println("!!! Invalid quantity");
                    }
                } while (!exit);
                String price;
                do {
                    System.out.print("New product price: ");
                    price = scanner.nextLine();
                    exit = price.matches("^\\d+[.,]\\d{2}$");
                    if(!exit) {
                        System.out.println("!!! Invalid price");
                    }
                } while (!exit);
                createProduct(conn, name, quantity, price);
                break;
            case "4":
                do {
                    System.out.print("Product ID: ");
                    id = scanner.nextLine();
                    exit = id.matches("\\d+");
                    validID = checkproductID(conn, id);
                    if(!exit) {
                        System.out.println("!!! ID must be a number");
                    }
                    if(!validID) {
                        System.out.println("!!! Invalid ID");
                    }
                } while (!exit || !validID);
                showProducts(conn, null, id);
                updateMenu(conn, id);
                break;
            case "5":
                do {
                    System.out.print("Product ID: ");
                    id = scanner.nextLine();
                    exit = id.matches("\\d+");
                    validID = checkproductID(conn, id);
                    if(!exit) {
                        System.out.println("!!! ID must be a number");
                    }
                    if(!validID) {
                        System.out.println("!!! Invalid ID");
                    }
                } while (!exit || !validID);
                showProducts(conn, null, id);
                deleteProduct(conn, id);
                break;
            case "6":
                System.out.println("Goodbye! ^_^");
                repeat = false;
                break;
            default:
                System.out.println("!!! Please select a valid option");
                break;
            }
        }
        scanner.close();
    }

    public static void showProducts(Connection conn, String filter, String ID) throws SQLException {
        String tableName = env.get("TABLE_NAME");
        String query = String.format("SELECT id, name, quantity, price FROM %s", tableName);
        if (filter != null) {
            query += " WHERE name LIKE '%" + filter + "%'";
        }
        if (ID != null) {
            query += String.format(" WHERE id = %s", ID);
        }
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            System.out.println("\nProducts List:");
            System.out.println("-------------------------------------------------------------------");
            System.out.printf("%-5s | %-35s | %-8s | %-20s%n", "ID", "Name", "Quantity", "Price");
            System.out.println("-------------------------------------------------------------------");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                BigDecimal price = rs.getBigDecimal("price");

                System.out.printf("%-5d | %-35s | %-8d | %-20.2f%n", id, name, quantity, price);
            }
        }
        if (ID == null) {
            Console console = System.console();
            if (console != null) {
                console.readPassword("\nPress Enter to go back...");
            }
        }

    }

    public static void createProduct(Connection conn, String name, String quantity, String price) throws Exception{
        price = price.replace(",", ".");
        String tableName = env.get("TABLE_NAME");
        String query = String.format("INSERT INTO %s (name, quantity, price) VALUES ('%s',%s,%s)", tableName, name, quantity, price);
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            System.out.println("Product added correctly");
        }
    }

    public static void updateMenu(Connection conn, String id) throws Exception {
        HashMap<String, String> productData = getProductData(conn, id);
        boolean repeat = true;
        while (repeat) {
            System.out.println("""

--- Update Product ---
1- Change name
2- Change quantity
3- Change price
4- GO BACK
            """);
            System.out.print("What do you want to do? ");
            String userResponse = scanner.nextLine();
            System.out.println("\n");
            boolean exit;
            switch (userResponse) {
                case "1":
                    System.out.print("New name: ");
                    String newName = scanner.nextLine();
                    updateProduct(conn, id, newName, productData.get("quantity"), productData.get("price"));
                    productData = getProductData(conn, id);
                    break;
                case "2":
                    String newQuantity;
                    do {
                        System.out.print("New product quantity: ");
                        newQuantity = scanner.nextLine();
                        exit = newQuantity.matches("\\d+");
                        if(!exit) {
                            System.out.println("!!! Invalid quantity");
                        }
                    } while (!exit);
                    updateProduct(conn, id, productData.get("name"), newQuantity, productData.get("price"));
                    productData = getProductData(conn, id);
                    break;
                case "3":
                    String newPrice;
                    do {
                        System.out.print("New product price: ");
                        newPrice = scanner.nextLine();
                        exit = newPrice.matches("^\\d+[.,]\\d{2}$");
                        if(!exit) {
                            System.out.println("!!! Invalid price");
                        }
                    } while (!exit);
                    updateProduct(conn, id, productData.get("name"), productData.get("quantity"), newPrice);
                    productData = getProductData(conn, id);
                    break;
                case "4":
                    repeat = false;
                    break;
                default:
                    System.out.println("!!! Please select a valid option");
                    break;
            }
        }
    }

    public static void updateProduct(Connection conn, String id, String name, String quantity, String price) throws Exception {
        price = price.replace(",", ".");
        String tableName = env.get("TABLE_NAME");
        String query = String.format("UPDATE %s SET name = '%s', quantity = %s, price = %s WHERE id=%s;", tableName, name, quantity, price, id);
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            System.out.println("Product updated successfully");
        }
    }

    public static void deleteProduct(Connection conn, String id) throws Exception {
        System.out.print("\nAre you sure you want to delete this product? (y/N): ");
        String response = scanner.nextLine();
        if (response.equalsIgnoreCase("y") || response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("yep")) {
            String tableName = env.get("TABLE_NAME");
            String query = String.format("DELETE FROM %s WHERE id=%s;", tableName, id);
            try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
                System.out.println("Product deleted successfully");
            }
        }
    }

    public static HashMap<String,String> getProductData(Connection conn, String id) throws Exception {
        String tableName = env.get("TABLE_NAME");
        String query = String.format("SELECT name, quantity, price FROM %s WHERE id=%s", tableName, id);
        HashMap<String, String> productData = new HashMap<>();
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                productData.put("name", rs.getString("name"));
                productData.put("quantity", Integer.toString(rs.getInt("quantity")));
                productData.put("price", rs.getBigDecimal("price").toString());
            } else {
                throw new SQLException("No data found");
            }
        }
        return productData;
    }

    public static boolean checkproductID(Connection conn, String id) throws Exception {
        String tableName = env.get("TABLE_NAME");
        String query = String.format("SELECT name, quantity, price FROM %s WHERE id=%s", tableName, id);
        try (PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        }
    }
}
