package com.example.library.db;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Db {

    private static final HikariDataSource DS;

    static {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:postgresql://localhost:5432/library");
        cfg.setUsername("postgres");
        cfg.setPassword("postgres");
    }
}
