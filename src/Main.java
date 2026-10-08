import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import dao.EmployeeDAOImpl;
import exception.DuplicateEmailException;
import exception.EmployeeNotFoundException;
import exception.InvalidInputException;
import model.Employee;
import service.EmployeeService;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final EmployeeService service = new EmployeeService(new EmployeeDAOImpl());

    private static final String ROW_FORMAT = "%-4s %-18s %-26s %-10s %-12s %-18s %11s %-10s%n";
    private static final String LINE = String.format("%116s", "").replace(' ', '-');

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1: addEmployee(); break;
                    case 2: viewAllEmployees(); break;
                    case 3: searchEmployee(); break;
                    case 4: updateEmployee(); break;
                    case 5: deleteEmployee(); break;
                    case 6: searchByDepartment(); break;
                    case 7: showSalaryReport(); break;
                    case 8:
                        running = false;
                        System.out.println("\nThank you for using Employee Management System. Goodbye!");
                        break;
                    default:
                        System.out.println("\nInvalid choice. Please enter a number from 1 to 8.");
                }
            } catch (InvalidInputException | EmployeeNotFoundException | DuplicateEmailException e) {
                System.out.println("\n[ERROR] " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("\n[DATABASE ERROR] " + e.getMessage());
            }
        }
        scanner.close();
    }

    // ---------- MENU ----------
    private static void printMenu() {
        System.out.println();
        System.out.println("===== EMPLOYEE MANAGEMENT SYSTEM =====");
        System.out.println("1. Add Employee");
        System.out.println("2. View All Employees");
        System.out.println("3. Search Employee");
        System.out.println("4. Update Employee");
        System.out.println("5. Delete Employee");
        System.out.println("6. Search by Department");
        System.out.println("7. Salary Report");
        System.out.println("8. Exit");
        System.out.println("======================================");
    }

    // ---------- FEATURES ----------
    private static void addEmployee()
            throws InvalidInputException, DuplicateEmailException, SQLException {
        System.out.println("\n--- Add New Employee ---");
        String name        = readLine("Name                       : ");
        String email       = readLine("Email                      : ");
        String phone       = readLine("Phone (10 digits)          : ");
        String department  = readLine("Department                 : ");
        String designation = readLine("Designation                : ");
        String salary      = readLine("Monthly Salary             : ");
        String joiningDate = readLine("Joining Date (yyyy-MM-dd)  : ");

        Employee emp = service.addEmployee(name, email, phone, department,
                                           designation, salary, joiningDate);
        System.out.println("\n[SUCCESS] Employee added with ID: " + emp.getId());
    }

    private static void viewAllEmployees() throws SQLException {
        System.out.println("\n--- All Employees ---");
        printEmployeeTable(service.getAllEmployees());
    }

    private static void searchEmployee()
            throws InvalidInputException, EmployeeNotFoundException, SQLException {
        System.out.println("\n--- Search Employee ---");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        int option = readInt("Choose an option: ");

        if (option == 1) {
            int id = readInt("Enter Employee ID: ");
            printEmployeeDetails(service.getEmployeeById(id));
        } else if (option == 2) {
            String name = readLine("Enter name (or part of it): ");
            printEmployeeTable(service.searchByName(name));
        } else {
            System.out.println("\nInvalid option.");
        }
    }

    private static void updateEmployee()
            throws InvalidInputException, EmployeeNotFoundException,
                   DuplicateEmailException, SQLException {
        System.out.println("\n--- Update Employee ---");
        int id = readInt("Enter ID of the employee to update: ");
        Employee existing = service.getEmployeeById(id);

        printEmployeeDetails(existing);
        System.out.println("\n(Press Enter to keep the current value)");

        String name        = readOrKeep("Name", existing.getName());
        String email       = readOrKeep("Email", existing.getEmail());
        String phone       = readOrKeep("Phone", existing.getPhone());
        String department  = readOrKeep("Department", existing.getDepartment());
        String designation = readOrKeep("Designation", existing.getDesignation());
        String salary      = readOrKeep("Monthly Salary", existing.getSalary().toPlainString());
        String joiningDate = readOrKeep("Joining Date", existing.getJoiningDate().toString());

        service.updateEmployee(id, name, email, phone, department,
                               designation, salary, joiningDate);
        System.out.println("\n[SUCCESS] Employee " + id + " updated.");
    }

    private static void deleteEmployee()
            throws InvalidInputException, EmployeeNotFoundException, SQLException {
        System.out.println("\n--- Delete Employee ---");
        int id = readInt("Enter ID of the employee to delete: ");
        Employee emp = service.getEmployeeById(id);

        String answer = readLine("Delete " + emp.getName() + " (ID " + id + ")? (y/n): ");
        if (answer.equalsIgnoreCase("y")) {
            service.deleteEmployee(id);
            System.out.println("\n[SUCCESS] Employee deleted.");
        } else {
            System.out.println("\nDeletion cancelled.");
        }
    }

    private static void searchByDepartment() throws InvalidInputException, SQLException {
        System.out.println("\n--- Search by Department ---");
        String department = readLine("Enter department: ");
        printEmployeeTable(service.getByDepartment(department));
    }

    private static void showSalaryReport() throws SQLException {
        List<Employee> all = service.getAllEmployees();
        if (all.isEmpty()) {
            System.out.println("\nNo employees found.");
            return;
        }

        Employee top = service.getHighestPaid(all);
        System.out.println("\n--- Salary Report ---");
        System.out.println("Total employees          : " + all.size());
        System.out.println("Total monthly payroll    : " + service.calculateTotalPayroll(all));
        System.out.println("Average monthly salary   : " + service.calculateAverageSalary(all));
        System.out.println("Highest paid             : " + top.getName() + " (" + top.getSalary() + ")");

        System.out.println("\nDepartment-wise monthly payroll:");
        for (Map.Entry<String, BigDecimal> entry : service.getDepartmentWiseTotals(all).entrySet()) {
            System.out.printf("  %-15s : %s%n", entry.getKey(), entry.getValue());
        }
    }

    // ---------- DISPLAY HELPERS ----------
    private static void printEmployeeTable(List<Employee> employees) {
        if (employees.isEmpty()) {
            System.out.println("\nNo employees found.");
            return;
        }
        System.out.println(LINE);
        System.out.printf(ROW_FORMAT, "ID", "Name", "Email", "Phone",
                          "Department", "Designation", "Salary", "Joined");
        System.out.println(LINE);
        for (Employee e : employees) {
            System.out.printf(ROW_FORMAT,
                    e.getId(),
                    truncate(e.getName(), 18),
                    truncate(e.getEmail(), 26),
                    e.getPhone(),
                    truncate(e.getDepartment(), 12),
                    truncate(e.getDesignation(), 18),
                    e.getSalary().toPlainString(),
                    e.getJoiningDate());
        }
        System.out.println(LINE);
        System.out.println(employees.size() + " record(s) found.");
    }

    private static void printEmployeeDetails(Employee e) {
        System.out.println("\n--- Employee Details ---");
        System.out.printf("%-15s: %s%n", "ID", e.getId());
        System.out.printf("%-15s: %s%n", "Name", e.getName());
        System.out.printf("%-15s: %s%n", "Email", e.getEmail());
        System.out.printf("%-15s: %s%n", "Phone", e.getPhone());
        System.out.printf("%-15s: %s%n", "Department", e.getDepartment());
        System.out.printf("%-15s: %s%n", "Designation", e.getDesignation());
        System.out.printf("%-15s: %s%n", "Monthly Salary", e.getSalary().toPlainString());
        System.out.printf("%-15s: %s%n", "Annual Salary", service.calculateAnnualSalary(e).toPlainString());
        System.out.printf("%-15s: %s%n", "Joining Date", e.getJoiningDate());
    }

    private static String truncate(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 1) + "~";
    }

    // ---------- INPUT HELPERS ----------
    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            String line = readLine(prompt);
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static String readOrKeep(String label, String currentValue) {
        String input = readLine(label + " [" + currentValue + "]: ");
        return input.isEmpty() ? currentValue : input;
    }
}
