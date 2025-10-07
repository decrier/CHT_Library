package com.example.library.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Schema {
    public static void init() throws SQLException {
        try (Connection c = Db.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("DROP TABLE IF EXISTS books");
            // простая схема для Book (минимально нужная для наших методов)
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS books (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    isbn VARCHAR(50) NOT NULL UNIQUE,
                    title VARCHAR(200) NOT NULL,
                    author VARCHAR(200) NOT NULL,
                    pub_year INT NOT NULL,
                    copies_total INT NOT NULL,
                    copies_avail INT NOT NULL
                )
            """);
        }
    }
}
