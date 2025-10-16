package com.example.library.web;

import com.example.library.model.Book;
import com.example.library.repo.BookRepository;
import com.example.library.repo.JdbcBookRepository;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Optional;

@Named
@ViewScoped
public class EditBookBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;           // из viewParam
    private Book book;         // редактируемая книга
    private boolean notFound;  // флаг "не найдено"

    private final BookRepository repo = new JdbcBookRepository();

    // Вызывается из <f:event preRenderView>
    public void init() {
        if (book != null || id == null) return; // уже загружено или id не задан
        Optional<Book> found = repo.findById(id);
        if (found.isPresent()) {
            // создаём копию, чтобы бин был независим от кеша/списка
            Book b = found.get();
            book = new Book(b.getIsbn(), b.getTitle(), b.getAuthor(), b.getPubYear(), b.getCopiesTotal());
            book.setId(b.getId());
            book.setCopiesAvailable(b.getCopiesAvailable());
        } else {
            notFound = true;
        }
    }

    public String save() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            if (book == null || book.getId() == null) {
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nothing to save", null));
                return null;
            }
            repo.save(book); // id != null => update
            // Сообщение переживёт redirect
            ctx.getExternalContext().getFlash().setKeepMessages(true);
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Book updated: id=" + book.getId(), null));
            return "books?faces-redirect=true";
        } catch (IllegalArgumentException ex) {
            // например, дубликат ISBN
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        } catch (RuntimeException ex) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "DB error: " + ex.getMessage(), null));
            return null;
        }
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public boolean isNotFound() { return notFound; }
    public void setNotFound(boolean notFound) { this.notFound = notFound; }
}

