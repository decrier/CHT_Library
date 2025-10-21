package com.example.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Loan {

    private Long id;
    private Long bookId;
    private Long userId;
    private LocalDate loanDate; // дата выдачи
    private LocalDate dueDate; // срок возврата
    private LocalDate returnDate; // дата фактического возврата

    public Loan(){}

    public Loan(Long bookId, Long userId, LocalDate loanDate, LocalDate dueDate) {
        if (bookId == null) throw new IllegalArgumentException("bookId must not be null");
        if (userId == null) throw new IllegalArgumentException("userId must not be null");
        if (loanDate == null) throw new IllegalArgumentException("loanDate must not be null");
        if (dueDate == null) throw new IllegalArgumentException("dueDate must not be null");
        if (dueDate.isBefore(loanDate)) throw new IllegalArgumentException("dueDate cannot be before loanDate");
        this.bookId = bookId;
        this.userId = userId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        if (dueDate == null) throw new IllegalArgumentException("dueDate must not be null");
        if (dueDate.isBefore(loanDate)) throw new IllegalArgumentException("dueDate cannot be before loanDate");
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        if (returnDate != null && returnDate.isBefore(loanDate)) {
            throw new IllegalArgumentException("returnDate cannot be before loanDate");
        }
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public boolean isOverdue(LocalDate today) {
        LocalDate t = Objects.requireNonNull(today);
        return isReturned() && dueDate.isBefore(today);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", userId=" + userId +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                '}';
    }
}
