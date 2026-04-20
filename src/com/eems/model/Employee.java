package com.eems.model;

import java.time.LocalDate;

public class Employee extends BaseEntity {

    private String fullName;
    private String title;
    private LocalDate hireDate;
    private double salary;
    private Department department;

    public Employee() {
    }

    public Employee(String fullName, String title, LocalDate hireDate,
                    double salary, Department department) {
        this.fullName = fullName;
        this.title = title;
        this.hireDate = hireDate;
        this.salary = salary;
        this.department = department;
    }

    public Employee(int id, String fullName, String title, LocalDate hireDate,
                    double salary, Department department) {
        this.id = id;
        this.fullName = fullName;
        this.title = title;
        this.hireDate = hireDate;
        this.salary = salary;
        this.department = department;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        String deptName = department != null ? department.getName() : "N/A";
        return String.format("Employee{id=%d, fullName='%s', title='%s', hireDate=%s, salary=%.2f, department='%s'}",
                id, fullName, title, hireDate, salary, deptName);
    }
}
