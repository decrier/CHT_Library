package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.model.User;
import com.example.library.repo.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LoanServiceTest {

    private static class Fixture {
        final InMemoryBookRepository books = new InMemoryBookRepository();
        final InMemoryUserRepository users = new InMemoryUserRepository();
        final InMemoryLoanRepository loans = new InMemoryLoanRepository();
        final LoanService svc = new LoanService(books, users, loans);

        long addUser(String name, String email) {
            var u = users.save(new User(name, email));
            return u.getId();
        }
        long addBook(String isbn, String title, String author, int year, int copies) {
            var b = books.save(new Book(isbn, title, author, year, copies));
            return b.getId();
        }
    }

    @Test
    void issue_and_return_happyPath() {
        var fx = new Fixture();
        long uid = fx.addUser("Ivan", "ivan@example.com");
        long bid = fx.addBook("111", "Clean Code", "RCM", 2008, 2);

        var loan = fx.svc.issueLoan(uid, bid, LocalDate.of(2025,1,10), LocalDate.of(2025,1,20));
        assertNotNull(loan.getId());
        // копий стало меньше
        assertEquals(1, fx.books.findById(bid).get().getCopiesAvailable());

        // возврат
        assertTrue(fx.svc.returnLoan(loan.getId(), LocalDate.of(2025,1,15)));
        assertEquals(2, fx.books.findById(bid).get().getCopiesAvailable());
    }

    @Test
    void cannot_issue_when_no_available_or_duplicate_open_loan() {
        var fx = new Fixture();
        long uid = fx.addUser("Ivan", "ivan@example.com");
        long bid = fx.addBook("222", "Effective Java", "Bloch", 2018, 1);

        // первая выдача — ок
        fx.svc.issueLoan(uid, bid, LocalDate.of(2025,2,1), LocalDate.of(2025,2,15));
        // повторная такая же (открытая уже есть) — запрещено
        assertThrows(IllegalArgumentException.class,
                () -> fx.svc.issueLoan(uid, bid, LocalDate.of(2025,2,2), LocalDate.of(2025,2,16)));

        // для другого пользователя — тоже нельзя, т.к. копий нет
        long uid2 = fx.addUser("Petr", "petr@example.com");
        assertThrows(IllegalArgumentException.class,
                () -> fx.svc.issueLoan(uid2, bid, LocalDate.of(2025,2,3), LocalDate.of(2025,2,17)));
    }
}

