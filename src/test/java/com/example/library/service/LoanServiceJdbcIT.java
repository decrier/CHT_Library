package com.example.library.service;

import com.example.library.db.Schema;
import com.example.library.model.Book;
import com.example.library.model.User;
import com.example.library.repo.JdbcBookRepository;
import com.example.library.repo.JdbcLoanRepository;
import com.example.library.repo.JdbcUserRepository;
import com.example.library.repo.BookRepository;
import com.example.library.repo.UserRepository;
import com.example.library.repo.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceJdbcIT {

    BookRepository books;
    UserRepository users;
    LoanRepository loans;
    LoanService service;

    @BeforeEach
    void setUp() throws Exception {
        // чистая схема на каждый тест
        Schema.init();
        books = new JdbcBookRepository();
        users = new JdbcUserRepository();
        loans = new JdbcLoanRepository();
        service = new LoanService(books, users, loans);
    }

    @Test
    void issue_then_return_works_with_jdbc() {
        // arrange: создаём книгу и пользователя в БД
        Book b = books.save(new Book("111", "Clean Code", "RCM", 2008, 2));
        User u = users.save(new User("Ivan", "ivan@example.com"));

        // act: выдаём
        var loan = service.issueLoan(u.getId(), b.getId(),
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2025, 1, 20));

        // assert: у книги стало меньше avail
        var afterIssue = books.findById(b.getId()).orElseThrow();
        assertEquals(1, afterIssue.getCopiesAvailable());
        assertNotNull(loan.getId());

        // act: возвращаем
        boolean ok = service.returnLoan(loan.getId(), LocalDate.of(2025, 1, 15));
        assertTrue(ok);

        // assert: у книги восстановился avail
        var afterReturn = books.findById(b.getId()).orElseThrow();
        assertEquals(2, afterReturn.getCopiesAvailable());
    }

    @Test
    void cannot_issue_duplicate_open_loan() throws SQLException {
        Schema.init();
        books = new JdbcBookRepository();
        users = new JdbcUserRepository();
        loans = new JdbcLoanRepository();
        service = new LoanService(books, users, loans);

        Book b = books.save(new Book("222", "Effective Java", "Bloch", 2018, 1));
        User u = users.save(new User("Ivan", "ivan@example.com"));

        // первая выдача — ок
        service.issueLoan(u.getId(), b.getId(), LocalDate.now(), LocalDate.now().plusDays(14));

        // повторная для того же (user, book) пока не вернули — должна упасть
        assertThrows(IllegalArgumentException.class,
                () -> service.issueLoan(u.getId(), b.getId(), LocalDate.now(), LocalDate.now().plusDays(14)));
    }

}
