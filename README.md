# Employee Management System

A console-based Employee Management System built with **Core Java, JDBC and MySQL**
(no frameworks). It supports full CRUD, search, input validation, custom exception
handling and salary reporting, organized in a clean layered architecture.

## Features
- Add, view, update and delete employees
- Search by ID, by name (partial match) and by department
- Salary report: annual salary, total payroll, average, highest paid, department-wise totals
- Input validation (name, email, 10-digit phone, salary, joining date)
- Duplicate email protection (service check + MySQL UNIQUE constraint)
- Custom exceptions and friendly error messages
- Secure SQL with `PreparedStatement` (protects against SQL injection)
- Database credentials via environment variables (nothing hardcoded)

## Tech Stack
Java 11+, JDBC, MySQL 8, Maven (dependency management), Git/GitHub

## Architecture
```
Main (console UI) -> EmployeeService (validation + business logic)
                  -> EmployeeDAO / EmployeeDAOImpl (JDBC + SQL)
                  -> DBConnection (connection from env variables) -> MySQL
```
- `model`: `Employee` (encapsulated data class)
- `dao`: `EmployeeDAO` interface + JDBC implementation
- `service`: `EmployeeService`
- `util`: `DBConnection`
- `exception`: `EmployeeNotFoundException`, `DuplicateEmailException`, `InvalidInputException`

## Project Structure
```
src/
├── Main.java
├── model/Employee.java
├── dao/EmployeeDAO.java, EmployeeDAOImpl.java
├── service/EmployeeService.java
├── util/DBConnection.java
└── exception/...
database/schema.sql, sample_data.sql
pom.xml
```

## Setup
1. **Prerequisites:** JDK 11+, MySQL 8+, Maven
2. **Create the database:**
   ```bash
   mysql -u root -p < database/schema.sql
   mysql -u root -p < database/sample_data.sql   # optional
   ```
3. **Set environment variables:**

   | Variable | Required | Default |
   |---|---|---|
   | `DB_PASSWORD` | Yes | none |
   | `DB_USER` | No | `root` |
   | `DB_URL` | No | `jdbc:mysql://localhost:3306/employee_management?serverTimezone=UTC` |

   Linux/macOS: `export DB_PASSWORD="your_password"`
   Windows (PowerShell): `setx DB_PASSWORD "your_password"` (then reopen the terminal)
4. **Run:**
   ```bash
   mvn compile org.codehaus.mojo:exec-maven-plugin:3.1.0:java -Dexec.mainClass=Main
   ```
   Or open the project in IntelliJ/Eclipse and run `Main`.

## Menu
```
===== EMPLOYEE MANAGEMENT SYSTEM =====
1. Add Employee
2. View All Employees
3. Search Employee
4. Update Employee
5. Delete Employee
6. Search by Department
7. Salary Report
8. Exit
======================================
```

## Validation Rules
| Field | Rule |
|---|---|
| Name | 2-100 chars; letters, spaces, `.`, `'`, `-` |
| Email | Valid format, unique, stored in lowercase |
| Phone | Exactly 10 digits |
| Salary | Greater than 0, up to 2 decimal places |
| Joining date | `yyyy-MM-dd`, not in the future |

## Future Improvements
- Connection pooling (HikariCP)
- JUnit tests with a mock DAO
- Pagination and sorting
- Logging framework
- Migration to Spring Boot + Spring Data JPA

## Author
Your Name | [GitHub](https://github.com/your-username) | [LinkedIn](https://linkedin.com/in/your-profile)
