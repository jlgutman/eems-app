package com.eems.model;

public class EmployeeAllocation {
    private final Employee employee;
    private final double allocationPercentage;

    public EmployeeAllocation(Employee employee, double allocationPercentage) {
        this.employee = employee;
        this.allocationPercentage = allocationPercentage;
    }

    public Employee getEmployee() {
        return employee;
    }

    public double getAllocationPercentage() {
        return allocationPercentage;
    }

    @Override
    public String toString() {
        return String.format("EmployeeAllocation{employee='%s', allocation=%.1f%%}",
                employee.getFullName(), allocationPercentage);
    }
}
