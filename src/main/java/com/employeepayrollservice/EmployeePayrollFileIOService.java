package com.employeepayrollservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollFileIOService {
    public static String PAYROLL_FILE_NAME = "payroll-file.txt";

    public void write(List<EmployeePayroll> employeePayrollList) {
        StringBuffer empBuffer = new StringBuffer();
        employeePayrollList.forEach(employee ->{
            String empDataString = employee.toString().concat("\n");
            empBuffer.append(empDataString);
        });
        try {
            System.out.println("In Write method");
            Files.write(Paths.get(PAYROLL_FILE_NAME),empBuffer.toString().getBytes());
            System.out.println("File Written");
        }catch (IOException x){
            x.printStackTrace();
        }
    }

    public void printData() {
        try {
            Files.lines(new File(PAYROLL_FILE_NAME).toPath())
                    .forEach(System.out::println);
        }catch (IOException x){
            x.printStackTrace();
        }
    }

    public List<EmployeePayroll> readData() {
        List<EmployeePayroll> employeePayrollList = new ArrayList<EmployeePayroll>();
        try {
            Files.lines(new File("payroll-file.txt").toPath())
                    .map(line -> line.trim())
                    .forEach(line -> System.out.println(line));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public long countEntries() {
        long entries = 0;
        try {
            entries = Files.lines(new File(PAYROLL_FILE_NAME).toPath())
                    .count();
        }catch (IOException x){
            x.printStackTrace();
        }
        return entries;
    }
}
