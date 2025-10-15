package com.example.library.web;

import com.example.library.db.Migrations;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class AppInit implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Migrations.migrate();
    }
}
