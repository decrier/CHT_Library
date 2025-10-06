package com.example.library.repo;

import com.example.library.model.Loan;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryLoanRepositoryTest {

    @Test
    void save_and_find_by_user_and_book() {
        var repo = new InMemoryLoanRepository();

        var l1 = new Loan(1L, 10L, LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 20));
        var l2 = new Loan(2L, 10L, LocalDate.of(2025, 1, 11), LocalDate.of(2025, 1, 21));
        var l3 = new Loan(1L, 20L, LocalDate.of(2025, 1, 12), LocalDate.of(2025, 1, 22));

        repo.save(l1);
        repo.save(l2);
        repo.save(l3);

        assertNotNull(l1.getId());
        assertEquals(3, repo.findAll().size());

        var u10 = repo.findByUserId(10L);
        assertEquals(2, u10.size());

        var b1 = repo.findByBookId(1L);
        assertEquals(2, b1.size());
    }

    @Test
    void find_open_by_user_and_book() {
        var repo = new InMemoryLoanRepository();

        var l = new Loan(1L, 10L, LocalDate.of(2025, 1, 10), LocalDate.of(2025,1,20));
        repo.save(l);

        assertTrue(repo.findOpenByUserAndBook(10L, 1L).isPresent());

        l.setReturnDate(LocalDate.of(2025, 1, 15));
        repo.save(l);

        assertTrue(repo.findOpenByUserAndBook(10L, 1L).isEmpty());
    }
}
