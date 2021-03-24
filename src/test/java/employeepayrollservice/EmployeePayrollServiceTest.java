package employeepayrollservice;

import com.employeepayrollservice.EmployeePayroll;
import com.employeepayrollservice.EmployeePayrollService;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.employeepayrollservice.EmployeePayrollService.IOService.DB_IO;
import static com.employeepayrollservice.EmployeePayrollService.IOService.FILE_IO;

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
        Assert.assertEquals(3, employeePayrollData.size());
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
}

