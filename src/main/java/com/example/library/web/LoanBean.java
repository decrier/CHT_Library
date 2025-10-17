package com.example.library.web;

import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.User;
import com.example.library.repo.*;
import com.example.library.service.LoanService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("loanBean")
@RequestScoped
public class LoanBean {
    private final BookRepository bookRepo = new JdbcBookRepository();
    private final UserRepository userRepo = new JdbcUserRepository();
    private final LoanRepository loanRepo = new JdbcLoanRepository();
    private final LoanService loanService = new LoanService(bookRepo, userRepo, loanRepo);

    public static class Row {
        public long id;
        public String userName;
        public String bookTitle;
        public LocalDate loanDate;
        public LocalDate dueDate;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public void setBookTitle(String bookTitle) {
            this.bookTitle = bookTitle;
        }

        public LocalDate getLoanDate() {
            return loanDate;
        }

        public void setLoanDate(LocalDate loanDate) {
            this.loanDate = loanDate;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }
    }

    public List<Row> getActiveLoans() {
        List<Loan> loans = loanRepo.findActive();
        Map<Long, User> users = userRepo.findAll().stream().collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, Book> books = bookRepo.findAll().stream().collect(Collectors.toMap(Book::getId, b -> b));

        List<Row> rows = new ArrayList<>();
        for (Loan l : loans) {
            Row r = new Row();
            r.setId(l.getId());
            r.setLoanDate(l.getLoanDate());
            r.setLoanDate(l.getDueDate());

            User u = users.get(l.getUserId());
            Book b = books.get(l.getBookId());
            r.setUserName(u != null ? u.getName() : ("#" + l.getUserId()));
            r.setBookTitle(b != null ? b.getTitle() : ("book#" + l.getBookId()));
            rows.add(r);
        }
        return rows;
    }

    public String returnLoan(long loanId) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            loanService.returnLoan(loanId, LocalDate.now());
            context.getExternalContext().getFlash().setKeepMessages(true);
            context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Returned id=" + loanId, null));
            return "/loans.xhtml?faces-redirect=true";
        } catch (IllegalArgumentException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        } catch (RuntimeException ex) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "DB error: " + ex.getMessage(), null));
            return null;
        }
    }
}
