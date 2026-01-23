import java.io.*;
import java.util.*;

public class EnvLoader {
    public static Map<String,String> loadEnv(String path) throws IOException {
        Map<String,String> env = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // skip comments and empty lines
            if (!line.startsWith("#") && line.contains("=")) {
                String[] parts = line.split("=", 2);
                env.put(parts[0], parts[1]);
            }
        }

        reader.close();
        return env;
    }
}

