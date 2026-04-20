package com.eems.service;

import com.eems.model.Department;
import com.eems.model.Employee;
import com.eems.model.Project;
import com.eems.repository.DepartmentRepository;
import com.eems.repository.ProjectRepository;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DepartmentService {

    private static final Set<String> VALID_SORT_COLUMNS = new HashSet<>(
            Arrays.asList("budget", "end_date", "start_date", "name", "status"));

    private final DepartmentRepository departmentRepo;
    private final ProjectRepository projectRepo;

    public DepartmentService() {
        this.departmentRepo = new DepartmentRepository();
        this.projectRepo = new ProjectRepository();
    }

    public Department create(Department dept) throws SQLException {
        if (dept.getName() == null || dept.getName().isBlank())
            throw new IllegalArgumentException("Department name cannot be empty.");
        if (dept.getAnnualBudget() < 0)
            throw new IllegalArgumentException("Annual budget cannot be negative.");
        return departmentRepo.create(dept);
    }

    public Department findById(int id) throws SQLException {
        return departmentRepo.findById(id);
    }

    public Department findByIdWithEmployees(int id) throws SQLException {
        Department dept = departmentRepo.findById(id);
        if (dept == null) return null;
        List<Employee> employees = departmentRepo.findEmployeesByDepartment(id);
        dept.setEmployees(employees);
        return dept;
    }

    public List<Department> findAll() throws SQLException {
        return departmentRepo.findAll();
    }

    public void update(Department dept) throws SQLException {
        if (departmentRepo.findById(dept.getId()) == null)
            throw new IllegalArgumentException("Department not found: " + dept.getId());
        departmentRepo.update(dept);
    }

    public void delete(int id) throws SQLException {
        departmentRepo.delete(id);
    }

    public void assignToProject(int departmentId, int projectId) throws SQLException {
        departmentRepo.assignToProject(departmentId, projectId);
    }

    public void removeFromProject(int departmentId, int projectId) throws SQLException {
        departmentRepo.removeFromProject(departmentId, projectId);
    }

    // Task 2: Active projects for a department, ordered by sortBy field.
    public List<Project> getProjectsByDepartment(int departmentId, String sortBy) throws SQLException {
        if (!VALID_SORT_COLUMNS.contains(sortBy))
            throw new IllegalArgumentException(
                    "Invalid sort field: '" + sortBy + "'. Allowed: " + VALID_SORT_COLUMNS);
        if (departmentRepo.findById(departmentId) == null)
            throw new IllegalArgumentException("Department not found: " + departmentId);
        return projectRepo.findActiveByDepartment(departmentId, sortBy);
    }
}
