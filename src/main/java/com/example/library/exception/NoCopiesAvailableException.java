package com.example.library.exception;

public class NoCopiesAvailableException extends RuntimeException {
    public NoCopiesAvailableException(long bookId) {
        super("no copies available for book id=" + bookId);
    }
}
