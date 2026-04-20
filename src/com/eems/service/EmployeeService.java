package com.eems.service;

import com.eems.model.Department;
import com.eems.model.Employee;
import com.eems.repository.DepartmentRepository;
import com.eems.repository.EmployeeRepository;
import com.eems.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final DepartmentRepository departmentRepo;

    public EmployeeService() {
        this.employeeRepo = new EmployeeRepository();
        this.departmentRepo = new DepartmentRepository();
    }

    public Employee create(Employee emp) throws SQLException {
        if (emp.getFullName() == null || emp.getFullName().isBlank())
            throw new IllegalArgumentException("Employee name cannot be empty.");
        if (emp.getSalary() < 0)
            throw new IllegalArgumentException("Salary cannot be negative.");
        if (emp.getDepartment() == null)
            throw new IllegalArgumentException("Employee must be assigned to a department.");
        if (departmentRepo.findById(emp.getDepartment().getId()) == null)
            throw new IllegalArgumentException("Department not found: " + emp.getDepartment().getId());
        return employeeRepo.create(emp);
    }

    public Employee findById(int id) throws SQLException {
        return employeeRepo.findById(id);
    }

    public List<Employee> findAll() throws SQLException {
        return employeeRepo.findAll();
    }

    public void update(Employee emp) throws SQLException {
        if (employeeRepo.findById(emp.getId()) == null)
            throw new IllegalArgumentException("Employee not found: " + emp.getId());
        if (emp.getDepartment() == null)
            throw new IllegalArgumentException("Employee must be assigned to a department.");
        employeeRepo.update(emp);
    }

    public void delete(int id) throws SQLException {
        employeeRepo.delete(id);
    }

    public void assignToProject(int employeeId, int projectId, double allocationPct) throws SQLException {
        employeeRepo.assignToProject(employeeId, projectId, allocationPct);
    }

    public void removeFromProject(int employeeId, int projectId) throws SQLException {
        employeeRepo.removeFromProject(employeeId, projectId);
    }

    // Task 4: Move an employee to a new department atomically.
    public void transferEmployeeToDepartment(int employeeId, int newDepartmentId) throws SQLException {
        Employee emp = employeeRepo.findById(employeeId);
        if (emp == null)
            throw new IllegalArgumentException("Employee not found: " + employeeId);
        Department newDept = departmentRepo.findById(newDepartmentId);
        if (newDept == null)
            throw new IllegalArgumentException("Target department not found: " + newDepartmentId);
        if (emp.getDepartment().getId() == newDepartmentId)
            throw new IllegalArgumentException(
                    "Employee " + employeeId + " is already in department " + newDepartmentId);

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                employeeRepo.updateDepartment(conn, employeeId, newDepartmentId);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Transfer rolled back: " + e.getMessage(), e);
            }
        }
    }
}
