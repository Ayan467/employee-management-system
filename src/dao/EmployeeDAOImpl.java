package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Employee;
import util.DBConnection;

public class EmployeeDAOImpl implements EmployeeDAO {

    private static final String SELECT_ALL =
            "SELECT id, name, email, phone, department, designation, salary, joining_date "
          + "FROM employees";

    // Converts the CURRENT row of a ResultSet into an Employee object
    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("department"),
                rs.getString("designation"),
                rs.getBigDecimal("salary"),
                rs.getDate("joining_date").toLocalDate());
    }

    // ---------- CREATE ----------
    @Override
    public void addEmployee(Employee emp) throws SQLException {
        String sql = "INSERT INTO employees "
                   + "(name, email, phone, department, designation, salary, joining_date) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPhone());
            ps.setString(4, emp.getDepartment());
            ps.setString(5, emp.getDesignation());
            ps.setBigDecimal(6, emp.getSalary());
            ps.setDate(7, Date.valueOf(emp.getJoiningDate()));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    emp.setId(keys.getInt(1));
                }
            }
        }
    }

    // ---------- READ ----------
    @Override
    public List<Employee> getAllEmployees() throws SQLException {
        String sql = SELECT_ALL + " ORDER BY id";
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapRow(rs));
            }
        }
        return employees;
    }

    @Override
    public Employee getEmployeeById(int id) throws SQLException {
        String sql = SELECT_ALL + " WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;   // nobody with this id
    }

    @Override
    public Employee getEmployeeByEmail(String email) throws SQLException {
        String sql = SELECT_ALL + " WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Employee> searchByName(String name) throws SQLException {
        String sql = SELECT_ALL + " WHERE name LIKE ? ORDER BY name";
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapRow(rs));
                }
            }
        }
        return employees;
    }

    @Override
    public List<Employee> getByDepartment(String department) throws SQLException {
        String sql = SELECT_ALL + " WHERE department = ? ORDER BY name";
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, department);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapRow(rs));
                }
            }
        }
        return employees;
    }

    // ---------- UPDATE ----------
    @Override
    public boolean updateEmployee(Employee emp) throws SQLException {
        String sql = "UPDATE employees SET name = ?, email = ?, phone = ?, department = ?, "
                   + "designation = ?, salary = ?, joining_date = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPhone());
            ps.setString(4, emp.getDepartment());
            ps.setString(5, emp.getDesignation());
            ps.setBigDecimal(6, emp.getSalary());
            ps.setDate(7, Date.valueOf(emp.getJoiningDate()));
            ps.setInt(8, emp.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // ---------- DELETE ----------
    @Override
    public boolean deleteEmployee(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
