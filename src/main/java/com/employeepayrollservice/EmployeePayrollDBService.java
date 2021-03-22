package com.employeepayrollservice;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {
    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    EmployeePayrollDBService() {}

    public static EmployeePayrollDBService getInstance() {
        if (employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSl=false";
        String userBane = "root";
        String password = "robowars@1amit";
        Connection connection;
        System.out.println("Connecting to database: " + jdbcURL);
        connection = DriverManager.getConnection(jdbcURL, userBane, password);
        System.out.println("Connection is succesful!!" + connection);
        return connection;
    }

    public List<EmployeePayroll> readData() {
        String sql = "select * from employee_payroll; ";
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    public int updateEmployeeData(String name, double salary) {
        return this.updateEmployeedataUsingStatement(name, salary);
    }

    private int updateEmployeedataUsingStatement(String name, double salary) {
        String sql = String.format("update employee_payroll set salary = %.2f where name = '%s';", salary, name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<EmployeePayroll> getEmployeePayrollData(String name) {
        List<EmployeePayroll> employeePayrollList = null;
        if (this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
        try {
            employeePayrollDataStatement.setString(1,name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayroll> getEmployeePayrollData(ResultSet resultSet) {
        List<EmployeePayroll> employeePayrollList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("Salary");
                LocalDate start = resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayroll(id, name, salary, start));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollList;
    }

    private void prepareStatementForEmployeeData() {
        try {
            Connection connection = this.getConnection();
            String sql = "select * from employee_payroll where name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<EmployeePayroll> getEmployeePayrollforDateRange(LocalDate startDate, LocalDate endDate) {
        String sql = String.format("Select * from employee_payroll where start between '%s' and '%s';",
                                    Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    private List<EmployeePayroll> getEmployeePayrollDataUsingDB(String sql) {
        List<EmployeePayroll> employeePayrollList = new ArrayList<>();
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollList;
    }

    public Map<String, Double> getAverageSalaryByGender() {
        String sql = "select gender, avg(salary) as avg_salary from employee_payroll group by gender;";
        Map<String,Double> genderToAverageSalaryMap = new HashMap<>();
        try(Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("avg_salary");
                genderToAverageSalaryMap.put(gender, salary);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return genderToAverageSalaryMap;
    }

    public EmployeePayroll addEmployeeToPayrollUC7(String name, double salary, LocalDate startDate, String gender) {
        int emploeeId = -1;
        EmployeePayroll employeePayrollData = null;
        String sql = String.format("insert into employee_payroll (name, gender, salary, start)" +
                                    "values ('%s', '%s', '%s', '%s')", name,gender,salary,Date.valueOf(startDate));
        try(Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            int rowAffected = statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) emploeeId = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollData;
    }

    public EmployeePayroll addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender) {
        int employeeId = -1;
        Connection connection = null;
        EmployeePayroll employeePayrollData = null;
        try {
            connection = this.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try(Statement statement = connection.createStatement()) {
            String sql = String.format("insert into employee_payroll (name, gender, salary, start)" +
                                        "values ('%s', '%s', '%s', '%s')", name,gender,salary,Date.valueOf(startDate));
            int rowAffected = statement.executeUpdate(sql,statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) employeeId = resultSet.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try(Statement statement = connection.createStatement()) {
            double deductions = salary * 0.2;
            double taxablePay = salary - deductions;
            double tax = taxablePay * 0.1;
            double net_pay = salary - tax;
            String sql = String.format("insert into payroll_details " +
                                        "(employee_id,basic_pay,deductions,taxable_pay,tax,net_pay) values " +
                                        "( '%s','%s','%s','%s','%s','%s')",employeeId,salary,deductions,taxablePay,tax,net_pay);
            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1)
                employeePayrollData = new EmployeePayroll(employeeId,name,salary,startDate);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return employeePayrollData;
    }
}
