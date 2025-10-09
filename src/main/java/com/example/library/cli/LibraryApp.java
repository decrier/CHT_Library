package com.example.library.cli;

import com.example.library.db.Schema;
import com.example.library.repo.InMemoryBookRepository;
import com.example.library.repo.JdbcBookRepository;
import com.example.library.service.CatalogService;
import com.example.library.model.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class LibraryApp {
    public static void main(String[] args) throws SQLException {
        Schema.init();

        var repo = new JdbcBookRepository();
        var service = new CatalogService(repo);
        var in = new Scanner(System.in);

        service.addBook("111-1", "Clean Code", "Robert C. Martin", 2008, 2);
        service.addBook("222-2", "Effective Java", "Joshua Bloch", 2018, 1);

        System.out.println("""
                Команды:
                  add             - добавить новую книгу
                  list            - показать все книги
                  search <строка> - поиск по названию/автору
                  remove <id>     - удалить книгу по id
                  loan <id>       — выдать экземпляр (avail-1)
                  return <id>     — принять возврат (avail+1)
                  exit            - выйти
                """);
        while (true) {
            System.out.print("> ");
            String cmd = in.nextLine().toLowerCase().trim();

            switch (cmd) {
                case "add" -> addBookInteractively(service, in);
                case "list" -> printBooks(service.listAll());
                case "" -> {}
                case "exit" -> {
                    System.out.println("пока!");
                    return;
                }
                default -> {
                    if (cmd.startsWith("search ")) {
                        String q = cmd.substring("search ".length()).trim();
                        searchAndPrint(service, q);
                    } else if (cmd.startsWith("remove ")) {
                        String arg = cmd.substring("remove ".length()).trim();
                        removeById(service, arg);
                    } else if (cmd.startsWith("loan ")) {
                        String arg = cmd.substring("loan ".length()).trim();
                        loanById(service, arg);
                    } else if (cmd.startsWith("return ")) {
                        String arg = cmd.substring("return ".length()).trim();
                        returnById(service, arg);
                    } else {
                        System.out.println("Неизвестная команда.");
                    }
                }
            }
        }
    }

    private static void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("(пусто)");
            return;
        }
        for (Book b:  books) {
            System.out.printf("#%d | %s | %s | %s | %d | total=%d avail=%d%n",
                    b.getId(), b.getIsbn(), b.getTitle(), b.getAuthor(), b.getPubYear(), b.getCopiesTotal(), b.getCopiesAvailable());
        }
    }

    private static void addBookInteractively(CatalogService service, Scanner in) {
        try {
            String isbn = readNonBlanc(in, "ISBN: ");
            String title = readNonBlanc(in, "Название: ");
            String author = readNonBlanc(in, "Автор: ");
            int year = readYear(in, "Год издания (начиная с 1450): ");
            int copies = readInt(in, "Количество экземпляром (>=0): ", 0, Integer.MAX_VALUE);

            var b = service.addBook(isbn, title, author, year, copies);
            System.out.printf("✅ Добавлена книга #%d: %s - %s (%d), total=%d avail=%d%n",
                    b.getId(), b.getTitle(), b.getAuthor(), b.getPubYear(), b.getCopiesTotal(), b.getCopiesAvailable());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Ошибка: " + e.getMessage());
        }
    }

    private static String readNonBlanc(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            if (!s.isBlank()) return s;
            System.out.println("Поле ввода не может быть пустым. Повторите ввод");
        }
    }

    private static int readYear(Scanner in, String prompt) {
        int min = 1450;
        int max = java.time.Year.now().getValue() + 1;
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try {
                int year = Integer.parseInt(s);
                if (year < min || year > max) {
                    System.out.printf("Год вне допустимого диапазона: [%d..%d]", min, max);
                    continue;
                }
                return year;
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число!");
            }
        }
    }

    private static int readInt(Scanner in, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = in.nextLine().trim();
            try {
                int val = Integer.parseInt(s);
                if (val < min || val > max) {
                    System.out.printf("Число вне диапазона [%d..%d]", min, max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число!");
            }
        }
    }

    private static void searchAndPrint(CatalogService service, String query) {
        if (query == null || query.isBlank()) {
            System.out.println("Пустая строка запроса");
            return;
        }
        List<Book> found = service.search(query);
        if (!found.isEmpty()) {
            printBooks(found);
        } else {
            System.out.println("По запросу ничего не найдено");
        }
    }

    private static void removeById(CatalogService service, String idArg) {
        if (idArg.isEmpty()) {
            System.out.println("Укажите id: remove <id>");
            return;
        }

        try {
            long id = Long.parseLong(idArg);
            boolean ok = service.remove(id);
            if (ok) {
                System.out.println("✅ Книга удалена: id=" + id);
            } else {
                System.out.println("❌ Книга с таким id не найдена: " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("ID должен быть целым числом!");
        }
    }

    private static void loanById(CatalogService service, String idArg) {
        if (idArg.isEmpty()) {
            System.out.println("Укажите id: loan <id>");
            return;
        }
        try {
            long id = Long.parseLong(idArg);
            boolean ok = service.loan(id);
            if (ok) {
                System.out.println("✅ Экземпляр выдан: id=" + id);
            } else {
                System.out.println("❌ Нельзя выдать: либо книги нет, либо нет доступных экземпляров.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID должен быть целым числом!");
        }
    }

    private static void returnById(CatalogService service, String idArg) {
        if (idArg.isEmpty()) {
            System.out.println("Укажите id: loan <id>");
            return;
        }
        try {
            long id = Long.parseLong(idArg);
            boolean ok = service.returnCopy(id);
            if (ok) {
                System.out.println("✅ Возврат принят: id=" + id);
            } else {
                System.out.println("❌ Нельзя принять возврат: книги нет или все экземпляры уже в наличии.");
            }
        } catch (NumberFormatException e) {
            System.out.println("ID должен быть целым числом!");
        }
    }
}
