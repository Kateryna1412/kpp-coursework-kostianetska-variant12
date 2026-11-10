package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RentalContractDAO {
    public void add(RentalContract rc) throws SQLException {
        String sql = "INSERT INTO rental_contract(request_id,contract_date,total_cost) VALUES(?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, rc);
            p.executeUpdate();
        }
    }

    public List<RentalContract> getAll() throws SQLException {
        List<RentalContract> list = new ArrayList<>();
        String sql = "SELECT * FROM rental_contract ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                list.add(map(r));
            }
        }
        return list;
    }

    public RentalContract getById(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM rental_contract WHERE id=?")) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(RentalContract rc) throws SQLException {
        String sql = "UPDATE rental_contract SET request_id=?,contract_date=?,total_cost=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, rc);
            p.setLong(4, rc.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM rental_contract WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, RentalContract rc) throws SQLException {
        p.setLong(1, rc.getRequestId());
        p.setString(2, rc.getContractDate());
        p.setDouble(3, rc.getTotalCost());
    }

    private RentalContract map(ResultSet r) throws SQLException {
        return new RentalContract(
                r.getLong("id"),
                r.getLong("request_id"),
                r.getString("contract_date"),
                r.getDouble("total_cost")
        );
    }
}