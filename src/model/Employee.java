package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Employee {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private BigDecimal salary;
    private LocalDate joiningDate;

    // 1) No-argument constructor
    public Employee() {
    }

    // 2) Constructor WITHOUT id (used when adding; MySQL generates the id)
    public Employee(String name, String email, String phone, String department,
                    String designation, BigDecimal salary, LocalDate joiningDate) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.designation = designation;
        this.salary = salary;
        this.joiningDate = joiningDate;
    }

    // 3) Constructor WITH id (used when reading from the database)
    public Employee(int id, String name, String email, String phone, String department,
                    String designation, BigDecimal salary, LocalDate joiningDate) {
        this(name, email, phone, department, designation, salary, joiningDate);
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name + "', email='" + email
                + "', department='" + department + "', salary=" + salary + "}";
    }
}
