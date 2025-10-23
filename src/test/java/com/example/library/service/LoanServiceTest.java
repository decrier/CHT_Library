package com.example.library.service;

import com.example.library.db.Db;
import com.example.library.db.Migrations;
import com.example.library.exception.NoCopiesAvailableException;
import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.User;
import com.example.library.repo.BookRepository;
import com.example.library.repo.JdbcBookRepository;
import com.example.library.repo.JdbcLoanRepository;
import com.example.library.repo.JdbcUserRepository;
import com.example.library.repo.LoanRepository;
import com.example.library.repo.UserRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class LoanServiceTest {

//    private final BookRepository bookRepo = new JdbcBookRepository();
//    private final UserRepository userRepo = new JdbcUserRepository();
//    private final LoanRepository loanRepo = new JdbcLoanRepository();
//    private final LoanService loanService = new LoanService(bookRepo, userRepo, loanRepo);
//
//    @BeforeEach
//    void setup() throws Exception {
//        // Применяем миграции (если уже применены — будет "up to date")
//        Migrations.migrate();
//        // Чистим таблицы, чтобы тесты были независимыми
//        truncateAll();
//    }
//
//    @Test
//    @DisplayName("1) Успешная выдача: copiesAvailable уменьшается, появляется активная выдача")
//    void issue_success() {
//        long userId = createUser("Alice", "alice@example.com");
//        long bookId = createBook("978-0-13-235088-4", "Clean Code", "Robert C. Martin", 2008, 2);
//
//        loanService.issueLoan(userId, bookId, today(), today().plusDays(14));
//
//        // Проверяем книгу
//        Book b = bookRepo.findById(bookId).orElseThrow();
//        assertEquals(1, b.getCopiesAvailable(), "После выдачи доступных экземпляров должно стать 1");
//
//        // Проверяем наличие одной активной выдачи
//        long active = loanRepo.findAll().stream().filter(l -> l.getReturnDate() == null).count();
//        assertEquals(1, active, "Должна быть одна активная выдача");
//    }
//
//    @Test
//    @DisplayName("2) Нельзя выдать ту же книгу тому же пользователю второй раз, пока первая не возвращена")
//    void issue_same_book_twice_forbidden() {
//        long userId = createUser("Bob", "bob@example.com");
//        long bookId = createBook("978-1-491-94700-2", "Effective Java", "Joshua Bloch", 2018, 1);
//
//        // Первая выдача — ок
//        loanService.issueLoan(userId, bookId, today(), today().plusDays(7));
//
//        // Повторная — ожидаем IllegalArgumentException (или твое бизнес-исключение)
//        assertThrows(NoCopiesAvailableException.class, () ->
//                        loanService.issueLoan(userId, bookId, today(), today().plusDays(7)),
//                "Повторная активная выдача той же книги тому же пользователю должна быть запрещена");
//
//        // copiesAvailable не уменьшился второй раз
//        Book b = bookRepo.findById(bookId).orElseThrow();
//        assertEquals(0, b.getCopiesAvailable(), "Должно остаться 0 — в минус уходить нельзя");
//
//        // Активная выдача по-прежнему одна
//        long active = loanRepo.findAll().stream().filter(l -> l.getReturnDate() == null).count();
//        assertEquals(1, active, "Должна быть только одна активная выдача");
//    }
//
//    @Test
//    @DisplayName("3) Возврат: returnLoan проставляет returnDate и увеличивает copiesAvailable")
//    void return_success() {
//        long userId = createUser("Carol", "carol@example.com");
//        long bookId = createBook("978-0-201-61622-4", "Design Patterns", "GoF", 1994, 1);
//
//        loanService.issueLoan(userId, bookId, today(), today().plusDays(10));
//
//        // Находим активную выдачу (самую свежую)
//        Loan active = findLatestActive().orElseThrow();
//
//        // До возврата доступных 0
//        assertEquals(0, bookRepo.findById(bookId).orElseThrow().getCopiesAvailable());
//
//        loanService.returnLoan(active.getId(), today());
//
//        // После возврата доступных 1
//        assertEquals(1, bookRepo.findById(bookId).orElseThrow().getCopiesAvailable());
//
//        // У этой выдачи теперь returnDate != null
//        Loan after = (Loan) loanRepo.findById(active.getId()).orElseThrow();
//        assertNotNull(after.getReturnDate(), "returnDate должен быть проставлен после возврата");
//    }
//
//    // -------------------- helpers --------------------
//
//    private static LocalDate today() {
//        return LocalDate.now();
//    }
//
//    private long createUser(String name, String email) {
//        User u = new User(name, email);
//        userRepo.save(u);
//        return u.getId();
//    }
//
//    private long createBook(String isbn, String title, String author, int year, int total) {
//        Book b = new Book(isbn, title, author, year, total);
//        // Если в конструкторе не ставится доступность — зададим явно
//        if (b.getCopiesAvailable() == 0) {
//            b.setCopiesAvailable(total);
//        }
//        bookRepo.save(b);
//        return b.getId();
//    }
//
//    private Optional<Loan> findLatestActive() {
//        List<Loan> all = loanRepo.findAll();
//        return all.stream()
//                .filter(l -> l.getReturnDate() == null)
//                .max(Comparator.comparing(Loan::getLoanDate).thenComparing(Loan::getId));
//    }
//
//    private void truncateAll() throws SQLException {
//        try (Connection c = Db.getDataSource().getConnection()) {
//            // порядок важен из-за внешних ключей
//            c.createStatement().executeUpdate("DELETE FROM loans");
//            c.createStatement().executeUpdate("DELETE FROM users");
//            c.createStatement().executeUpdate("DELETE FROM books");
//        }
//    }
}
