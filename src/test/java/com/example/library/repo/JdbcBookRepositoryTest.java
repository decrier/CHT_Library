package com.example.library.repo;

import com.example.library.db.Schema;
import com.example.library.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcBookRepositoryTest {

    private JdbcBookRepository repo;

    @BeforeEach
    void setUp() throws SQLException {
        // обнуляем и создаём таблицу на каждый тест
        Schema.init();
        repo = new JdbcBookRepository();
    }

    @Test
    void insert_then_findById_and_findAll() {
        Book book = new Book("111", "Clean Code", "Robert C. Martin", 2008, 3);
        assertNull(book.getId());
        repo.save(book);
        assertNotNull(book.getId(), "после insert у книги должен появиться id");

        // find by Id
        Optional<Book> found = repo.findById(book.getId());
        assertTrue(found.isPresent());
        assertEquals("Clean Code", found.get().getTitle());
        assertEquals(2008, found.get().getPubYear());
        assertEquals(3, found.get().getCopiesTotal());
        assertEquals(3, found.get().getCopiesAvailable());

        // find All
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void update_changes_are_persisted() {
        Book book = new Book("111", "Clean Code", "Robert C. Martin", 2008, 2);
        repo.save(book);
        Long id = book.getId();
        assertNotNull(id);

        // act: меняем название и доступные экземпляры
        book.setTitle("Effective Java (3rd)");
        book.setCopiesAvailable(1);
        repo.save(book);

        // assert: читаем обратно и проверяем изменения
        Book loaded = repo.findById(id).orElseThrow();
        assertEquals("Effective Java (3rd)", loaded.getTitle());
        assertEquals(1, loaded.getCopiesAvailable());

        // размер таблицы не изменился
        assertEquals(1, repo.findAll().size());
    }

    @Test
    void deleteById_removes_row() {
        Book a = new Book("333", "Design Patterns", "GoF", 1994, 1);
        Book b = new Book("444", "Refactoring", "Martin Fowler", 1999, 2);
        repo.save(a);
        repo.save(b);
        assertEquals(2, repo.findAll().size());

        // act
        boolean removed = repo.deleteById(a.getId());
        assertTrue(removed, "deleteById должен вернуть true при удалении существующей записи");

        // assert
        assertEquals(1, repo.findAll().size());
        assertTrue(repo.findById(a.getId()).isEmpty());
        assertTrue(repo.findByIsbn("333").isEmpty());
        assertTrue(repo.findByIsbn("444").isPresent());
    }

}
