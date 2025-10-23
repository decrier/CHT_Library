package com.example.library.service;

import com.example.library.exception.ActiveLoanExistsException;
import com.example.library.exception.NoCopiesAvailableException;
import com.example.library.model.Book;
import com.example.library.model.Loan;
import com.example.library.model.User;
import com.example.library.repo.BookRepository;
import com.example.library.repo.LoanRepository;
import com.example.library.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceUnitTest {

//    @Mock
//    BookRepository bookRepo;
//
//    @Mock
//    UserRepository userRepo;
//
//    @Mock
//    LoanRepository loanRepo;
//
//    LoanService service;
//
//    @BeforeEach
//    void setUp() {
//        service = new LoanService(bookRepo, userRepo, loanRepo);
//    }
//
//    @Test
//    @DisplayName("Успешная выдача: уменьшаем copiesAvailable и сохраняем Loan")
//    void issue_success() {
//        long userId= 10L;
//        long bookId = 20L;
//        User user = new User("Alice", "alice@example.com");
//        user.setId(userId);
//        Book book = new Book("978-0-13-235088-4", "Clean Code", "Uncle Bob", 2008, 2);
//        book.setId(bookId);
//        book.setCopiesAvailable(book.getCopiesTotal());
//
//        // stub: что вернут репозитории
//        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
//        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
//        when(loanRepo.findOpenByUserAndBook(userId, bookId)).thenReturn(Optional.empty());
//
//        // act
//        var loanDate = LocalDate.now();
//        var dueDate  = loanDate.plusDays(14);
//        service.issueLoan(userId, bookId, loanDate, dueDate);
//
//        // assert: доступность уменьшилась и была сохранена
//        assertEquals(1, book.getCopiesAvailable());
//        verify(bookRepo).save(book);
//
//        // assert: сохранён Loan с правильными полями
//        ArgumentCaptor<Loan> loanCap = ArgumentCaptor.forClass(Loan.class);
//        verify(loanRepo).save(loanCap.capture());
//        Loan saved = loanCap.getValue();
//        assertEquals(userId, saved.getUserId());
//        assertEquals(bookId, saved.getBookId());
//        assertEquals(loanDate, saved.getLoanDate());
//        assertEquals(dueDate, saved.getDueDate());
//        assertNull(saved.getReturnDate());
//    }
//
//    @Test
//    @DisplayName("Нельзя выдать, если нет доступных экземпляров")
//    void issue_forbidden_when_no_copies() {
//        long userId = 1L, bookId = 2L;
//        var user = new User("Bob", "bob@example.com"); user.setId(userId);
//        var book = new Book("978-1-491-94700-2", "Effective Java", "Bloch", 2018, 1);
//        book.setId(bookId); book.setCopiesAvailable(0);
//
//        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
//        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
//
//        assertThrows(NoCopiesAvailableException.class, () ->
//                service.issueLoan(userId, bookId, LocalDate.now(), LocalDate.now().plusDays(7)));
//
//        // убеждаемся, что ничего не сохранили
//        verify(loanRepo, never()).save(any());
//        verify(bookRepo, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Нельзя выдать ту же книгу тому же пользователю, если активный Loan уже есть")
//    void issue_same_book_twice_forbidden() {
//        long userId = 3L, bookId = 4L;
//        var user = new User("Carol", "carol@example.com"); user.setId(userId);
//        var book = new Book("978-0-201-61622-4", "Design Patterns", "GoF", 1994, 1);
//        book.setId(bookId); book.setCopiesAvailable(1);
//
//        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
//        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
//        when(loanRepo.findOpenByUserAndBook(userId, bookId))
//                .thenReturn(Optional.of(new Loan(bookId, userId, LocalDate.now(), LocalDate.now().plusDays(14)))); // уже есть активный
//
//        assertThrows(ActiveLoanExistsException.class, () ->
//                service.issueLoan(userId, bookId, LocalDate.now(), LocalDate.now().plusDays(10)));
//
//        verify(loanRepo, never()).save(any());
//        verify(bookRepo, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("Возврат: проставляем returnDate и увеличиваем copiesAvailable")
//    void return_success() {
//        long loanId = 100L, userId = 5L, bookId = 6L;
//        Loan loan = new Loan(bookId, userId, LocalDate.now(), LocalDate.now().plusDays(14));
//        loan.setId(loanId);
//        var book = new Book("978-x", "Any", "Auth", 2000, 1);
//        book.setId(bookId); book.setCopiesAvailable(0);
//
//        when(loanRepo.findById(loanId)).thenReturn(Optional.of(loan));
//        when(bookRepo.findById(bookId)).thenReturn(Optional.of(book));
//
//        LocalDate retDate = LocalDate.now();
//        service.returnLoan(loanId, retDate);
//
//        assertEquals(1, book.getCopiesAvailable());
//        verify(bookRepo).save(book);
//
//        // verify обновлённый Loan с returnDate
//        assertEquals(retDate, loan.getReturnDate());
//        verify(loanRepo).save(loan);
//    }
}
