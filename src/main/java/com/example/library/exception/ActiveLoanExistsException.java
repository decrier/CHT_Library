package com.example.library.exception;

public class ActiveLoanExistsException extends RuntimeException {
    public ActiveLoanExistsException(long userId, long bookId) {
        super("active lo<n already exists for userId=" + userId + ", bookID=" + bookId);
    }
}
