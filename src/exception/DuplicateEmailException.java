package exception;

public class DuplicateEmailException extends Exception {
    public DuplicateEmailException(String email) {
        super("An employee with email '" + email + "' already exists.");
    }
}
