package com.example.library.repo;

import com.example.library.db.Db;
import com.example.library.model.Role;
import com.example.library.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository{

    @Override
    public User save(User user) {
        if (user == null) throw new IllegalArgumentException("user is null");
        return (user.getId() == null) ? insert(user) : update(user);
    }

    private User insert(User user){
        final String sql = """
                INSERT INTO users (full_name, email, role, membership_date, active)
                VALUES (?,?,?,?,?)
                """;
        try (Connection conn = Db.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole().name().toLowerCase());
            ps.setDate(4, Date.valueOf(user.getMembershipDate()));
            ps.setBoolean(5, user.isActive());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()){
                if (keys.next()) user.setId(keys.getLong(1));
            }
            return user;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("email already exists: " + user.getEmail());
        } catch (SQLException e) {
            // PostgreSQL кидает PSQLException c SQLState 23505 при unique violation
            if ("23505".equals(e.getSQLState())) {
                throw new IllegalArgumentException("email already exists: " + user.getEmail());
            }
            throw new RuntimeException("DB error on insert user", e);
        }
    }

    private User update(User user) {
        final String sql = """
                UPDATE users
                SET full_name=?, email=?, role=?, membership_date=?, active=?
                WHERE id=?
                """;
        try(Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole().name().toLowerCase());
            ps.setDate(4, Date.valueOf(user.getMembershipDate()));
            ps.setBoolean(5, user.isActive());
            ps.setLong(6, user.getId());

            int updated = ps.executeUpdate();
            if (updated == 0){
                throw new IllegalArgumentException("user not found by id:" + user.getId());
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("DB error on update user", e);
        }
    }

    @Override
    public List<User> findAll() {
        final String sql = """
                SELECT id, full_name, email, role, membership_date, active 
                FROM users
                ORDER BY id
                """;
        try (Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            List<User> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("DB error on findAll users", e);
        }
    }

    @Override
    public Optional<User> findById(long id) {
        final String sql = """
                SELECT id, full_name, email, role, membership_date, active 
                FROM users
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
    public Optional<User> findByEmail(String email) {
        final String sql = """
                SELECT id, full_name, email, role, membership_date, active 
                FROM users
                WHERE email=?
                """;
        try (Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            ps.setString(1, email.toLowerCase());
            try (ResultSet rs = ps.executeQuery()){
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error on findByEmail", e);
        }
    }

    @Override
    public boolean deleteById(long id) {
        final String sql = "DELETE FROM users WHERE id=?";
        try (Connection conn = Db.getDataSource().getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // 23503 — FK violation (есть ссылки в loans)
            if ("23503".equals(e.getSQLState())) {
                throw new IllegalStateException("Нельзя удалить пользователя: по нему есть записи в loans");
            }
            throw new RuntimeException("DB error on deleteById", e);
        }
    }

    /** Мягкое удаление: active=false */

    private User map(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("full_name"),
                rs.getString("email")
        );
        user.setId(rs.getLong("id"));

        String roleStr = rs.getString("role");
        if (roleStr != null) {
            user.setRole(Role.valueOf(roleStr.toUpperCase()));
        }

        Date membershipDate = rs.getDate("membership_date");
        if (membershipDate != null) {
            user.setMembershipDate(membershipDate.toLocalDate());
        } else {
            user.setMembershipDate(LocalDate.now());
        }

        user.setActive(rs.getBoolean("active"));
        return user;
    }
}
