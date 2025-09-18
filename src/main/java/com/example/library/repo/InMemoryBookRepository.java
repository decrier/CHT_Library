package com.example.library.repo;

import com.example.library.model.Book;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryBookRepository implements BookRepository{

    private final Map<Long, Book> byId = new LinkedHashMap<>();
    private final Map<String, Long> idByIsbn = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public synchronized Book save(Book book) {
        if (book == null) throw new IllegalArgumentException("book is null");
        String key  = book.getIsbn().toLowerCase();

        if (book.getId() == null) {
            // create
            if (idByIsbn.containsKey(key)) {
                throw new IllegalArgumentException("ISBN already exists: " + book.getIsbn());
            }
            long id = seq.getAndIncrement();
            book.setId(id);
            byId.put(id, book);
            idByIsbn.put(key, id);
            return book;
        } else {
            // update
            Long existingId = idByIsbn.get(key);
            if (existingId != null && !existingId.equals(book.getId())) {
                throw new IllegalArgumentException("ISBN already exists: " + book.getIsbn());
            }
            byId.put(book.getId(), book);
            return book;
        }
    }

    @Override
    public synchronized Optional<Book> findById(long id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public synchronized Optional<Book> findByIsbn(String isbn) {
        if (isbn == null) return Optional.empty();
        Long id = idByIsbn.get(isbn.toLowerCase());
        return  Optional.ofNullable(byId.get(id));
    }

    @Override
    public synchronized List<Book> findAll() {
        return new ArrayList<>(byId.values());
    }

    @Override
    public synchronized boolean deleteById(long id) {
        Book removed = byId.remove(id);
        if (removed != null) {
            idByIsbn.remove(removed.getIsbn().toLowerCase());
            return true;
        }
        return false;
    }
}
