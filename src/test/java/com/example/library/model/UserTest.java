package com.example.library.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    void create_ok_and_normalizesEmail() {
        User u = new User(" Ivan Petrov ", "IVAN@example.com");
        assertNull(u.getId());
        assertEquals("Ivan Petrov", u.getName());
        assertEquals("ivan@example.com", u.getEmail());
    }

    @Test
    void bad_name_or_email_throw() {
        assertThrows(IllegalArgumentException.class, () -> new User(" ", "mail@mail.com"));
        assertThrows(IllegalArgumentException.class, () -> new User("Ivan", " "));
    }

    @Test
    void bad_email_format_throw() {
        assertThrows(IllegalArgumentException.class, () -> new User("Ivan", "@mail"));
        assertThrows(IllegalArgumentException.class, () -> new User("Ivan", "mail@"));
        assertThrows(IllegalArgumentException.class, () -> new User("Ivan", "no-at"));
    }
}
