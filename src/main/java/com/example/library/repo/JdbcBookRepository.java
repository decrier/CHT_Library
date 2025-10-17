package com.example.library.repo;

import com.example.library.db.Db;
import com.example.library.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class JdbcBookRepository implements BookRepository {

    @Override
    public Book save(Book book) {
        if (book == null) throw new IllegalArgumentException("book is null");
        return (book.getId() == null) ? insert(book) : update(book);
    }

    private Book insert(Book book) {
        final String sql = """
                INSERT INTO books (isbn, title, author, pub_year, copies_total, copies_avail)
                VALUES (?,?,?,?,?,?)
                """;
        try (Connection conn = Db.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setInt(4, book.getPubYear());
            ps.setInt(5, book.getCopiesTotal());
            ps.setInt(6, book.getCopiesAvailable());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()){
                if (keys.next()) {
                    book.setId(keys.getLong(1));
                }
            }
            return book;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("ISBN already exists: " + book.getIsbn());
        } catch (SQLException e) {
            throw new RuntimeException("DB error on insert book", e);
        }
    }

    private Book update(Book book) {
        final String sql = """
                UPDATE books
                SET isbn=?, title=?, author=?, pub_year=?, copies_total=?, copies_avail=?
                WHERE id=?
                """;
        try (Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setInt(4, book.getPubYear());
            ps.setInt(5, book.getCopiesTotal());
            ps.setInt(6, book.getCopiesAvailable());
            ps.setLong(7, book.getId());

            int updated = ps.executeUpdate();
            if (updated == 0){
                throw new IllegalArgumentException("book not found by id=" + book.getId());
            }
            return book;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("ISBN already exists: " + book.getIsbn());
        } catch (SQLException e) {
            throw new RuntimeException("DB error on update book", e);
        }
    }

    @Override
    public Optional<Book> findById(long id) {
        final String sql = """
                SELECT id, isbn, title, author, pub_year, copies_total, copies_avail
                FROM books
                WHERE id=?
                """;
        try (Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()){
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error on findById", e);
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        final String sql = """
                SELECT id, isbn, title, author, pub_year, copies_total, copies_avail
                FROM books
                WHERE isbn=?
                """;
        try (Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()){
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error on findByIsbn", e);
        }
    }

    @Override
    public boolean deleteById(long id) {
        final String sql = """
                DELETE FROM books
                WHERE id =?
                """;
        try (Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("DB error on deleteById", e);
        }
    }

    @Override
    public List<Book> findAll() {
        final String sql = """
                SELECT id, isbn, title, author, pub_year, copies_total, copies_avail
                FROM books
                ORDER BY id
                """;
        try (Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            List<Book> out = new ArrayList<>();
            while (rs.next()) {
                out.add(map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("DB error on findAll", e);
        }
    }

    @Override
    public List<Book> search(String q) {
        if (q == null || q.isBlank()) {
            return findAll();
        }
        String needle = "%" + q.toLowerCase(Locale.ROOT) + "%";

        final String sql = """
                SELECT id, isbn, title, author, pub_year, copies_total, copies_avail
                FROM books
                WHERE LOWER(isbn) LIKE ?
                    OR LOWER(title) LIKE ?
                    OR LOWER(author) LIKE ?
                ORDER BY id
                """;
        try(Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, needle);
            ps.setString(2, needle);
            ps.setString(3, needle);

            try (ResultSet rs = ps.executeQuery()) {
                List<Book> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(map(rs));
                }
                return out;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error on search", e);
        }
    }

    private static Book map(ResultSet rs) throws SQLException {
        Book book = new Book(
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getInt("pub_year"),
                rs.getInt("copies_total")
        );
        book.setId(rs.getLong("id"));
        book.setCopiesAvailable(rs.getInt("copies_avail"));
        return book;
    }
}
