package com.example.library.db;


import org.flywaydb.core.Flyway;

public class Migrations {

    public static void migrate() {
        Flyway.configure()
                .dataSource(Db.getDataSource())
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }

}
