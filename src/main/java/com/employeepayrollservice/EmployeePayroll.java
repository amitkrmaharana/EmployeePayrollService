package com.employeepayrollservice;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayroll {
    protected int id;
    protected String name;
    protected double salary;
    protected LocalDate start;
    protected String gender;
    protected String[] department;

    public EmployeePayroll(int id, String name, Double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public EmployeePayroll(int id, String name, double salary, LocalDate start) {
        this(id, name, salary);
        this.start = start;
    }

    public EmployeePayroll(int id, String name, String gender, double salary, LocalDate start) {
        this(id, name, salary, start);
        this.gender = gender;
    }

    public EmployeePayroll(int id, String name, double salary, LocalDate start, String gender, String[] department) {
        this(id, name, salary, start);
        this.department = department;

    }

    @Override
    public int hashCode() {
        return Objects.hash(name, salary, start, gender);
    }

    @Override
    public String toString() {
        return "EmployeePayroll{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", start=" + start +
                ", gender='" + gender + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayroll that = (EmployeePayroll) o;
        return id == that.id && Double.compare(that.salary, salary) == 0 && Objects.equals(name, that.name);
    }
}
