package com.example.library.repo;

import com.example.library.model.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryUserRepositoryTest {

    @Test
    void save_assignsId_and_findByEmailWorks() {
        var repo = new InMemoryUserRepository();
        var u = new User("Ivan Petrov", "ivan@example.com");

        assertNull(u.getId(), "у нового пользователя id должен быть null");

        var saved = repo.save(u);
        assertNotNull(saved.getId(), "после save должен появиться id");
        assertEquals(saved.getId(), u.getId(), "save должен установить id в исходный объект");

        Optional<User> byEmail = repo.findByEmail("IVAN@EXAMPLE.com");
        assertTrue(byEmail.isPresent(), "пользователь должен находиться по email (без учёта регистра)");
        assertEquals("Ivan Petrov", byEmail.get().getName());
        assertEquals(saved.getId(), byEmail.get().getId());
    }

    @Test
    void duplicateEmail_forbidden() {
        var repo = new InMemoryUserRepository();

        repo.save(new User("Ivan", "ivan@example.com"));

        assertThrows(IllegalArgumentException.class, () -> repo.save(new User("Other", "ivan@example.com")),
                "должно кидать исключение при попытке сохранить пользователя с таким же email");
        assertEquals(1, repo.findAll().size());
        assertEquals("Ivan", repo.findAll().get(0).getName());
    }
}
