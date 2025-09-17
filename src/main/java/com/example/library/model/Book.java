package com.example.library.model;

public class Book {

    private final String isbn;
    private String title;
    private String author;
    private int year;
    private int copiesTotal;
    private int copiesAvailable;

    public Book(String isbn, String title, String author, int year, int copiesTotal, int copiesAvailable) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.copiesTotal = copiesTotal;
        this.copiesAvailable = copiesAvailable;
    }

    private static String req(String s, String field) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return s.trim();
    }
}
