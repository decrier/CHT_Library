package com.example.library.web;

import com.example.library.model.Book;
import com.example.library.repo.BookRepository;
import com.example.library.repo.JdbcBookRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.util.List;

@Named("bookBean")
@RequestScoped
public class BookBean {

    private final BookRepository repo = new JdbcBookRepository();

    public List<Book> getBooks() {
        return repo.findAll();
    }

    public String delete(long id) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            boolean removed = repo.deleteById(id);
            // показываем сообщение после redirect
            ctx.getExternalContext().getFlash().setKeepMessages(true);
            if (removed) {
                ctx.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_INFO, "Book deleted: id=" + id, null));
            } else {
                ctx.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_WARN, "Book not found: id=" + id, null));
            }
            // PRG
            return "books?faces-redirect=true";
        } catch (RuntimeException e) {
            ctx.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "DB error on delete: " + e.getMessage(), null));
            return null; // остаёмся на странице, сообщение уже показано
        }
    }
}
