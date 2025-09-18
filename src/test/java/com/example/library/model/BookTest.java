package com.example.library.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    void create_ok() {
        Book b = new Book("978-0-13-235088-4", "Clean Code", "Robert C.Martin", 2008, 3);
        assertEquals("Clean Code", b.getTitle());
        assertEquals(3, b.getCopiesTotal());
        assertEquals(3, b.getCopiesAvailable());
    }

    @Test
    void blank_fields_throw() {
        assertThrows(IllegalArgumentException.class, () -> new Book(" ", "T", "A", 2000, 1));
        assertThrows(IllegalArgumentException.class, () -> new Book("ISBN", "", "A", 2000, 1));
        assertThrows(IllegalArgumentException.class, () -> new Book("ISBN", "T", "", 2000, 1));
    }

    @Test
    void year_and_copies_validate() {
        assertThrows(IllegalArgumentException.class, () -> new Book("ISBN", "T", "A", 1400, 1));
        assertThrows(IllegalArgumentException.class, () -> new Book("ISBN", "T", "A", 3000, 1));
        Book b = new Book("ISBN", "T", "A", 2000, 2);
        assertThrows(IllegalArgumentException.class, () -> b.setCopiesAvailable(3));

    }
}
