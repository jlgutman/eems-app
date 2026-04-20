package com.eems.repository;

import com.eems.model.Project;
import com.eems.model.ProjectStatus;
import com.eems.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectRepository implements Repository<Project> {

    @Override
    public Project create(Project project) throws SQLException {
        String sql = "INSERT INTO Project (name, description, start_date, end_date, budget, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setDate(3, Date.valueOf(project.getStartDate()));
            ps.setDate(4, Date.valueOf(project.getEndDate()));
            ps.setDouble(5, project.getBudget());
            ps.setString(6, project.getStatus().getDbValue());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) project.setId(keys.getInt(1));
            }
        }
        return project;
    }

    @Override
    public Project findById(int id) throws SQLException {
        String sql = "SELECT * FROM Project WHERE id = ?";
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
    public List<Project> findAll() throws SQLException {
        List<Project> list = new ArrayList<>();
        String sql = "SELECT * FROM Project";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void update(Project project) throws SQLException {
        String sql = "UPDATE Project SET name=?, description=?, start_date=?, end_date=?, budget=?, status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, project.getName());
            ps.setString(2, project.getDescription());
            ps.setDate(3, Date.valueOf(project.getStartDate()));
            ps.setDate(4, Date.valueOf(project.getEndDate()));
            ps.setDouble(5, project.getBudget());
            ps.setString(6, project.getStatus().getDbValue());
            ps.setInt(7, project.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Project WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // sortColumn is whitelisted by DepartmentService before reaching here
    public List<Project> findActiveByDepartment(int departmentId, String sortColumn) throws SQLException {
        String sql = "SELECT p.* FROM Project p " +
                "JOIN Department_Project dp ON p.id = dp.project_id " +
                "WHERE dp.department_id = ? AND p.status = ? " +
                "ORDER BY p." + sortColumn;
        List<Project> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            ps.setString(2, ProjectStatus.ACTIVE.getDbValue());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<Project> findProjectsEndingWithinDays(int days) throws SQLException {
        String sql = "SELECT * FROM Project " +
                "WHERE end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY)";
        List<Project> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Project mapRow(ResultSet rs) throws SQLException {
        return new Project(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getDouble("budget"),
                ProjectStatus.fromDbValue(rs.getString("status"))
        );
    }
}
