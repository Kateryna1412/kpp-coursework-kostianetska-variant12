package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {
    public void add(Equipment e) throws SQLException {
        String sql = "INSERT INTO equipment(name,category,rental_rate) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, e.getName());
            p.setString(2, e.getCategory());
            p.setDouble(3, e.getRentalRate());
            p.executeUpdate();
        }
    }

    public List<Equipment> getAll() throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT id,name,category,rental_rate FROM equipment ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(new Equipment(
                        r.getLong("id"),
                        r.getString("name"),
                        r.getString("category"),
                        r.getDouble("rental_rate")
                ));
            }
        }
        return list;
    }

    public Equipment getById(long id) throws SQLException {
        String sql = "SELECT id,name,category,rental_rate FROM equipment WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? new Equipment(
                        r.getLong("id"),
                        r.getString("name"),
                        r.getString("category"),
                        r.getDouble("rental_rate")
                ) : null;
            }
        }
    }

    public void update(Equipment e) throws SQLException {
        String sql = "UPDATE equipment SET name=?,category=?,rental_rate=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, e.getName());
            p.setString(2, e.getCategory());
            p.setDouble(3, e.getRentalRate());
            p.setLong(4, e.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM equipment WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }
}