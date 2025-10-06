package com.example.library.model;

import java.util.Objects;

public class User {

    private Long id;            // назначается репозиторием позже
    private String name;        // обязателен
    private String email;       // обязателен, уникален (проверит репозиторий)

    public User (String name, String email) {
        setName(name);
        setEmail(email);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        this.name = name.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(email == null || email.isBlank()) throw new IllegalArgumentException("email must not be blank");
        String e = email.toLowerCase().trim();
        if (!e.contains("@") || e.startsWith("@") || e.endsWith("@"))
            throw new IllegalArgumentException("invalid email format");
        this.email = e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User u)) return false;
        return email.equals(u.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
