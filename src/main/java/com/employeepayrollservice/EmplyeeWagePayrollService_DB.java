package com.employeepayrollservice;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import static java.sql.Driver.*;

public class EmplyeeWagePayrollService_DB {
        public static void main(String[] args) {
            String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSl=false";
            String userBane = "root";
            String password = "robowars@1amit";
            Connection connection;
            try { // Loading the driver
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("Driver Loaded");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Cannot find the driver in the classpath!", e);
            }
            listDrivers();
            try { // Connecting to database, DriverManager is the interface
                System.out.println("Connecting to database: " + jdbcURL);
                connection = DriverManager.getConnection(jdbcURL, userBane, password);
                System.out.println("Connection is succesful!!" + connection);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    // Listing out all the drivers present in the class path.
    private static void listDrivers() {
        Enumeration<Driver> driverList = DriverManager.getDrivers();
        while (driverList.hasMoreElements()) {
            Driver driverClass = driverList.nextElement();
            System.out.println("  " + driverClass.getClass().getName());
        }
    }

}
