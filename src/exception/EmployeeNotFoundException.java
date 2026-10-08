package exception;

public class EmployeeNotFoundException extends Exception {
    public EmployeeNotFoundException(int id) {
        super("No employee found with ID: " + id);
    }
}
