package com.employeepayrollservice;

import java.time.LocalDate;
import java.util.*;

public class EmployeePayrollService {


    public void addEmployeesToPayroll(List<EmployeePayroll> employeePayrollDataList) {
        employeePayrollDataList.forEach(employeePayrollData -> {
            System.out.println("Employee Being Added: " + employeePayrollData.name);
            this.addEmployeeToPayroll(employeePayrollData.name,employeePayrollData.salary,employeePayrollData.start,employeePayrollData.gender);
            System.out.println("Employee Added: " + employeePayrollData.name);
        });
        System.out.println(this.employeePayrollList);
    }

    public void addEmployeeToPayrollWithThreads(List<EmployeePayroll> employeePayrollDataList) {
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<>();
        employeePayrollDataList.forEach(employeePayrollData -> {
            // The task to be performed
            Runnable task = () -> {
            employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
            System.out.println("Employee Being Added: " + Thread.currentThread().getName());
            this.addEmployeeToPayroll(employeePayrollData.name,employeePayrollData.salary,employeePayrollData.start,employeePayrollData.gender);
            employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
             System.out.println("Employee Added: " + Thread.currentThread().getName());
            };
            //Threads implemented here
            Thread thread = new Thread(task, employeePayrollData.name);
            thread.start();
        });
        while (employeeAdditionStatus.containsValue(false)); {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(employeePayrollDataList);
    }

    public enum IOService {CONSOLE_IO, FILE_IO, DB_IO, REST_IO}
    public List<EmployeePayroll> employeePayrollList;
    private static EmployeePayrollDBService employeePayrollDBService;
    public List<EmployeePayroll> readEmployeePayrollforDateRange(IOService ioService, LocalDate startDate, LocalDate endDate) {
        if (ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeePayrollforDateRange(startDate,endDate);
        return null;
    }

    public Map<String, Double> readAverageSalaryByGender(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getAverageSalaryByGender();
        return null;
    }

    public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender, String[] department) {
        employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name,salary,startDate,gender,department));
    }
    public void addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
        employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name,salary,startDate,gender));
    }


    public EmployeePayrollService() {
        employeePayrollDBService = new EmployeePayrollDBService().getInstance();
    }

    public List<EmployeePayroll> readEmployeePayrollData(IOService ioService) {
        if (ioService.equals(IOService.DB_IO))
            this.employeePayrollList = employeePayrollDBService.readData();
        return this.employeePayrollList;
    }

    public boolean checkEmployeePayrollSyncWithDB(String name) {
        List<EmployeePayroll> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }

    public void updateEmployeeSalary(String name, double salary) {
        int result = employeePayrollDBService.updateEmployeeData(name,salary);
        if (result == 0) return;
        EmployeePayroll employeePayrollData = this.getEmployeePayrollData(name);
        if (employeePayrollData != null) employeePayrollData.salary = salary;
    }
    public void deleteEmployee(String name) {
        int result = employeePayrollDBService.deleteEmployeeData(name);
        if (result == 0) return;
    }

    private EmployeePayroll getEmployeePayrollData(String name) {
        return this.employeePayrollList.stream()
                .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
                .findFirst()
                .orElse(null);
    }


    public EmployeePayrollService(List<EmployeePayroll> employeePayrollList) {
        this();
        this.employeePayrollList = employeePayrollList;
    }

    public void writeEmployeePayroll(IOService ioService) {
        if(ioService.equals(IOService.CONSOLE_IO))
            employeePayrollList.stream().forEach(System.out::println);
        else if (ioService.equals(IOService.FILE_IO))
        System.out.println("Write called");
        new EmployeePayrollFileIOService().write(employeePayrollList);

    }

    public void readEmployeePayroll(Scanner consoleINputReader) {
        System.out.println("Enter Employee ID: ");
        int id = consoleINputReader.nextInt();
        System.out.println("Enter Employee Name: ");
        String name = consoleINputReader.next();
        System.out.println("Enter Employee Salary: ");
        double salary = consoleINputReader.nextDouble();
        employeePayrollList.add(new EmployeePayroll(id,name,salary));
        System.out.println("Succesfully added in employeePayrollList");
    }

    public long readEmployeePayroll(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            this.employeePayrollList = new EmployeePayrollFileIOService().readData();
        return employeePayrollList.size();
    }

    public void printData(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().printData();
    }

    public long countEntries(IOService ioService) {
        if (ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return employeePayrollList.size();
    }
    public static void main(String[] args) {
        System.out.println("Welcome to the Employee Payroll Service program");
        ArrayList<EmployeePayroll> employeePayrollList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        EmployeePayrollFileIOService employeePayrollFileIOService = new EmployeePayrollFileIOService();
        Scanner consoleINputReader = new Scanner(System.in);
        employeePayrollService.readEmployeePayroll(consoleINputReader);
        employeePayrollService.writeEmployeePayroll(IOService.FILE_IO);
        employeePayrollFileIOService.printData();
    }
}
