package com.eems.repository;

import com.eems.model.Department;
import com.eems.model.Employee;
import com.eems.model.EmployeeAllocation;
import com.eems.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository implements Repository<Employee> {

    private static final String SELECT_BASE =
            "SELECT e.id, e.full_name, e.title, e.hire_date, e.salary, " +
                    "       d.id AS dept_id, d.name AS dept_name, " +
                    "       d.location AS dept_location, d.annual_budget AS dept_budget " +
                    "FROM Employee e JOIN Department d ON e.department_id = d.id";

    @Override
    public Employee create(Employee emp) throws SQLException {
        String sql = "INSERT INTO Employee (full_name, title, hire_date, salary, department_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, emp.getFullName());
            ps.setString(2, emp.getTitle());
            ps.setDate(3, Date.valueOf(emp.getHireDate()));
            ps.setDouble(4, emp.getSalary());
            ps.setInt(5, emp.getDepartment().getId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) emp.setId(keys.getInt(1));
            }
        }
        return emp;
    }

    @Override
    public Employee findById(int id) throws SQLException {
        String sql = SELECT_BASE + " WHERE e.id = ?";
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
    public List<Employee> findAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BASE);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public void update(Employee emp) throws SQLException {
        String sql = "UPDATE Employee SET full_name=?, title=?, hire_date=?, salary=?, department_id=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getFullName());
            ps.setString(2, emp.getTitle());
            ps.setDate(3, Date.valueOf(emp.getHireDate()));
            ps.setDouble(4, emp.getSalary());
            ps.setInt(5, emp.getDepartment().getId());
            ps.setInt(6, emp.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Employee WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void assignToProject(int employeeId, int projectId, double allocationPercentage) throws SQLException {
        String sql = "INSERT INTO Employee_Project (employee_id, project_id, allocation_percentage) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, projectId);
            ps.setDouble(3, allocationPercentage);
            ps.executeUpdate();
        }
    }

    public void removeFromProject(int employeeId, int projectId) throws SQLException {
        String sql = "DELETE FROM Employee_Project WHERE employee_id=? AND project_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, projectId);
            ps.executeUpdate();
        }
    }

    public List<EmployeeAllocation> findAllocationsByProject(int projectId) throws SQLException {
        String sql = "SELECT e.id, e.full_name, e.title, e.hire_date, e.salary, " +
                "       d.id AS dept_id, d.name AS dept_name, " +
                "       d.location AS dept_location, d.annual_budget AS dept_budget, " +
                "       ep.allocation_percentage " +
                "FROM Employee e " +
                "JOIN Department d ON e.department_id = d.id " +
                "JOIN Employee_Project ep ON e.id = ep.employee_id " +
                "WHERE ep.project_id = ?";
        List<EmployeeAllocation> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, projectId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new EmployeeAllocation(mapRow(rs), rs.getDouble("allocation_percentage")));
                }
            }
        }
        return result;
    }

    public void updateDepartment(Connection conn, int employeeId, int newDepartmentId) throws SQLException {
        String sql = "UPDATE Employee SET department_id=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newDepartmentId);
            ps.setInt(2, employeeId);
            ps.executeUpdate();
        }
    }

    public Employee mapRow(ResultSet rs) throws SQLException {
        Department dept = new Department(
                rs.getInt("dept_id"),
                rs.getString("dept_name"),
                rs.getString("dept_location"),
                rs.getDouble("dept_budget")
        );
        return new Employee(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("title"),
                rs.getDate("hire_date").toLocalDate(),
                rs.getDouble("salary"),
                dept
        );
    }
}
