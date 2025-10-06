package com.example.library.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LoanTest {

    @Test
    void create_and_return_ok() {
        LocalDate loan = LocalDate.of(2025, 1, 10);
        LocalDate due = LocalDate.of(2025, 1, 20);

        Loan l = new Loan(1L, 2L, loan, due);
        assertNull(l.getId());
        assertEquals(1L, l.getBookId());
        assertEquals(2L, l.getUserId());
        assertFalse(l.isReturned());

        // возврат
        l.setReturnDate(LocalDate.of(2025, 1, 19));
        assertTrue(l.isReturned());
        assertEquals(LocalDate.of(2025, 1, 19), l.getReturnDate());
    }

    @Test
    void invalid_dates_throw() {
        LocalDate loan = LocalDate.of(2025, 1, 10);
        assertThrows(IllegalArgumentException.class, () -> new Loan(1L, 2L, loan,
                LocalDate.of(2025, 1, 9)));

        Loan l = new Loan(1L, 2L, loan, LocalDate.of(2025, 1, 20));
        assertThrows(IllegalArgumentException.class,
                () -> l.setReturnDate(LocalDate.of(2025, 1, 9)));
    }
}
