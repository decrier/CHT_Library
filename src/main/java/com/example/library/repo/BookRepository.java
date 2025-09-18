package com.example.library.repo;

import com.example.library.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    /**
     * Создаёт новую запись (если id == null) или обновляет существующую (если id != null).
     * Должен вернуть ту же ссылку (book) с назначенным id в случае создания.
     */
    Book save(Book book);

    /** Поиск по внутреннему id */
    Optional<Book> findById(long id);

    /** Поиск по ISBN (без учета регистра в реализации) */
    Optional<Book> findByIsbn(String isbn);

    /** Список всех книг (порядок не важен на уровне контракта */
    List<Book> findAll();

    /** Удаление по id. Возвращает true, если запись существовала и была удалена */
    boolean deleteById(long id);
}
