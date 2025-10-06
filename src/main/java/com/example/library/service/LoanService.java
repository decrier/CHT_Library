package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.User;
import com.example.library.repo.BookRepository;
import com.example.library.repo.LoanRepository;
import com.example.library.repo.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

public class LoanService {

    private final BookRepository bookRepo;
    private final UserRepository userRepo;
    private final LoanRepository loanRepo;

    public LoanService(BookRepository bookRepo, UserRepository userRepo, LoanRepository loanRepo) {
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
        this.loanRepo = loanRepo;
    }

    public Loan issueLoan(long userId, long bookId, LocalDate loanDate, LocalDate dueDate) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("book not found: " + bookId));

        if (book.getCopiesAvailable() <= 0) {
            throw new IllegalArgumentException("no copies available for book id=" + bookId);
        }
        if (loanRepo.findOpenByUserAndBook(user.getId(), book.getId()).isPresent()) {
            throw new IllegalArgumentException("user already has an aopen loan for this book");
        }

        Loan loan = new Loan(book.getId(), user.getId(), loanDate, dueDate);
        loanRepo.save(loan);

        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        bookRepo.save(book);

        return loan;
    }

    public boolean returnLoan(long loanId, LocalDate returnDate) {
        Optional<Loan> opt = loanRepo.findById(loanId);
        if (opt.isEmpty()) return false;

        Loan loan = opt.get();
        if (loan.getReturnDate() != null) {
            // уже возвращена - считаем idempotent-операцией; ничего не делаем, но возвращаем true
            return true;
        }

        loan.setReturnDate(returnDate);
        loanRepo.save(loan);

        Book book = bookRepo.findById(loan.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("book not found for loan id=" + loanId));
        if (book.getCopiesAvailable() >= book.getCopiesTotal()) {
            throw new IllegalArgumentException("copiesAvailable already at max for book id=" + book.getId());
        }
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
        bookRepo.save(book);
        return true;
    }
}
