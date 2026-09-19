package com.example.beevolut;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {

    private static final String IP = "sql9001.site4now.net";
    private static final String PORT = "1433";
    private static final String DATABASE = "db_ace68a_beevolut";
    private static final String USERNAME = "db_ace68a_beevolut_admin";
    private static final String PASSWORD = "EE@2025$ajj";

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