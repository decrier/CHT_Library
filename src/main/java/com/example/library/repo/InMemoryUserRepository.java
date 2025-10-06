package com.example.library.repo;

import com.example.library.model.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository implements UserRepository{

    private final Map<Long, User> byId = new LinkedHashMap<>();     // порядок добавления
    private final Map<String, Long> idByEmail = new HashMap<>();    // email(lower) -> id
    private final AtomicLong seq = new AtomicLong(1);

    @Override
    public synchronized User save(User user) {
        if (user == null) throw new IllegalArgumentException("user is null");
        String key = user.getEmail().toLowerCase();

        if (user.getId() == null) {
            // create
            if (idByEmail.containsKey(key)){
                throw new IllegalArgumentException("email already exists");
            }
            long id = seq.getAndIncrement();
            user.setId(id);
            byId.put(id, user);
            idByEmail.put(key, id);
            return user;
        } else {
            // update
            Long existingId = idByEmail.get(key);
            if (existingId != null && !existingId.equals(user.getId())) {
                throw new IllegalArgumentException("email already exists");
            }
            byId.put(user.getId(), user);
            idByEmail.put(key, user.getId());
            return user;
        }
    }

    @Override
    public synchronized Optional<User> findById(long id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        Long id = idByEmail.get(email.toLowerCase());
        return id == null ? Optional.empty() : Optional.ofNullable(byId.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(byId.values());
    }

    @Override
    public boolean deleteById(long id) {
        User removed = byId.remove(id);
        if (removed != null) {
            idByEmail.remove(removed.getEmail());
            return true;
        }
        return false;
    }
}
