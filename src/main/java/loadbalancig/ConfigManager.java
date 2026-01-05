/**
 * <h1>ConfigManager</h1>
 * A lightweight utility class for loading and reading configuration parameters
 * from a standard Java <code>.properties</code> file.
 * the file named config.properties, you can adjust you prefered intialized metrics
 * <p>
 * It provides convenient methods to access configuration values as
 * {@code String}, {@code int}, {@code double}, or {@code boolean}, 
 * along with a method to print or enumerate all loaded keys.
 * values such as metrics (response time,ressource utilization) and thresholds.
 * <p>
 * Typical usage:
 * <pre>{@code
 * ConfigManager config = new ConfigManager("config.properties");
 * double deadline = config.getDouble("deadline", 10);
 * boolean debugMode = config.getBoolean("debug");
 * }</pre>
 *
 * @author Ben Halima Anouar
 * @version 1.0
 * 
 */
package loadbalancig;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class ConfigManager {
    private Properties properties;

    // Constructor: loads the config file
    public ConfigManager(String filePath) throws IOException {
        properties = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        }
    }

    // Get property as String
    public String getString(String key) {
        return properties.getProperty(key);
    }
    
    public double getDouble(String key, int defaultValue) {
         try {
            return Double.parseDouble(properties.getProperty(key));
        } catch (Exception e) {
             System.err.println("loadbalancig.ConfigManager.getDouble()"+ e.getMessage());
            return defaultValue;
        }
    }

    // Get property as boolean (yes/no or true/false)
    public boolean getBoolean(String key) {
        String value = properties.getProperty(key);
        if (value == null) return false;
        value = value.trim().toLowerCase();
        return value.equals("yes") || value.equals("true") || value.equals("1");
    }

    // Get property as int (default if missing)
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Set<String> getAllKeys(){
        
        return properties.stringPropertyNames();
    }
    
    
    // Print all properties (for debugging)
    public void printAll() {
        properties.forEach((k, v) -> System.out.println(k + " = " + v));
    }
}
