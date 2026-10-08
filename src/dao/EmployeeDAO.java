package dao;

import java.sql.SQLException;
import java.util.List;

import model.Employee;

public interface EmployeeDAO {

    void addEmployee(Employee employee) throws SQLException;

    List<Employee> getAllEmployees() throws SQLException;

    Employee getEmployeeById(int id) throws SQLException;       // null if not found

    Employee getEmployeeByEmail(String email) throws SQLException; // null if not found

    List<Employee> searchByName(String name) throws SQLException;

    List<Employee> getByDepartment(String department) throws SQLException;

    boolean updateEmployee(Employee employee) throws SQLException; // true if a row was updated

    boolean deleteEmployee(int id) throws SQLException;            // true if a row was deleted
}
