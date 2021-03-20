package com.employeepayrollservice;

import java.time.LocalDate;
import java.util.Date;

public class EmployeePayroll {
    protected int id;
    protected String name;
    protected double salary;
    protected LocalDate start;

    public EmployeePayroll(int id, String name, Double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public EmployeePayroll(int id, String name, double salary, LocalDate start) {
        this(id, name, salary);
        this.start = start;
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
