package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.repo.InMemoryBookRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CatalogServiceTest {

    @Test
    void addBook_assignsId_and_listAllReturnsIt() {
        var repo = new InMemoryBookRepository();
        var service = new CatalogService(repo);

        Book b = service.addBook("978-0-13-235088-4", "Clean Code", "Robert C. Martin", 2008, 3);
        assertNotNull(service.listAll());
        assertNotNull(b.getId(), "после addBook у книги должен быть назначен id");
        assertEquals(1, service.listAll().size(), "в каталоге должна быть 1 книга");
        assertEquals("Clean Code", service.listAll().get(0).getTitle());
    }

    @Test
    void search_findsByTitleOrAuthor_caseInsensitive() {
        var repo = new InMemoryBookRepository();
        var service = new CatalogService(repo);

        service.addBook("111-1", "Clean Code", "Robert C. Martin", 2008, 2);
        service.addBook("222-2", "Effective Java", "Joshua Bloch", 2018, 1);
        service.addBook("333-3", "Design Patterns", "GoF", 1994, 1);

        // поиск по названию
        var r1 = service.search("clean");
        assertEquals(1, r1.size());
        assertEquals("Clean Code", r1.get(0).getTitle());

        // поиск по автору
        var r2 = service.search("bloch");
        assertEquals(1, r2.size());
        assertEquals("Joshua Bloch", r2.get(0).getAuthor());

        // пустой запрос - возвращает все
        var r3 = service.search(" ");
        assertEquals(3, r3.size());
    }

    @Test
    void removeBook_deletesFromRepo() {
        var repo = new InMemoryBookRepository();
        var service = new CatalogService(repo);

        Book b1 = service.addBook("111-1", "Clean Code", "Robert C. Martin", 2008, 2);
        Book b2 = service.addBook("222-2", "Effective Java", "Joshua Bloch", 2018, 1);

        assertEquals(2, service.listAll().size());

        boolean removed = service.remove(b1.getId());
        assertTrue(removed);

        assertEquals(1, service.listAll().size());
        assertEquals("Effective Java", service.listAll().get(0).getTitle());
    }

    @Test
    void loan_and_return_happyPath() {
        var repo = new InMemoryBookRepository();
        var service = new CatalogService(repo);

        Book b = service.addBook("111-1", "Clean Code", "Robert C. Martin", 2008, 2);
        assertEquals(2, service.listAll().get(0).getCopiesAvailable());

        // выдаем один экз
        assertTrue(service.loan(1));
        assertEquals(1, service.listAll().get(0).getCopiesAvailable());

        // возвращаем
        assertTrue(service.returnCopy(1));
        assertEquals(2, service.listAll().get(0).getCopiesAvailable());
    }
}
