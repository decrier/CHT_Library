package com.example.library.model;

import java.util.Objects;

public class Book {

    private Long id;
    private final String isbn;
    private String title;
    private String author;
    private int year;
    private int copiesTotal;
    private int copiesAvailable;

    public Book(String isbn, String title, String author, int year, int copiesTotal) {
        this.isbn = req(isbn, "isbn");
        this.title = req(title, "title");
        this.author = req(author, "author");
        setYear(year);
        setCopiesTotal(copiesTotal);
        this.copiesAvailable = copiesTotal;
    }

    private static String req(String s, String field) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
        return s.trim();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = req(title, "title");
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = req(author, "author");
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        int max = java.time.Year.now().getValue() + 1;
        if (year < 1450 || year > max) throw new IllegalArgumentException("year out of range");
    }

    public int getCopiesTotal() {
        return copiesTotal;
    }

    public void setCopiesTotal(int copiesTotal) {
        if (copiesTotal < 0) throw new IllegalArgumentException("copiesTotal must be >= 0");
        if (this.copiesAvailable > copiesTotal) throw new IllegalArgumentException("copiesAvailable cannot exceed copiesTotal");
        this.copiesTotal = copiesTotal;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        if (copiesAvailable < 0 || copiesAvailable > copiesTotal) {
            throw new IllegalArgumentException("copiesAvailable must be in [0, copiesTotal]");
        }
        this.copiesAvailable = copiesAvailable;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (! (obj instanceof Book b)) return false;
        return isbn.equalsIgnoreCase(b.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn.toLowerCase());
    }
}
