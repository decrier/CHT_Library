package com.example.library.repo;

import com.example.library.model.Loan;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryLoanRepository implements LoanRepository{

    private final Map<Long, Loan> byId = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public synchronized Loan save(Loan loan) {
        if (loan == null) throw new IllegalArgumentException("loan is null");
        if (loan.getId() == null) {
            long id = seq.getAndIncrement();
            loan.setId(id);
            byId.put(id, loan);
            return loan;
        } else {
            byId.put(loan.getId(), loan);
            return loan;
        }
    }

    @Override
    public synchronized Optional findById(long id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public synchronized List<Loan> findAll() {
        return new ArrayList<>(byId.values());
    }

    @Override
    public synchronized List<Loan> findByUserId(Long userId) {
        List<Loan> found = new ArrayList<>();
        for (Loan l : byId.values()) {
            if (Objects.equals(l.getUserId(), userId)) {
                found.add(l);
            }
        }
        return found;
    }

    @Override
    public List<Loan> findByBookId(Long bookId) {
        List<Loan> found = new ArrayList<>();
        for (Loan l : byId.values()) {
            if (Objects.equals(l.getBookId(), bookId)) {
                found.add(l);
            }
        }
        return found;
    }

    @Override
    public Optional<Loan> findOpenByUserAndBook(long userId, long bookId) {
        for (Loan l : byId.values()) {
            if (Objects.equals(l.getBookId(), bookId)
                    && Objects.equals(l.getUserId(),userId)
                    && !l.isReturned()) {
                return Optional.of(l);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(long id) {
        return byId.remove(id) != null;
    }
}
