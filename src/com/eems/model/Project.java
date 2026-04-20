package com.eems.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Project extends BaseEntity {

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private double budget;
    private ProjectStatus status;
    private List<EmployeeAllocation> employeeAllocations = new ArrayList<>();
    private List<Department> departments = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();

    public Project() {
    }

    public Project(String name, String description, LocalDate startDate, LocalDate endDate,
                   double budget, ProjectStatus status) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.status = status;
    }

    public Project(int id, String name, String description, LocalDate startDate, LocalDate endDate,
                   double budget, ProjectStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public List<EmployeeAllocation> getEmployeeAllocations() {
        return employeeAllocations;
    }

    public void setEmployeeAllocations(List<EmployeeAllocation> allocations) {
        this.employeeAllocations = allocations;
    }

    public void addEmployeeAllocation(EmployeeAllocation allocation) {
        this.employeeAllocations.add(allocation);
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public void addDepartment(Department department) {
        this.departments.add(department);
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public void addClient(Client client) {
        this.clients.add(client);
    }

    @Override
    public String toString() {
        return String.format("Project{id=%d, name='%s', status='%s', startDate=%s, endDate=%s, budget=%.2f}",
                id, name, status, startDate, endDate, budget);
    }
}
