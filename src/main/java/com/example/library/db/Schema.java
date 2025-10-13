package com.example.library.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Schema {
    public static void init() throws SQLException {
        try (Connection c = Db.getDataSource().getConnection(); Statement st = c.createStatement()) {
//            st.executeUpdate("DROP TABLE IF EXISTS books");
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
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    full_name VARCHAR(200) NOT NULL,
                    email VARCHAR(200) NOT NULL UNIQUE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
            """);
            st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS loans (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    book_id BIGINT NOT NULL,
                    user_id BIGINT NOT NULL,
                    loan_date DATE NOT NULL,
                    due_date DATE NOT NULL,
                    return_date DATE,
                    
                    CONSTRAINT fk_loans_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT,
                    CONSTRAINT fk_loans_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
                    )
            """);
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_loans_user ON loans(user_id)");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_loans_book ON loans(book_id)");
            st.executeUpdate("CREATE INDEX IF NOT EXISTS idx_loans_open ON loans(user_id, book_id, return_date)");
        }
    }
}
