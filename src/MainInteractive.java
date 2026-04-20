import com.eems.model.*;
import com.eems.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MainInteractive {

    static final Scanner sc = new Scanner(System.in);

    static final DepartmentService departmentService = new DepartmentService();
    static final EmployeeService employeeService = new EmployeeService();
    static final ProjectService projectService = new ProjectService();
    static final ClientService clientService = new ClientService();

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║   EEMS — Employment Management System    ║");
        System.out.println("╚══════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            System.out.println("\n── Main Menu ──────────────────────────────");
            System.out.println("  1. Departments");
            System.out.println("  2. Employees");
            System.out.println("  3. Projects");
            System.out.println("  4. Clients");
            System.out.println("  5. Relationships");
            System.out.println("  6. Business Logic Tasks");
            System.out.println("  0. Exit");
            switch (prompt("Choice")) {
                case "1" -> departmentMenu();
                case "2" -> employeeMenu();
                case "3" -> projectMenu();
                case "4" -> clientMenu();
                case "5" -> relationshipMenu();
                case "6" -> businessLogicMenu();
                case "0" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
        System.out.println("Goodbye.");
    }

    static void departmentMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── Departments ────────────────────────────");
            System.out.println("  1. Create");
            System.out.println("  2. Find by ID");
            System.out.println("  3. List All");
            System.out.println("  4. Update");
            System.out.println("  5. Delete");
            System.out.println("  6. View with Employees");
            System.out.println("  0. Back");
            switch (prompt("Choice")) {
                case "1" -> createDepartment();
                case "2" -> findDepartmentById();
                case "3" -> listAllDepartments();
                case "4" -> updateDepartment();
                case "5" -> deleteDepartment();
                case "6" -> viewDepartmentWithEmployees();
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void createDepartment() {
        try {
            String name = prompt("Name");
            String location = prompt("Location");
            double budget = promptDouble("Annual Budget");
            Department d = departmentService.create(new Department(name, location, budget));
            System.out.println("Created: " + d);
        } catch (Exception e) {
            error(e);
        }
    }

    static void findDepartmentById() {
        try {
            int id = promptInt("Department ID");
            Department d = departmentService.findById(id);
            System.out.println(d != null ? d : "Not found.");
        } catch (Exception e) {
            error(e);
        }
    }

    static void listAllDepartments() {
        try {
            List<Department> list = departmentService.findAll();
            if (list.isEmpty()) {
                System.out.println("No departments found.");
                return;
            }
            list.forEach(System.out::println);
        } catch (Exception e) {
            error(e);
        }
    }

    static void updateDepartment() {
        try {
            int id = promptInt("Department ID to update");
            Department d = departmentService.findById(id);
            if (d == null) {
                System.out.println("Not found.");
                return;
            }
            System.out.println("Current: " + d);
            d.setName(promptOrKeep("New Name", d.getName()));
            d.setLocation(promptOrKeep("New Location", d.getLocation()));
            d.setAnnualBudget(promptDoubleOrKeep("New Annual Budget", d.getAnnualBudget()));
            departmentService.update(d);
            System.out.println("Updated: " + departmentService.findById(id));
        } catch (Exception e) {
            error(e);
        }
    }

    static void deleteDepartment() {
        try {
            int id = promptInt("Department ID to delete");
            if (!confirm("Delete department " + id)) return;
            departmentService.delete(id);
            System.out.println("Deleted.");
        } catch (Exception e) {
            error(e);
        }
    }

    static void viewDepartmentWithEmployees() {
        try {
            int id = promptInt("Department ID");
            Department d = departmentService.findByIdWithEmployees(id);
            if (d == null) {
                System.out.println("Not found.");
                return;
            }
            System.out.println(d);
            if (d.getEmployees().isEmpty()) {
                System.out.println("  No employees in this department.");
            } else {
                System.out.println("  Employees:");
                d.getEmployees().forEach(e -> System.out.println("    " + e));
            }
        } catch (Exception e) {
            error(e);
        }
    }

    static void employeeMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── Employees ──────────────────────────────");
            System.out.println("  1. Create");
            System.out.println("  2. Find by ID");
            System.out.println("  3. List All");
            System.out.println("  4. Update");
            System.out.println("  5. Delete");
            System.out.println("  0. Back");
            switch (prompt("Choice")) {
                case "1" -> createEmployee();
                case "2" -> findEmployeeById();
                case "3" -> listAllEmployees();
                case "4" -> updateEmployee();
                case "5" -> deleteEmployee();
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void createEmployee() {
        try {
            String name = prompt("Full Name");
            String title = prompt("Title (e.g. Manager, Specialist)");
            LocalDate hire = promptDate("Hire Date (YYYY-MM-DD)");
            double salary = promptDouble("Salary");
            int deptId = promptInt("Department ID");
            Department dept = departmentService.findById(deptId);
            if (dept == null) {
                System.out.println("Department not found.");
                return;
            }
            Employee emp = employeeService.create(new Employee(name, title, hire, salary, dept));
            System.out.println("Created: " + emp);
        } catch (Exception e) {
            error(e);
        }
    }

    static void findEmployeeById() {
        try {
            int id = promptInt("Employee ID");
            Employee emp = employeeService.findById(id);
            if (emp == null) {
                System.out.println("Not found.");
                return;
            }
            System.out.println(emp);
            System.out.println("  Department (association): " + emp.getDepartment().getName());
        } catch (Exception e) {
            error(e);
        }
    }

    static void listAllEmployees() {
        try {
            List<Employee> list = employeeService.findAll();
            if (list.isEmpty()) {
                System.out.println("No employees found.");
                return;
            }
            list.forEach(System.out::println);
        } catch (Exception e) {
            error(e);
        }
    }

    static void updateEmployee() {
        try {
            int id = promptInt("Employee ID to update");
            Employee emp = employeeService.findById(id);
            if (emp == null) {
                System.out.println("Not found.");
                return;
            }
            System.out.println("Current: " + emp);
            emp.setFullName(promptOrKeep("New Full Name", emp.getFullName()));
            emp.setTitle(promptOrKeep("New Title", emp.getTitle()));
            emp.setHireDate(promptDateOrKeep("New Hire Date (YYYY-MM-DD)", emp.getHireDate()));
            emp.setSalary(promptDoubleOrKeep("New Salary", emp.getSalary()));
            int newDeptId = promptIntOrKeep("New Department ID", emp.getDepartment().getId());
            if (newDeptId != emp.getDepartment().getId()) {
                Department newDept = departmentService.findById(newDeptId);
                if (newDept == null) System.out.println("  Department not found — keeping current.");
                else emp.setDepartment(newDept);
            }
            employeeService.update(emp);
            System.out.println("Updated: " + employeeService.findById(id));
        } catch (Exception e) {
            error(e);
        }
    }

    static void deleteEmployee() {
        try {
            int id = promptInt("Employee ID to delete");
            if (!confirm("Delete employee " + id)) return;
            employeeService.delete(id);
            System.out.println("Deleted.");
        } catch (Exception e) {
            error(e);
        }
    }

    static void projectMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── Projects ───────────────────────────────");
            System.out.println("  1. Create");
            System.out.println("  2. Find by ID");
            System.out.println("  3. List All");
            System.out.println("  4. Update");
            System.out.println("  5. Delete");
            System.out.println("  0. Back");
            switch (prompt("Choice")) {
                case "1" -> createProject();
                case "2" -> findProjectById();
                case "3" -> listAllProjects();
                case "4" -> updateProject();
                case "5" -> deleteProject();
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void createProject() {
        try {
            String name = prompt("Name");
            String desc = prompt("Description");
            LocalDate start = promptDate("Start Date (YYYY-MM-DD)");
            LocalDate end = promptDate("End Date   (YYYY-MM-DD)");
            double budget = promptDouble("Budget");
            ProjectStatus status = promptStatus();
            Project p = projectService.create(new Project(name, desc, start, end, budget, status));
            System.out.println("Created: " + p);
        } catch (Exception e) {
            error(e);
        }
    }

    static void findProjectById() {
        try {
            int id = promptInt("Project ID");
            Project p = projectService.findById(id);
            System.out.println(p != null ? p : "Not found.");
        } catch (Exception e) {
            error(e);
        }
    }

    static void listAllProjects() {
        try {
            List<Project> list = projectService.findAll();
            if (list.isEmpty()) {
                System.out.println("No projects found.");
                return;
            }
            list.forEach(System.out::println);
        } catch (Exception e) {
            error(e);
        }
    }

    static void updateProject() {
        try {
            int id = promptInt("Project ID to update");
            Project p = projectService.findById(id);
            if (p == null) {
                System.out.println("Not found.");
                return;
            }
            System.out.println("Current: " + p);
            p.setName(promptOrKeep("New Name", p.getName()));
            p.setDescription(promptOrKeep("New Description", p.getDescription()));
            p.setStartDate(promptDateOrKeep("New Start Date (YYYY-MM-DD)", p.getStartDate()));
            p.setEndDate(promptDateOrKeep("New End Date   (YYYY-MM-DD)", p.getEndDate()));
            p.setBudget(promptDoubleOrKeep("New Budget", p.getBudget()));
            p.setStatus(promptStatusOrKeep(p.getStatus()));
            projectService.update(p);
            System.out.println("Updated: " + projectService.findById(id));
        } catch (Exception e) {
            error(e);
        }
    }

    static void deleteProject() {
        try {
            int id = promptInt("Project ID to delete");
            if (!confirm("Delete project " + id)) return;
            projectService.delete(id);
            System.out.println("Deleted.");
        } catch (Exception e) {
            error(e);
        }
    }

    static void clientMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── Clients ────────────────────────────────");
            System.out.println("  1. Create");
            System.out.println("  2. Find by ID");
            System.out.println("  3. List All");
            System.out.println("  4. Update");
            System.out.println("  5. Delete");
            System.out.println("  0. Back");
            switch (prompt("Choice")) {
                case "1" -> createClient();
                case "2" -> findClientById();
                case "3" -> listAllClients();
                case "4" -> updateClient();
                case "5" -> deleteClient();
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void createClient() {
        try {
            String name = prompt("Name");
            String indust = prompt("Industry");
            String cName = prompt("Contact Name");
            String cPhone = prompt("Contact Phone");
            String cEmail = prompt("Contact Email");
            Client c = clientService.create(new Client(name, indust, cName, cPhone, cEmail));
            System.out.println("Created: " + c);
        } catch (Exception e) {
            error(e);
        }
    }

    static void findClientById() {
        try {
            int id = promptInt("Client ID");
            Client c = clientService.findById(id);
            System.out.println(c != null ? c : "Not found.");
        } catch (Exception e) {
            error(e);
        }
    }

    static void listAllClients() {
        try {
            List<Client> list = clientService.findAll();
            if (list.isEmpty()) {
                System.out.println("No clients found.");
                return;
            }
            list.forEach(System.out::println);
        } catch (Exception e) {
            error(e);
        }
    }

    static void updateClient() {
        try {
            int id = promptInt("Client ID to update");
            Client c = clientService.findById(id);
            if (c == null) {
                System.out.println("Not found.");
                return;
            }
            System.out.println("Current: " + c);
            c.setName(promptOrKeep("New Name", c.getName()));
            c.setIndustry(promptOrKeep("New Industry", c.getIndustry()));
            c.setContactName(promptOrKeep("New Contact Name", c.getContactName()));
            c.setContactPhone(promptOrKeep("New Contact Phone", c.getContactPhone()));
            c.setContactEmail(promptOrKeep("New Contact Email", c.getContactEmail()));
            clientService.update(c);
            System.out.println("Updated: " + clientService.findById(id));
        } catch (Exception e) {
            error(e);
        }
    }

    static void deleteClient() {
        try {
            int id = promptInt("Client ID to delete");
            if (!confirm("Delete client " + id)) return;
            clientService.delete(id);
            System.out.println("Deleted.");
        } catch (Exception e) {
            error(e);
        }
    }

    static void relationshipMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── Relationships ──────────────────────────");
            System.out.println("  1. Assign Department to Project");
            System.out.println("  2. Remove Department from Project");
            System.out.println("  3. Assign Employee to Project");
            System.out.println("  4. Remove Employee from Project");
            System.out.println("  5. Assign Client to Project");
            System.out.println("  6. Remove Client from Project");
            System.out.println("  0. Back");
            switch (prompt("Choice")) {
                case "1" -> {
                    try {
                        departmentService.assignToProject(promptInt("Department ID"), promptInt("Project ID"));
                        System.out.println("Assigned.");
                    } catch (Exception e) {
                        error(e);
                    }
                }
                case "2" -> {
                    try {
                        departmentService.removeFromProject(promptInt("Department ID"), promptInt("Project ID"));
                        System.out.println("Removed.");
                    } catch (Exception e) {
                        error(e);
                    }
                }
                case "3" -> {
                    try {
                        employeeService.assignToProject(promptInt("Employee ID"), promptInt("Project ID"), promptDouble("Allocation % (0-100)"));
                        System.out.println("Assigned.");
                    } catch (Exception e) {
                        error(e);
                    }
                }
                case "4" -> {
                    try {
                        employeeService.removeFromProject(promptInt("Employee ID"), promptInt("Project ID"));
                        System.out.println("Removed.");
                    } catch (Exception e) {
                        error(e);
                    }
                }
                case "5" -> {
                    try {
                        clientService.assignToProject(promptInt("Client ID"), promptInt("Project ID"));
                        System.out.println("Assigned.");
                    } catch (Exception e) {
                        error(e);
                    }
                }
                case "6" -> {
                    try {
                        clientService.removeFromProject(promptInt("Client ID"), promptInt("Project ID"));
                        System.out.println("Removed.");
                    } catch (Exception e) {
                        error(e);
                    }
                }
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void businessLogicMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── Business Logic Tasks ───────────────────");
            System.out.println("  1. Calculate Project HR Cost");
            System.out.println("  2. Get Active Projects by Department");
            System.out.println("  3. Find Clients by Upcoming Project Deadline");
            System.out.println("  4. Transfer Employee to Department");
            System.out.println("  0. Back");
            switch (prompt("Choice")) {
                case "1" -> taskCalculateHRCost();
                case "2" -> taskProjectsByDepartment();
                case "3" -> taskClientsByDeadline();
                case "4" -> taskTransferEmployee();
                case "0" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void taskCalculateHRCost() {
        try {
            int projectId = promptInt("Project ID");
            double cost = projectService.calculateProjectHRCost(projectId);
            System.out.printf("Projected HR cost for project %d: $%,.2f%n", projectId, cost);
        } catch (Exception e) {
            error(e);
        }
    }

    static void taskProjectsByDepartment() {
        try {
            int deptId = promptInt("Department ID");
            System.out.println("Sort by field — allowed: budget, end_date, start_date, name, status");
            String sortBy = prompt("Sort by");
            List<Project> projects = departmentService.getProjectsByDepartment(deptId, sortBy);
            if (projects.isEmpty()) {
                System.out.println("No active projects found for department " + deptId);
            } else {
                System.out.println("Active projects (sorted by " + sortBy + "):");
                projects.forEach(p -> System.out.println("  " + p));
            }
        } catch (Exception e) {
            error(e);
        }
    }

    static void taskClientsByDeadline() {
        try {
            int days = promptInt("Days until deadline (from today)");
            List<Client> clients = clientService.findClientsByUpcomingProjectDeadline(days);
            if (clients.isEmpty()) {
                System.out.println("No clients found with projects ending within " + days + " day(s).");
            } else {
                System.out.println("Clients with projects ending within " + days + " day(s):");
                clients.forEach(c -> System.out.println("  " + c));
            }
        } catch (Exception e) {
            error(e);
        }
    }

    static void taskTransferEmployee() {
        try {
            int empId = promptInt("Employee ID");
            int deptId = promptInt("Target Department ID");
            Employee before = employeeService.findById(empId);
            if (before == null) {
                System.out.println("Employee not found.");
                return;
            }
            System.out.println("Before: " + before);
            employeeService.transferEmployeeToDepartment(empId, deptId);
            System.out.println("After:  " + employeeService.findById(empId));
        } catch (Exception e) {
            error(e);
        }
    }

    static String prompt(String label) {
        System.out.print(label + ": ");
        return sc.nextLine().trim();
    }

    static int promptInt(String label) {
        while (true) {
            String input = prompt(label);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a whole number.");
            }
        }
    }

    static double promptDouble(String label) {
        while (true) {
            String input = prompt(label);
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a numeric value.");
            }
        }
    }

    static LocalDate promptDate(String label) {
        while (true) {
            String input = prompt(label);
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("  Use format YYYY-MM-DD.");
            }
        }
    }

    static ProjectStatus promptStatus() {
        System.out.println("  Status options: ACTIVE, COMPLETED, ON_HOLD, CANCELLED");
        while (true) {
            String input = prompt("Status").toUpperCase().replace(" ", "_").replace("-", "_");
            try {
                return ProjectStatus.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("  Invalid status. Use: ACTIVE, COMPLETED, ON_HOLD, CANCELLED");
            }
        }
    }

    static ProjectStatus promptStatusOrKeep(ProjectStatus current) {
        System.out.println("  Status options: ACTIVE, COMPLETED, ON_HOLD, CANCELLED");
        System.out.print("New Status [" + current.name() + "]: ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) return current;
        try {
            return ProjectStatus.valueOf(input.toUpperCase().replace(" ", "_").replace("-", "_"));
        } catch (IllegalArgumentException e) {
            System.out.println("  Invalid — keeping: " + current.name());
            return current;
        }
    }

    static String promptOrKeep(String label, String current) {
        System.out.print(label + " [" + current + "]: ");
        String input = sc.nextLine().trim();
        return input.isEmpty() ? current : input;
    }

    static double promptDoubleOrKeep(String label, double current) {
        while (true) {
            System.out.print(label + " [" + current + "]: ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) return current;
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a numeric value.");
            }
        }
    }

    static int promptIntOrKeep(String label, int current) {
        while (true) {
            System.out.print(label + " [" + current + "]: ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) return current;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a whole number.");
            }
        }
    }

    static LocalDate promptDateOrKeep(String label, LocalDate current) {
        while (true) {
            System.out.print(label + " [" + current + "]: ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) return current;
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("  Use format YYYY-MM-DD.");
            }
        }
    }

    static boolean confirm(String message) {
        return prompt(message + " — are you sure? (y/n)").equalsIgnoreCase("y");
    }

    static void error(Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
}
