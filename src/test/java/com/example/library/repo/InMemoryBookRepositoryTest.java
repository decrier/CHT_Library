package com.example.library.repo;

import com.example.library.model.Book;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryBookRepositoryTest {

    @Test
    void save_assignsId_and_findWorks() {
        InMemoryBookRepository repository = new InMemoryBookRepository();

        Book b = new Book("978-0-13-235088-4", "Clean Code", "Robert C. Martin", 2008, 3);
        assertNull(b.getId(), "у новой книги id должен быть null");

        Book saved = repository.save(b);
        assertNotNull(saved.getId(), "после save у книги должен появиться id");
        assertEquals(saved.getId(), b.getId(), "save должен установить id в исходный объект");

        Optional<Book> byId = repository.findById(saved.getId());
        assertTrue(byId.isPresent(), "книга должна находиться по id");
        assertEquals(saved, byId.get(), "найденная по id книга должна совпадать");

        Optional<Book> byIsbn = repository.findByIsbn("978-0-13-235088-4");
        assertTrue(byIsbn.isPresent(), "книга должна находиться по isbn");
        assertEquals(saved, byIsbn.get(),"найденная по isbn книга должна совпадать");

        assertEquals(1, repository.findAll().size(), "в репозитории должна быть ровно 1 книга");
    }

    @Test
    void duplicateIsbn_forbidden() {
        InMemoryBookRepository repo = new InMemoryBookRepository();

        Book b1 = new Book("111-1", "First", "Author A", 2000, 2);
        repo.save(b1);

        Book b2 = new Book("111-1", "Second", "Author B", 2005, 1);

        assertThrows(IllegalArgumentException.class,
                () -> repo.save(b2),
                "должно кидать исключение при попытке сохранить книгу с тем же ISBN");

        // Убеждаемся, что в репозитории осталась только первая книга
        assertEquals(1, repo.findAll().size(), "после дубликата в репозитории должна остаться только одна книга");
        assertEquals("First", repo.findAll().get(0).getTitle(), "в репозитории должна остаться именно первая книга");
    }

    @Test
    void deleteById_removesBook() {
        InMemoryBookRepository repo = new InMemoryBookRepository();

        Book b1 = new Book("222-2", "Second", "Author B", 2001, 1);
        Book b2 = new Book("333-3", "Third", "Author C", 2002, 1);

        repo.save(b1);
        repo.save(b2);

        //убеждаемся, что 2 книги добавлены
        assertEquals(2, repo.findAll().size());

        //удаляем первую
        boolean removed = repo.deleteById(b1.getId());
        assertTrue(removed, "deleteById должен вернуть true при успешном удалении");

        // проверяем, что ее больше нет
        assertTrue(repo.findById(b1.getId()).isEmpty(), "книга с этим id должна отсутствовать");
        assertTrue(repo.findByIsbn("222-2").isEmpty(), "по этому ISBN ничего не должно находиться");

        // осталась только вторая
        assertEquals(1, repo.findAll().size());
        assertEquals("Third", repo.findAll().get(0).getTitle());

    }
}
