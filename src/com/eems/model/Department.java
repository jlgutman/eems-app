package com.eems.model;

import java.util.ArrayList;
import java.util.List;

public class Department extends BaseEntity {

    private String name;
    private String location;
    private double annualBudget;
    private List<Employee> employees = new ArrayList<>();

    public Department() {
    }

    public Department(String name, String location, double annualBudget) {
        this.name = name;
        this.location = location;
        this.annualBudget = annualBudget;
    }

    public Department(int id, String name, String location, double annualBudget) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.annualBudget = annualBudget;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getAnnualBudget() {
        return annualBudget;
    }

    public void setAnnualBudget(double annualBudget) {
        this.annualBudget = annualBudget;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
    }

    @Override
    public String toString() {
        return String.format("Department{id=%d, name='%s', location='%s', annualBudget=%.2f, employees=%d}",
                id, name, location, annualBudget, employees.size());
    }
}
