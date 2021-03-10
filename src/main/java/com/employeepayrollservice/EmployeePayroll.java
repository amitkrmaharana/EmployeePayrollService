package com.employeepayrollservice;

public class EmployeePayroll {
    protected int id;
    protected String name;
    protected double salary;

    public EmployeePayroll(int id, String name, Double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    @Override
    public String toString() {
        return "EmployeePayroll{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                '}';
    }
}
