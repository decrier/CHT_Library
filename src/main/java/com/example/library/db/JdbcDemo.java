package com.example.library.db;

import com.example.library.model.Book;
import com.example.library.model.User;
import com.example.library.repo.*;
import com.example.library.model.Loan;

import java.time.LocalDate;

public class JdbcDemo {
    public static void main(String[] args) throws Exception {
        Schema.init();

        // репозитории
        var books = new JdbcBookRepository();
        var users = new JdbcUserRepository();
        var loans = new JdbcLoanRepository();

        // тестовые данные
        var b = books.save(new Book("111", "Clean Code", "RCM", 2008, 2));
        var u = users.save(new User("Ivan", "ivan@example.com"));

        // создаём выдачу
        var l = new Loan(b.getId(), u.getId(), LocalDate.now(), LocalDate.now().plusDays(14));
        loans.save(l);
        System.out.println("Loan id=" + l.getId());

        // проверяем открытые выдачи
        System.out.println("Open? " + loans.findOpenByUserAndBook(u.getId(), b.getId()).isPresent());

        // закрываем
        l.setReturnDate(LocalDate.now().plusDays(7));
        loans.save(l);
        System.out.println("Open after return? " + loans.findOpenByUserAndBook(u.getId(), b.getId()).isEmpty());
    }
}
