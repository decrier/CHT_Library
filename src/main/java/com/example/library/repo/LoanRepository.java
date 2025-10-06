package com.example.library.repo;

import com.example.library.model.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    /**
     * Создает (если id == null) или обновляет (если id != null) выдачу.
     * Возвращает тот же объект с назначенным id при создании
     */

    Loan save(Loan loan);

    /** поиск по id */
    Optional findById(long id);

    /** все выдачи */
    List<Loan> findAll();

    /** все выдачи конкретного пользователя */
    List<Loan> findByUserId(Long userId);

    /** все выдачи конкретной книги */
    List<Loan> findByBookId(Long bookId);

    /** ищет незакрытую (returnDate==null) выдачу по паре (userId, bookId)
     * Если такой нет - empty
     * */
    Optional<Loan> findOpenByUserAndBook(long userId, long bookId);

    /** удаление по id. true, если запись существовала и удалена */
    boolean deleteById(long id);
}
