package com.eems.repository;

import com.eems.model.Department;
import com.eems.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentRepository implements Repository<Department> {

    @Override
    public Department create(Department dept) throws SQLException {
        String sql = "INSERT INTO Department (name, location, annual_budget) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getLocation());
            ps.setDouble(3, dept.getAnnualBudget());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) dept.setId(keys.getInt(1));
            }
        }
        return dept;
    }

    @Override
    public Department findById(int id) throws SQLException {
        String sql = "SELECT * FROM Department WHERE id = ?";
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
    public List<Department> findAll() throws SQLException {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM Department";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void update(Department dept) throws SQLException {
        String sql = "UPDATE Department SET name=?, location=?, annual_budget=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getLocation());
            ps.setDouble(3, dept.getAnnualBudget());
            ps.setInt(4, dept.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Department WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void assignToProject(int departmentId, int projectId) throws SQLException {
        String sql = "INSERT INTO Department_Project (department_id, project_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            ps.setInt(2, projectId);
            ps.executeUpdate();
        }
    }

    public void removeFromProject(int departmentId, int projectId) throws SQLException {
        String sql = "DELETE FROM Department_Project WHERE department_id=? AND project_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            ps.setInt(2, projectId);
            ps.executeUpdate();
        }
    }

    // Returns all employees belonging to this department, each with its Department reference populated.
    public List<com.eems.model.Employee> findEmployeesByDepartment(int departmentId) throws SQLException {
        String sql = "SELECT e.id, e.full_name, e.title, e.hire_date, e.salary, " +
                     "       d.id AS dept_id, d.name AS dept_name, " +
                     "       d.location AS dept_location, d.annual_budget AS dept_budget " +
                     "FROM Employee e JOIN Department d ON e.department_id = d.id " +
                     "WHERE e.department_id = ?";
        List<com.eems.model.Employee> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapEmployeeRow(rs));
            }
        }
        return list;
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        return new Department(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("location"),
                rs.getDouble("annual_budget")
        );
    }

    private com.eems.model.Employee mapEmployeeRow(ResultSet rs) throws SQLException {
        Department dept = new Department(
                rs.getInt("dept_id"),
                rs.getString("dept_name"),
                rs.getString("dept_location"),
                rs.getDouble("dept_budget")
        );
        return new com.eems.model.Employee(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("title"),
                rs.getDate("hire_date").toLocalDate(),
                rs.getDouble("salary"),
                dept
        );
    }
}
