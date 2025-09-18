package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.repo.BookRepository;

import java.util.List;
import java.util.Objects;

public class CatalogService {

    private final BookRepository repo;

    public CatalogService(BookRepository repo){
        this.repo = Objects.requireNonNull(repo);
    }

    /** Добавляет книгу в каталог и возвращает её (с назначенным id). */
    public Book addBook(String isbn, String title, String author, int year, int copiesTotal) {
        Book b = new Book(isbn, title,author, year, copiesTotal);
        return repo.save(b);
    }

    /** Возвращает все книги. */
    public List<Book> listAll() {
        return repo.findAll();
    }

    /** Поиск по вхождению в title/author (без учёта регистра).
     *  Пустой или null-запрос = вернуть все книги.
     *  */
    public List<Book> search(String query) {
        if (query == null || query.isBlank()) return repo.findAll();
        String q = query.toLowerCase();
        return repo.findAll().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q)
                                || b.getAuthor().toLowerCase().contains(q))
                .toList();
    }

    public boolean remove(long id) {
        return repo.deleteById(id);
    }

    /** Выдать экземпляр книги. Возвращает true, если успешно. */
    public boolean loan(long id) {
        var opt = repo.findById(id);
        if (opt.isEmpty()) return false;
        Book b = opt.get();
        if (b.getCopiesAvailable() <= 0) return false;
        b.setCopiesAvailable(b.getCopiesAvailable() - 1);
        repo.save(b); // обновляем запись
        return true;
    }

    /** Принять возврат экземпляра. Возвращает true, если успешно. */
    public boolean returnCopy(long id) {
        var opt = repo.findById(id);
        if (opt.isEmpty()) return false;
        var b = opt.get();
        if (b.getCopiesAvailable() >= b.getCopiesTotal()) return false;
        b.setCopiesAvailable(b.getCopiesAvailable() + 1);
        repo.save(b); // обновляем запись
        return true;
    }
}
