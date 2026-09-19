package com.example.beevolut;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    private static final String IP = "192.168.1.158";
    private static final String PORT = "1433";
    private static final String DATABASE = "Beevolut";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "123";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + IP + ":" + PORT + "/" + DATABASE + ";user=" + USERNAME + ";password=" + PASSWORD + ";";
            conn = DriverManager.getConnection(connectionUrl);
        } catch (Exception e) {
            System.out.println("Erro Helper: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return conn;
    }
}