package com.example.library.repo;

import com.example.library.db.Db;
import com.example.library.model.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLoanRepository implements LoanRepository{

    @Override
    public Loan save(Loan loan) {
        if (loan == null) throw new IllegalArgumentException("loan is null");
        return loan.getId() == null ? insert(loan) : update(loan);
    }

    private Loan insert(Loan loan) {
        final String sql = """
                INSERT INTO loans (book_id, user_id, loan_date, due_date, return_date)
                VALUES (?,?,?,?,?)
                """;
        try (Connection conn = Db.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, loan.getBookId());
            ps.setLong(2, loan.getUserId());
            ps.setObject(3, loan.getLoanDate());
            ps.setObject(4, loan.getDueDate());
            ps.setObject(5, loan.getReturnDate());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()) loan.setId(rs.getLong(1));
            }
            return loan;

        } catch (SQLException e) {
            throw new RuntimeException("DB error on insert loan", e);
        }
    }

    private Loan update(Loan loan) {
        final String sql = """
                UPDATE loans
                SET book_id=?, user_id=?, loan_date=?, due_date=?, return_date=?
                WHERE id=?
                """;
        try (Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setLong(1, loan.getBookId());
            ps.setLong(2, loan.getUserId());
            ps.setObject(3, loan.getLoanDate());
            ps.setObject(4, loan.getDueDate());
            ps.setObject(5, loan.getReturnDate());
            ps.setLong(6, loan.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) throw new IllegalArgumentException("loan not founf id=" + loan.getId());
            return loan;

        } catch (SQLException e) {
            throw new RuntimeException("DB error on insert loan", e);
        }
    }

    @Override
    public Optional<Loan> findById(long id) {
        final String sql = """
                SELECT id, book_id, user_id, loan_date, due_date, return_date
                FROM loans WHERE id=?
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
    public List<Loan> findAll() {
        final String sql = """
                SELECT id, book_id, user_id, loan_date, due_date, return_date FROM loans
                """;
        try (Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            List<Loan> found = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) found.add(map(rs));
            return found;

        } catch (SQLException e) {
            throw new RuntimeException("DB error on findAll", e);
        }
    }

    @Override
    public List<Loan> findByUserId(Long userId) {
        final String sql = """
                SELECT id, book_id, user_id, loan_date, due_date, return_date 
                FROM loans WHERE user_id=? ORDER BY id
                """;
        try (Connection conn = Db.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()){
                List<Loan> found = new ArrayList<>();
                while (rs.next()) {
                    found.add(map(rs));
                }
                return found;
            }

        } catch (SQLException e) {
            throw new RuntimeException("DB error on findByUserId", e);
        }
    }

    @Override
    public List<Loan> findByBookId(Long bookId) {
        final String sql = """
                SELECT id, book_id, user_id, loan_date, due_date, return_date 
                FROM loans WHERE book_id=? ORDER BY id
                """;
        try (Connection conn = Db.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, bookId);
            try (ResultSet rs = ps.executeQuery()){
                List<Loan> found = new ArrayList<>();
                while (rs.next()) {
                    found.add(map(rs));
                }
                return found;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error on findByBookId", e);
        }
    }

    @Override
    public Optional<Loan> findOpenByUserAndBook(long userId, long bookId) {
        final String sql = """
                SELECT id, book_id, user_id, loan_date, due_date, return_date 
                FROM loans WHERE book_id=? AND user_id=? AND return_date is NULL LIMIT 1
                """;
        try (Connection conn = Db.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, bookId);
            ps.setLong(2, userId);
            try (ResultSet rs = ps.executeQuery()){
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error on findOpenByUserAndBook", e);
        }
    }

    @Override
    public boolean deleteById(long id) {
        final String sql = "DELETE FROM loans WHERE id=?";
        try (Connection conn = Db.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("DB error on deleteById", e);
        }
    }

    private static Loan map(ResultSet rs) throws SQLException {
        Loan loan = new Loan(
                rs.getLong("book_id"),
                rs.getLong("user_id"),
                rs.getObject("loan_date", LocalDate.class),
                rs.getObject("due_date", LocalDate.class)
        );
        loan.setId(rs.getLong("id"));
        LocalDate returnDate = rs.getObject("return_date", LocalDate.class);
        loan.setReturnDate(returnDate);
        return loan;
    }

    @Override
    public List<Loan> findActive() {
        return findAll().stream().filter(l -> l.getReturnDate() == null).toList();
    }
}
