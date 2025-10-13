package com.example.library.db;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class Db {

    private static final HikariDataSource DS;

    static {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl("jdbc:postgresql://localhost:5432/library");
        cfg.setUsername("postgres");
        cfg.setPassword("postgres");
        cfg.setDriverClassName("org.postgresql.Driver");
        cfg.setMaximumPoolSize(5);
        cfg.setMinimumIdle(1);
        DS = new HikariDataSource(cfg);
    }

    public static DataSource getDataSource() {
        return DS;
    }
}
