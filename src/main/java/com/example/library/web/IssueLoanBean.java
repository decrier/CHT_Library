package com.example.library.web;

import com.example.library.model.Book;
import com.example.library.model.User;
import com.example.library.repo.*;
import com.example.library.service.LoanService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class IssueLoanBean {
    private BookRepository bookRepo = new JdbcBookRepository();
    private UserRepository userRepo = new JdbcUserRepository();
    private LoanRepository loanRepo = new JdbcLoanRepository();
    private LoanService loanService = new LoanService(bookRepo, userRepo, loanRepo);

    private Long userId;
    private Long bookId;
    private LocalDate dueDate = LocalDate.now().plusDays(14);

    // списки для выпадающих меню
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    public List<Book> getAvailableBooks() {
        // простая фильтрация на стороне приложения
        return bookRepo.findAll().stream()
                .filter(b -> b.getCopiesAvailable() > 0)
                .collect(Collectors.toList());
    }

    public String issue() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (userId == null || bookId == null || dueDate == null) {
                context.addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Select user, book and due date", null));
                return null;
            }
            loanService.issueLoan(userId, bookId, LocalDate.now(), dueDate);
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Loan issued", null));
            return "/loans.xhtml?faces-redirect=true";
        } catch (IllegalArgumentException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        } catch (RuntimeException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "DB error: " + ex.getMessage(), null));
            return null;
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
