package com.example.library.db;

import java.sql.*;

public class JdbcDemo {
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String pass = "";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement st = conn.createStatement()) {

            // создаем таблицу
            st.executeUpdate("CREATE TABLE books (id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "isbn VARCHAR(50) UNIQUE, title VARCHAR(200), author VARCHAR(200))");

            // вставляем запись
            st.executeUpdate("INSERT INTO books (isbn, title, author) VALUES " +
                    "('111', 'Clean Code', 'Robert Martin')");

            // читаем и выводим
            try (ResultSet rs = st.executeQuery("SELECT id, isbn, title, author FROM books")){
                while (rs.next()) {
                    System.out.printf("Book #%d: %s - %s (%s)%n",
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("isbn"));
                }
            }
        }
    }
}
