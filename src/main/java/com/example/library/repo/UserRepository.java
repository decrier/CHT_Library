package com.example.library.repo;

import com.example.library.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    /**
     * Создаёт (если id == null) или обновляет (если id != null) пользователя.
     * Должен вернуть того же пользователя с назначенным id при создании.
     * Email должен быть уникальным (реализация проверит).
     */
    User save(User user);

    /** Поиск по внутреннему id. */
    Optional<User> findById(long id);

    /** Поиск по email (в реализации — без учёта регистра). */
    Optional<User> findByEmail(String email);

    /** Все пользователи. */
    List<User> findAll();

    /** Удаление по id. true — если пользователь существовал и удалён. */
    boolean deleteById(long id);

    public boolean deactivateById(long id);

    public boolean activateById(long id);

    public List<User> findAllAnyStatus();

    public List<User> findAllActiveUsers();

    public List<User> findAllInactiveUsers();
}
