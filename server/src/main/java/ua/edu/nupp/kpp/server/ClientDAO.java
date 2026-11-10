package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    public void add(Client cl) throws SQLException {
        String sql = "INSERT INTO client(full_name,phone,email) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, cl.getFullName());
            p.setString(2, cl.getPhone());
            p.setString(3, cl.getEmail());
            p.executeUpdate();
        }
    }

    public List<Client> getAll() throws SQLException {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT id,full_name,phone,email FROM client ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(new Client(
                        r.getLong("id"),
                        r.getString("full_name"),
                        r.getString("phone"),
                        r.getString("email")
                ));
            }
        }
        return list;
    }

    public Client getById(long id) throws SQLException {
        String sql = "SELECT id,full_name,phone,email FROM client WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? new Client(
                        r.getLong("id"),
                        r.getString("full_name"),
                        r.getString("phone"),
                        r.getString("email")
                ) : null;
            }
        }
    }

    public void update(Client cl) throws SQLException {
        String sql = "UPDATE client SET full_name=?,phone=?,email=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, cl.getFullName());
            p.setString(2, cl.getPhone());
            p.setString(3, cl.getEmail());
            p.setLong(4, cl.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM client WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }
}