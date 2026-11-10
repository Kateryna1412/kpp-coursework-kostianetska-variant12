package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalRequestDAO {

    public void add(RentalRequest req) throws SQLException {
        String sql = "INSERT INTO rental_request(client_id, equipment_id, state, f1, f2, f3, integral_index) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, req.getClientId());
            p.setLong(2, req.getEquipmentId());
            p.setString(3, req.getState().name());
            p.setDouble(4, req.getF1());
            p.setDouble(5, req.getF2());
            p.setDouble(6, req.getF3());
            p.setDouble(7, req.getIntegralIndex());
            p.executeUpdate();
        }
    }

    public List<RentalRequest> getAll() throws SQLException {
        List<RentalRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM rental_request ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public RentalRequest getById(long id) throws SQLException {
        String sql = "SELECT * FROM rental_request WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(RentalRequest req) throws SQLException {
        String sql = "UPDATE rental_request SET client_id=?, equipment_id=?, state=?, f1=?, f2=?, f3=?, integral_index=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, req.getClientId());
            p.setLong(2, req.getEquipmentId());
            p.setString(3, req.getState().name());
            p.setDouble(4, req.getF1());
            p.setDouble(5, req.getF2());
            p.setDouble(6, req.getF3());
            p.setDouble(7, req.getIntegralIndex());
            p.setLong(8, req.getId());
            p.executeUpdate();
        }
    }

    public void updateState(long id, RentalRequestState state) throws SQLException {
        String sql = "UPDATE rental_request SET state=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, state.name());
            p.setLong(2, id);
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM rental_request WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private RentalRequest map(ResultSet r) throws SQLException {
        return new RentalRequest(
                r.getLong("id"),
                r.getLong("client_id"),
                r.getLong("equipment_id"),
                RentalRequestState.valueOf(r.getString("state")),
                r.getDouble("f1"),
                r.getDouble("f2"),
                r.getDouble("f3"),
                r.getDouble("integral_index")
        );
    }
}