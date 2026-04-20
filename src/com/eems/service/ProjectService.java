package com.eems.service;

import com.eems.model.EmployeeAllocation;
import com.eems.model.Project;
import com.eems.model.ProjectStatus;
import com.eems.repository.EmployeeRepository;
import com.eems.repository.ProjectRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ProjectService {

    private final ProjectRepository projectRepo;
    private final EmployeeRepository employeeRepo;

    public ProjectService() {
        this.projectRepo = new ProjectRepository();
        this.employeeRepo = new EmployeeRepository();
    }

    public Project create(Project project) throws SQLException {
        if (project.getName() == null || project.getName().isBlank())
            throw new IllegalArgumentException("Project name cannot be empty.");
        if (project.getEndDate().isBefore(project.getStartDate()))
            throw new IllegalArgumentException("End date cannot be before start date.");
        return projectRepo.create(project);
    }

    public Project findById(int id) throws SQLException {
        return projectRepo.findById(id);
    }

    public List<Project> findAll() throws SQLException {
        return projectRepo.findAll();
    }

    public void update(Project project) throws SQLException {
        if (projectRepo.findById(project.getId()) == null)
            throw new IllegalArgumentException("Project not found: " + project.getId());
        projectRepo.update(project);
    }

    public void delete(int id) throws SQLException {
        projectRepo.delete(id);
    }

    // Task 1: Total projected HR cost for a project.
    // Cost = sum over employees of: (salary / 12) * durationMonths * (allocationPct / 100)
    // Duration is rounded up to the next full month.
    public double calculateProjectHRCost(int projectId) throws SQLException {
        Project project = projectRepo.findById(projectId);
        if (project == null)
            throw new IllegalArgumentException("Project not found: " + projectId);

        LocalDate start = project.getStartDate();
        LocalDate end = project.getEndDate();

        long fullMonths = ChronoUnit.MONTHS.between(start, end);
        long durationMonths = start.plusMonths(fullMonths).isBefore(end) ? fullMonths + 1 : fullMonths;
        if (durationMonths == 0) durationMonths = 1;

        final long months = durationMonths;

        return employeeRepo.findAllocationsByProject(projectId).stream()
                .mapToDouble(alloc -> (alloc.getEmployee().getSalary() / 12.0)
                        * months
                        * (alloc.getAllocationPercentage() / 100.0))
                .sum();
    }
}
