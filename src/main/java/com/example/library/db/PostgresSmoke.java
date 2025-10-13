package com.example.library.db;

import com.example.library.model.Book;
import com.example.library.repo.BookRepository;
import com.example.library.repo.JdbcBookRepository;

public class PostgresSmoke {
    public static void main(String[] args) {
        Migrations.migrate();

        BookRepository repo = new JdbcBookRepository();
        Book b = new Book("978-0-13-235088-4", "Clean Code", "Robert C. Martin", 2008, 3);
        repo.save(b);
        System.out.println("Inserted with id=" + b.getId());
        System.out.println("Total rows: " + repo.findAll().size());
    }
}
