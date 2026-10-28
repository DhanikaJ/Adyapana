package Model;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

public class MySQL {
    private static Connection connection;

    public static void createConnection() throws Exception {
        if (connection == null) {
            Properties props = new Properties();
            props.load(new FileInputStream("src/model/config.properties.txt"));

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
        }
    }

    public static ResultSet executeSearch(String query) throws Exception {
        createConnection();
        return connection.createStatement().executeQuery(query);
    }

    public static Integer executeIUD(String query) throws Exception {
        createConnection();
        return connection.createStatement().executeUpdate(query);
    }

    public static ResultSet executeQuery(String query) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}