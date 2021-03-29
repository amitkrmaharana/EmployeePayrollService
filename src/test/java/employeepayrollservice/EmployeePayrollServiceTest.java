package employeepayrollservice;

import com.employeepayrollservice.EmployeePayroll;
import com.employeepayrollservice.EmployeePayrollService;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.awt.geom.GeneralPath;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.employeepayrollservice.EmployeePayrollService.IOService.*;

public class EmployeePayrollServiceTest {
    @Test
    public  void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
        EmployeePayroll[] arrayOfEmps = {
                new EmployeePayroll(1, "Athif",100000.0),
                new EmployeePayroll(2, "Ashish",150000.0),
                new EmployeePayroll(3, "Kevin",200000.0),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayroll(FILE_IO);
        employeePayrollService.printData(FILE_IO);
        long entries = employeePayrollService.countEntries(FILE_IO);
        Assert.assertEquals(3, entries);
    }
    @Test
    public void givenFileOnReadingFromFileShouldMatchEmployeeCount() {
        EmployeePayroll[] arrayOfEmps = {
                new EmployeePayroll(1, "Athif",100000.0),
                new EmployeePayroll(2, "Ashish",150000.0),
                new EmployeePayroll(3, "Kevin",200000.0),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayroll(FILE_IO);;
        long entries = employeePayrollService.countEntries(FILE_IO);
        Assert.assertEquals(3, entries);
    }
    // JDBC
    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayroll> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_IO);
        Assert.assertEquals(5,employeePayrollData.size());
    }
    @Test
    public void givenNewSalaryForEmployee_whenUpdated_shouldSync() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayroll> employeePayrollData = employeePayrollService.readEmployeePayrollData(DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa",3000000.00);
        boolean result = employeePayrollService.checkEmployeePayrollSyncWithDB("Terisa");
        Assert.assertTrue(result);
    }
    @Test
    public void givenDateRange_WhenRetrieved_shouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        LocalDate startDate = LocalDate.of(2018,01,01);
        LocalDate endDate = LocalDate.now();
        List<EmployeePayroll> employeePayrollData = employeePayrollService.readEmployeePayrollforDateRange(DB_IO, startDate, endDate);
        Assert.assertEquals(5, employeePayrollData.size());
    }
    @Test
    public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnPropoerValue() {
       EmployeePayrollService employeePayrollService = new EmployeePayrollService();
       employeePayrollService.readEmployeePayrollData(DB_IO);
        Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(DB_IO);
        Assert.assertTrue(averageSalaryByGender.get("M").equals(2000000.00) &&
                            averageSalaryByGender.get("F").equals(3000000.00));
    }
    @Test
    public void givenNewEmployee_WhenAdded_ShouldSyncWithAllTheTablesInDB() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        employeePayrollService.addEmployeeToPayroll("Angelina",4000000.00,LocalDate.now(),"F", new String[]{"Hr", "Marketing"});
        boolean result = employeePayrollService.checkEmployeePayrollSyncWithDB("Angelina");
        Assert.assertTrue(result);
    }

    @Test
    public void givenNewEmployeeDetails_WhenAdded_ShouldSyncWithAllTheTablesInDB() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        employeePayrollService.addEmployeeToPayroll("Tom",9000000.00,LocalDate.now(),"M", new String[]{"Production"});
        boolean result = employeePayrollService.checkEmployeePayrollSyncWithDB("Tom");
        Assert.assertTrue(result);
    }
    @Test
    public void givenEmployeeName_WhenDeleted_ShouldChange_ItsActivitityToFalse() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        employeePayrollService.deleteEmployee("Terisa");
        boolean result = employeePayrollService.checkEmployeePayrollSyncWithDB("Terisa");
        Assert.assertTrue(result);
    }
    @Test
    public void given6Employees_WhenAddedToDB_ShouldSyncWithDB() {
        EmployeePayroll[] arrayOfEmps = {
          new EmployeePayroll(0,"Jeff Bezos", "M", 100000.00, LocalDate.now() ),
          new EmployeePayroll(0,"Bill Gates", "M", 200000.00, LocalDate.now()),
          new EmployeePayroll(0,"Mark Zuckerberg", "M", 300000.00, LocalDate.now()),
          new EmployeePayroll(0,"Sunder", "M", 600000.00, LocalDate.now()),
          new EmployeePayroll(0,"Mukesh", "M", 1000000.00, LocalDate.now()),
          new EmployeePayroll(0,"Anil", "M", 200000.00, LocalDate.now())
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
        Instant end = Instant.now();
        System.out.println("Duration without thread: " + Duration.between(start,end));
        Instant threadStart = Instant.now();
        employeePayrollService.addEmployeeToPayrollWithThreads(Arrays.asList(arrayOfEmps));
        Instant threadEnd = Instant.now();
        System.out.println("Duration with Threads: " + Duration.between(threadStart,threadEnd));
        Assert.assertEquals(13,employeePayrollService.countEntries(DB_IO));
    }

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost/";
        RestAssured.port = 3000;
    }

    public EmployeePayroll[] getemployeeList() {
        Response response = RestAssured.get("/employees");
        System.out.println("Employee Payroll Entries in JsonServer:\n" + response);
        EmployeePayroll[] arrayOfEmp = new Gson().fromJson(response.asString(), EmployeePayroll[].class);
        return arrayOfEmp;
    }

    private Response addEmployeeToJsonServer(EmployeePayroll employeePayrollData) {
        String empJson = new Gson().toJson(employeePayrollData);
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type","application/json");
        request.body(empJson);
        return request.post("/employees");
    }

    @Test
    public void givenEmployeeDataInJsonServer_WhenRetrieved_ShouldMatchTheCount() {
        EmployeePayroll[] arrayOfEmps;
        arrayOfEmps = getemployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        long entries = employeePayrollService.countEntries(REST_IO);
        Assert.assertEquals(2,entries);
    }

    @Test
    public void givenNewEmployeeData_WhenRetrieved_ShouldMatch201ResponseAndTheCount() {
        EmployeePayroll[] arrayOfEmps;
        arrayOfEmps = getemployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        EmployeePayroll employeePayrollData = new EmployeePayroll(0,"Mark ZuckerBerg","M",300000,LocalDate.now());
        Response response = addEmployeeToJsonServer(employeePayrollData);
        int statusCode = response.getStatusCode();
        Assert.assertEquals(201,statusCode);

        employeePayrollData = new Gson().fromJson(response.asString(),EmployeePayroll.class);
        employeePayrollService.addEmployeeToPayroll(employeePayrollData,REST_IO);
        long entries = employeePayrollService.countEntries(REST_IO);
        Assert.assertEquals(3,entries);
    }

    @Test
    public void givenListOfnewEmployees_WhenAdded_ShouldMatch201ResponseAndCount() {
        EmployeePayroll[] arrayOfEmps;
        arrayOfEmps = getemployeeList();
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));

        EmployeePayroll[] arrayOfEmployeePayrolls = {
                new EmployeePayroll(0,"Sunder","M",600000,LocalDate.now()),
                new EmployeePayroll(0,"Mukesh","M",1000000,LocalDate.now()),
                new EmployeePayroll(0,"Anil","M",200000,LocalDate.now())
        };
        for (EmployeePayroll employeePayrollData : arrayOfEmployeePayrolls) {
            Response response = addEmployeeToJsonServer(employeePayrollData);
            Assert.assertEquals(201,response.getStatusCode());

            employeePayrollData = new Gson().fromJson(response.asString(),EmployeePayroll.class);
            employeePayrollService.addEmployeeToPayroll(employeePayrollData,REST_IO);
        }

        long entries = employeePayrollService.countEntries(REST_IO);
        Assert.assertEquals(6,entries);
    }

}

