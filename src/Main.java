import com.eems.model.*;
import com.eems.service.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {

    static final DepartmentService departmentService = new DepartmentService();
    static final EmployeeService employeeService = new EmployeeService();
    static final ProjectService projectService = new ProjectService();
    static final ClientService clientService = new ClientService();

    public static void main(String[] args) {
        try {
            testDepartmentCRUD();
            testEmployeeCRUD();
            testProjectCRUD();
            testClientCRUD();
            testRelationships();
            testCalculateProjectHRCost();
            testGetProjectsByDepartment();
            testFindClientsByUpcomingDeadline();
            testTransferEmployeeToDepartment();
            testCompositionAndAssociation();
            System.out.println("\n=== ALL TESTS PASSED ===");
        } catch (Exception e) {
            System.err.println("TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void testDepartmentCRUD() throws SQLException {
        section("DEPARTMENT CRUD");

        Department dept = departmentService.create(new Department("Test Dept", "Miami", 250000.00));
        System.out.println("Created:  " + dept);

        System.out.println("FindById: " + departmentService.findById(dept.getId()));

        List<Department> all = departmentService.findAll();
        System.out.println("FindAll:  " + all.size() + " department(s)");

        dept.setLocation("Miami Beach");
        dept.setAnnualBudget(300000.00);
        departmentService.update(dept);
        System.out.println("Updated:  " + departmentService.findById(dept.getId()));

        departmentService.delete(dept.getId());
        System.out.println("Deleted:  findById returns " + departmentService.findById(dept.getId()));
    }

    static void testEmployeeCRUD() throws SQLException {
        section("EMPLOYEE CRUD");

        Department dept1 = departmentService.findById(1);
        Employee emp = employeeService.create(new Employee(
                "Frank Castle", "Test Engineer", LocalDate.of(2024, 1, 15), 80000.00, dept1));
        System.out.println("Created:  " + emp);

        System.out.println("FindById: " + employeeService.findById(emp.getId()));

        List<Employee> all = employeeService.findAll();
        System.out.println("FindAll:  " + all.size() + " employee(s)");

        emp.setTitle("Senior Test Engineer");
        emp.setSalary(90000.00);
        employeeService.update(emp);
        System.out.println("Updated:  " + employeeService.findById(emp.getId()));

        employeeService.delete(emp.getId());
        System.out.println("Deleted:  findById returns " + employeeService.findById(emp.getId()));
    }

    static void testProjectCRUD() throws SQLException {
        section("PROJECT CRUD");

        Project proj = projectService.create(new Project(
                "Test Project", "CRUD test project",
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 10, 31),
                50000.00, ProjectStatus.ACTIVE));
        System.out.println("Created:  " + proj);

        System.out.println("FindById: " + projectService.findById(proj.getId()));

        List<Project> all = projectService.findAll();
        System.out.println("FindAll:  " + all.size() + " project(s)");

        proj.setBudget(75000.00);
        proj.setStatus(ProjectStatus.ON_HOLD);
        projectService.update(proj);
        System.out.println("Updated:  " + projectService.findById(proj.getId()));

        projectService.delete(proj.getId());
        System.out.println("Deleted:  findById returns " + projectService.findById(proj.getId()));
    }

    static void testClientCRUD() throws SQLException {
        section("CLIENT CRUD");

        Client client = clientService.create(new Client(
                "TestClient Inc.", "Testing", "Tester T.",
                "555-9999", "test@testclient.com"));
        System.out.println("Created:  " + client);

        System.out.println("FindById: " + clientService.findById(client.getId()));

        List<Client> all = clientService.findAll();
        System.out.println("FindAll:  " + all.size() + " client(s)");

        client.setIndustry("Quality Assurance");
        client.setContactEmail("qa@testclient.com");
        clientService.update(client);
        System.out.println("Updated:  " + clientService.findById(client.getId()));

        clientService.delete(client.getId());
        System.out.println("Deleted:  findById returns " + clientService.findById(client.getId()));
    }

    static void testRelationships() throws SQLException {
        section("RELATIONSHIP OPERATIONS (Junction Tables)");

        try {
            departmentService.assignToProject(1, 1);
            System.out.println("Dept 1 assigned to Project 1");
        } catch (SQLException e) {
            System.out.println("Dept 1 -> Project 1 already linked (expected): " + e.getMessage());
        }
        departmentService.removeFromProject(1, 1);
        System.out.println("Dept 1 removed from Project 1");
        departmentService.assignToProject(1, 1);
        System.out.println("Dept 1 re-assigned to Project 1");

        try {
            employeeService.assignToProject(1, 1, 50.0);
            System.out.println("Employee 1 assigned to Project 1 at 50%");
        } catch (SQLException e) {
            System.out.println("Employee 1 -> Project 1 already linked (expected): " + e.getMessage());
        }

        try {
            clientService.assignToProject(1, 1);
            System.out.println("Client 1 assigned to Project 1");
        } catch (SQLException e) {
            System.out.println("Client 1 -> Project 1 already linked (expected): " + e.getMessage());
        }
    }

    static void testCalculateProjectHRCost() throws SQLException {
        section("TASK 1 — calculateProjectHRCost");

        double cost = projectService.calculateProjectHRCost(1);
        System.out.printf("HR cost for Project Alpha (id=1): $%.2f%n", cost);
        System.out.println("  Expected: ~$30,000.00 (Alice only, 50%, 6 months)");

        double costDelta = projectService.calculateProjectHRCost(4);
        System.out.printf("HR cost for Project Delta (id=4): $%.2f%n", costDelta);
        System.out.println("  Expected: ~$51,625.00 (Alice 50% + Eve 30%, 7 months)");
    }

    static void testGetProjectsByDepartment() throws SQLException {
        section("TASK 2 — getProjectsByDepartment");

        List<Project> byBudget = departmentService.getProjectsByDepartment(1, "budget");
        System.out.println("Engineering active projects sorted by budget:");
        for (Project p : byBudget) System.out.println("  " + p);

        List<Project> byEndDate = departmentService.getProjectsByDepartment(1, "end_date");
        System.out.println("Engineering active projects sorted by end_date:");
        for (Project p : byEndDate) System.out.println("  " + p);

        try {
            departmentService.getProjectsByDepartment(1, "DROP TABLE Employee");
            System.err.println("ERROR: SQL injection was not blocked!");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid sortBy correctly rejected: " + e.getMessage());
        }
    }

    static void testFindClientsByUpcomingDeadline() throws SQLException {
        section("TASK 3 — findClientsByUpcomingProjectDeadline");

        List<Client> clients = clientService.findClientsByUpcomingProjectDeadline(30);
        System.out.println("Clients with projects ending in 30 days:");
        for (Client c : clients) System.out.println("  " + c);

        List<Client> clients90 = clientService.findClientsByUpcomingProjectDeadline(90);
        System.out.println("Clients with projects ending in 90 days:");
        for (Client c : clients90) System.out.println("  " + c);
    }

    static void testTransferEmployeeToDepartment() throws SQLException {
        section("TASK 4 — transferEmployeeToDepartment");

        Employee david = employeeService.findById(4);
        System.out.println("Before transfer: " + david);

        employeeService.transferEmployeeToDepartment(4, 1);
        System.out.println("After  transfer: " + employeeService.findById(4));

        employeeService.transferEmployeeToDepartment(4, 4);
        System.out.println("Restored:        " + employeeService.findById(4));

        try {
            employeeService.transferEmployeeToDepartment(4, 4);
            System.err.println("ERROR: same-department transfer should have thrown.");
        } catch (IllegalArgumentException e) {
            System.out.println("Same-department transfer correctly rejected: " + e.getMessage());
        }
    }

    static void testCompositionAndAssociation() throws SQLException {
        section("COMPOSITION & ASSOCIATION DEMO");

        Department eng = departmentService.findByIdWithEmployees(1);
        System.out.println("Department (with employees): " + eng);
        System.out.println("  Employees in Engineering:");
        for (Employee e : eng.getEmployees()) System.out.println("    " + e);

        Employee alice = employeeService.findById(1);
        System.out.println("Alice's department (via association): " + alice.getDepartment().getName());

        System.out.println("\nAll ProjectStatus values:");
        for (ProjectStatus s : ProjectStatus.values()) {
            System.out.println("  " + s.name() + " -> DB value: '" + s.getDbValue() + "'");
        }

        System.out.println("\nEmployee allocations on Project Alpha (id=1):");
        double cost = projectService.calculateProjectHRCost(1);
        System.out.printf("  Total HR cost: $%.2f%n", cost);
    }

    static void section(String title) {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.printf("║  %-40s  ║%n", title);
        System.out.println("╚══════════════════════════════════════════╝");
    }
}
