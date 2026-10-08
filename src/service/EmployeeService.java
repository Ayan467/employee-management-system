package service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import dao.EmployeeDAO;
import exception.DuplicateEmailException;
import exception.EmployeeNotFoundException;
import exception.InvalidInputException;
import model.Employee;

public class EmployeeService {

    private static final int MYSQL_DUPLICATE_ENTRY = 1062;
    private static final BigDecimal MAX_SALARY = new BigDecimal("99999999.99");

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[A-Za-z][A-Za-z .'-]{1,99}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{10}$");

    private final EmployeeDAO employeeDAO;

    // The DAO is handed in from outside (constructor injection, done by hand)
    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    // ---------- CREATE ----------
    public Employee addEmployee(String name, String email, String phone, String department,
                                String designation, String salaryText, String dateText)
            throws InvalidInputException, DuplicateEmailException, SQLException {

        Employee emp = buildValidatedEmployee(name, email, phone, department,
                                              designation, salaryText, dateText);

        if (employeeDAO.getEmployeeByEmail(emp.getEmail()) != null) {
            throw new DuplicateEmailException(emp.getEmail());
        }

        try {
            employeeDAO.addEmployee(emp);
        } catch (SQLException e) {
            if (e.getErrorCode() == MYSQL_DUPLICATE_ENTRY) {   // safety net from the DB
                throw new DuplicateEmailException(emp.getEmail());
            }
            throw e;
        }
        return emp;
    }

    // ---------- READ ----------
    public List<Employee> getAllEmployees() throws SQLException {
        return employeeDAO.getAllEmployees();
    }

    public Employee getEmployeeById(int id)
            throws InvalidInputException, EmployeeNotFoundException, SQLException {
        validateId(id);
        Employee emp = employeeDAO.getEmployeeById(id);
        if (emp == null) {
            throw new EmployeeNotFoundException(id);
        }
        return emp;
    }

    public List<Employee> searchByName(String name) throws InvalidInputException, SQLException {
        return employeeDAO.searchByName(requireNonBlank(name, "Search name"));
    }

    public List<Employee> getByDepartment(String department)
            throws InvalidInputException, SQLException {
        return employeeDAO.getByDepartment(requireNonBlank(department, "Department"));
    }

    // ---------- UPDATE ----------
    public Employee updateEmployee(int id, String name, String email, String phone,
                                   String department, String designation,
                                   String salaryText, String dateText)
            throws InvalidInputException, EmployeeNotFoundException,
                   DuplicateEmailException, SQLException {

        getEmployeeById(id);   // throws if the id is invalid or does not exist

        Employee emp = buildValidatedEmployee(name, email, phone, department,
                                              designation, salaryText, dateText);
        emp.setId(id);

        Employee other = employeeDAO.getEmployeeByEmail(emp.getEmail());
        if (other != null && other.getId() != id) {
            throw new DuplicateEmailException(emp.getEmail());
        }

        try {
            if (!employeeDAO.updateEmployee(emp)) {
                throw new EmployeeNotFoundException(id);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == MYSQL_DUPLICATE_ENTRY) {
                throw new DuplicateEmailException(emp.getEmail());
            }
            throw e;
        }
        return emp;
    }

    // ---------- DELETE ----------
    public void deleteEmployee(int id)
            throws InvalidInputException, EmployeeNotFoundException, SQLException {
        validateId(id);
        if (!employeeDAO.deleteEmployee(id)) {
            throw new EmployeeNotFoundException(id);
        }
    }

    // ---------- SALARY CALCULATIONS ----------
    public BigDecimal calculateAnnualSalary(Employee emp) {
        return emp.getSalary().multiply(BigDecimal.valueOf(12));
    }

    public BigDecimal calculateTotalPayroll(List<Employee> employees) {
        BigDecimal total = BigDecimal.ZERO;
        for (Employee e : employees) {
            total = total.add(e.getSalary());
        }
        return total;
    }

    public BigDecimal calculateAverageSalary(List<Employee> employees) {
        if (employees.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return calculateTotalPayroll(employees)
                .divide(BigDecimal.valueOf(employees.size()), 2, RoundingMode.HALF_UP);
    }

    public Employee getHighestPaid(List<Employee> employees) {
        Employee top = null;
        for (Employee e : employees) {
            if (top == null || e.getSalary().compareTo(top.getSalary()) > 0) {
                top = e;
            }
        }
        return top;
    }

    public Map<String, BigDecimal> getDepartmentWiseTotals(List<Employee> employees) {
        Map<String, BigDecimal> totals = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Employee e : employees) {
            BigDecimal current = totals.getOrDefault(e.getDepartment(), BigDecimal.ZERO);
            totals.put(e.getDepartment(), current.add(e.getSalary()));
        }
        return totals;
    }

    // ---------- VALIDATION HELPERS ----------
    private void validateId(int id) throws InvalidInputException {
        if (id <= 0) {
            throw new InvalidInputException("Employee ID must be a positive number.");
        }
    }

    private String requireNonBlank(String value, String fieldName) throws InvalidInputException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    private Employee buildValidatedEmployee(String name, String email, String phone,
                                            String department, String designation,
                                            String salaryText, String dateText)
            throws InvalidInputException {

        name = requireNonBlank(name, "Name");
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new InvalidInputException(
                "Name must be 2-100 characters: letters, spaces, dots, apostrophes or hyphens only.");
        }

        email = requireNonBlank(email, "Email").toLowerCase();
        if (email.length() > 100 || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidInputException(
                "Please enter a valid email address (example: name@company.com).");
        }

        phone = requireNonBlank(phone, "Phone");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidInputException("Phone must be exactly 10 digits.");
        }

        department = requireNonBlank(department, "Department");
        designation = requireNonBlank(designation, "Designation");
        if (department.length() > 50 || designation.length() > 50) {
            throw new InvalidInputException(
                "Department and designation must be at most 50 characters.");
        }

        salaryText = requireNonBlank(salaryText, "Salary");
        BigDecimal salary;
        try {
            salary = new BigDecimal(salaryText);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(
                "Salary must be a valid number (example: 45000 or 45000.50).");
        }
        if (salary.signum() <= 0) {
            throw new InvalidInputException("Salary must be greater than zero.");
        }
        salary = salary.setScale(2, RoundingMode.HALF_UP);
        if (salary.compareTo(MAX_SALARY) > 0) {
            throw new InvalidInputException("Salary is too large (maximum 99999999.99).");
        }

        dateText = requireNonBlank(dateText, "Joining date");
        LocalDate joiningDate;
        try {
            joiningDate = LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException(
                "Joining date must be in yyyy-MM-dd format (example: 2024-06-15).");
        }
        if (joiningDate.isAfter(LocalDate.now())) {
            throw new InvalidInputException("Joining date cannot be in the future.");
        }

        return new Employee(name, email, phone, department, designation, salary, joiningDate);
    }
}
