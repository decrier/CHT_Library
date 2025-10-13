package com.example.library.web;

import com.example.library.db.Migrations;
import com.example.library.model.Book;
import com.example.library.repo.BookRepository;
import com.example.library.repo.JdbcBookRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.List;

@Named
@RequestScoped
public class BookBean {

    private final BookRepository repo;

    public BookBean() {
        Migrations.migrate();
        this.repo = new JdbcBookRepository();
    }

    public List<Book> getBooks() {
        return repo.findAll();
    }
}
