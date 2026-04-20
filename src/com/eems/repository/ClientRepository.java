package com.eems.repository;

import com.eems.model.Client;
import com.eems.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository implements Repository<Client> {

    @Override
    public Client create(Client client) throws SQLException {
        String sql = "INSERT INTO Client (name, industry, contact_name, contact_phone, contact_email) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getIndustry());
            ps.setString(3, client.getContactName());
            ps.setString(4, client.getContactPhone());
            ps.setString(5, client.getContactEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) client.setId(keys.getInt(1));
            }
        }
        return client;
    }

    @Override
    public Client findById(int id) throws SQLException {
        String sql = "SELECT * FROM Client WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<Client> findAll() throws SQLException {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT * FROM Client";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void update(Client client) throws SQLException {
        String sql = "UPDATE Client SET name=?, industry=?, contact_name=?, contact_phone=?, contact_email=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getIndustry());
            ps.setString(3, client.getContactName());
            ps.setString(4, client.getContactPhone());
            ps.setString(5, client.getContactEmail());
            ps.setInt(6, client.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Client WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void assignToProject(int clientId, int projectId) throws SQLException {
        String sql = "INSERT INTO Client_Project (client_id, project_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.setInt(2, projectId);
            ps.executeUpdate();
        }
    }

    public void removeFromProject(int clientId, int projectId) throws SQLException {
        String sql = "DELETE FROM Client_Project WHERE client_id=? AND project_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            ps.setInt(2, projectId);
            ps.executeUpdate();
        }
    }

    public List<Client> findByProjectIds(List<Integer> projectIds) throws SQLException {
        if (projectIds.isEmpty()) return new ArrayList<>();
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < projectIds.size(); i++) {
            if (i > 0) placeholders.append(", ");
            placeholders.append("?");
        }
        String sql = "SELECT DISTINCT c.* FROM Client c " +
                     "JOIN Client_Project cp ON c.id = cp.client_id " +
                     "WHERE cp.project_id IN (" + placeholders + ")";
        List<Client> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < projectIds.size(); i++) ps.setInt(i + 1, projectIds.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Client mapRow(ResultSet rs) throws SQLException {
        return new Client(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("industry"),
                rs.getString("contact_name"),
                rs.getString("contact_phone"),
                rs.getString("contact_email")
        );
    }
}
