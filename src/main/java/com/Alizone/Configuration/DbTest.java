package com.Alizone.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbTest {
    public static void main(String[] args) {
    	String url = "jdbc:postgresql://maglev.proxy.rlwy.net:47779/railway";
    	String user = "postgres";
    	String password = "XqjJLRQQrcVYAMLxpKkCDrQOtyzpaywf";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ DB bağlantısı başarılı!");
        } catch (Exception e) {
            System.err.println("❌ DB bağlantısı FAILED:");
            e.printStackTrace();
        }
    }
}