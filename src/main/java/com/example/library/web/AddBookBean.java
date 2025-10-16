package com.example.library.web;

import com.example.library.model.Book;
import com.example.library.repo.JdbcBookRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class AddBookBean {
    private String isbn;
    private String title;
    private String author;
    private Integer pubYear;
    private Integer copiesTotal;

    private final JdbcBookRepository repo = new JdbcBookRepository();

    public String addBook() {
        try {
            Book b = new Book(isbn, title, author, pubYear, copiesTotal);
            repo.save(b);
            // PRG: редирект на список после успешной отправки формы
            return "books?faces-redirect=true";
        } catch (IllegalArgumentException e) {
            // например, дубликат ISBN, валидность полей и т.п.
            FacesContext.getCurrentInstance().addMessage(
                    null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            return null; // остаться на форме
        } catch (RuntimeException e) {
            FacesContext.getCurrentInstance().addMessage(
                    null, new FacesMessage(
                            FacesMessage.SEVERITY_ERROR, "DB error: " + e.getMessage(), null)
            );
        } return null;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPubYear() {
        return pubYear;
    }

    public void setPubYear(Integer pubYear) {
        this.pubYear = pubYear;
    }

    public Integer getCopiesTotal() {
        return copiesTotal;
    }

    public void setCopiesTotal(Integer copiesTotal) {
        this.copiesTotal = copiesTotal;
    }
}
