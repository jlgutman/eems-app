# EEMS — UML & Database Diagrams

---

## 1. Class Diagram

```mermaid
classDiagram
    direction TB

    class BaseEntity {
        <<abstract>>
        #int id
    }

    class Department {
        -String name
        -String location
        -double annualBudget
        -List~Employee~ employees
    }

    class Employee {
        -String fullName
        -String title
        -LocalDate hireDate
        -double salary
        -Department department
    }

    class Project {
        -String name
        -String description
        -LocalDate startDate
        -LocalDate endDate
        -double budget
        -ProjectStatus status
    }

    class Client {
        -String name
        -String industry
        -String contactName
        -String contactPhone
        -String contactEmail
    }

    class EmployeeAllocation {
        -Employee employee
        -double allocationPercentage
        +getEmployee() Employee
        +getAllocationPercentage() double
    }

    class ProjectStatus {
        <<enumeration>>
        ACTIVE
        COMPLETED
        ON_HOLD
        CANCELLED
        +getDbValue() String
        +fromDbValue(String value) ProjectStatus
    }

    %% Inheritance
    BaseEntity <|-- Department
    BaseEntity <|-- Employee
    BaseEntity <|-- Project
    BaseEntity <|-- Client

    %% Composition: Department owns Employees (loaded eagerly via findByIdWithEmployees)
    Department "1" *-- "0..*" Employee : employs

    %% Association: Employee references its Department
    Employee "0..*" --> "1" Department : belongs to

    %% M:N resolved in DB via Department_Project junction table
    Department "0..*" -- "0..*" Project : hosts

    %% M:N resolved in DB via Employee_Project junction table
    Employee "0..*" -- "0..*" Project : works on

    %% M:N resolved in DB via Client_Project junction table
    Client "0..*" -- "0..*" Project : sponsors

    %% Value object — not persisted independently
    EmployeeAllocation "1" --> "1" Employee : wraps
    EmployeeAllocation ..> Project : used by HR-cost calc

    %% Enum
    Project --> ProjectStatus : has status
```

---

## 2. Use Case Diagram

```mermaid
graph LR
    Actor(["👤 User"])

    subgraph EEMS System
        subgraph CRUD Operations
            UC1([Manage Departments])
            UC2([Manage Employees])
            UC3([Manage Projects])
            UC4([Manage Clients])
        end

        subgraph Relationship Management
            UC5([Assign / Remove\nDept ↔ Project])
            UC6([Assign / Remove\nEmployee ↔ Project])
            UC7([Assign / Remove\nClient ↔ Project])
        end

        subgraph Business Logic Tasks
            UC8([Calculate Project\nHR Cost])
            UC9([Get Active Projects\nby Department])
            UC10([Find Clients by\nUpcoming Deadline])
            UC11([Transfer Employee\nto Department])
        end
    end

    Actor --> UC1
    Actor --> UC2
    Actor --> UC3
    Actor --> UC4
    Actor --> UC5
    Actor --> UC6
    Actor --> UC7
    Actor --> UC8
    Actor --> UC9
    Actor --> UC10
    Actor --> UC11

    %% include relationships (extend notation approximated with dashed arrows)
    UC1 -. includes .-> UC_CRUD([Create / Read / Update / Delete])
    UC2 -. includes .-> UC_CRUD
    UC3 -. includes .-> UC_CRUD
    UC4 -. includes .-> UC_CRUD

    UC11 -. extends .-> UC2
```

---

## 3. Sequence Diagram — Task 1: `calculateProjectHRCost(projectId)`

```mermaid
sequenceDiagram
    actor User
    participant CLI  as MainInteractive
    participant PS   as ProjectService
    participant PR   as ProjectRepository
    participant ER   as EmployeeRepository
    participant DB   as MySQL (eems)

    User  ->>  CLI : 1. enter projectId
    CLI   ->>  PS  : 2. calculateProjectHRCost(projectId)

    %% Step 1 — load the project
    PS    ->>  PR  : 3. findById(projectId)
    PR    ->>  DB  : 4. SELECT * FROM Project WHERE id = ?
    DB   -->>  PR  : 5. ResultSet
    PR   -->>  PS  : 6. Project { startDate, endDate }

    %% Step 2 — compute duration
    Note over PS : durationMonths = ChronoUnit.MONTHS.between(start, end)<br/>rounded UP to next full month (min 1)

    %% Step 3 — load allocations
    PS    ->>  ER  : 7. findAllocationsByProject(projectId)
    ER    ->>  DB  : 8. SELECT e.*, d.*, ep.allocation_percentage<br/>FROM Employee e<br/>JOIN Department d ON e.department_id = d.id<br/>JOIN Employee_Project ep ON e.id = ep.employee_id<br/>WHERE ep.project_id = ?
    DB   -->>  ER  : 9. ResultSet (one row per employee)
    ER   -->>  PS  : 10. List‹EmployeeAllocation›

    %% Step 4 — calculate
    Note over PS : totalCost = stream.mapToDouble(<br/>  alloc → (salary / 12) × months × (allocationPct / 100)<br/>).sum()

    PS   -->>  CLI : 11. double totalCost
    CLI  -->>  User : 12. "$X,XXX.XX"
```

---

## 4. Database / ER Diagram

```mermaid
erDiagram

    DEPARTMENT {
        int     id             PK
        varchar name           "NOT NULL"
        varchar location       "NOT NULL"
        decimal annual_budget  "NOT NULL"
    }

    EMPLOYEE {
        int     id            PK
        varchar full_name     "NOT NULL"
        varchar title         "NOT NULL"
        date    hire_date     "NOT NULL"
        decimal salary        "NOT NULL"
        int     department_id FK
    }

    PROJECT {
        int     id          PK
        varchar name        "NOT NULL"
        text    description
        date    start_date  "NOT NULL"
        date    end_date    "NOT NULL"
        decimal budget      "NOT NULL"
        varchar status      "DEFAULT Active"
    }

    CLIENT {
        int     id            PK
        varchar name          "NOT NULL"
        varchar industry      "NOT NULL"
        varchar contact_name  "NOT NULL"
        varchar contact_phone
        varchar contact_email
    }

    DEPARTMENT_PROJECT {
        int department_id PK,FK
        int project_id    PK,FK
    }

    EMPLOYEE_PROJECT {
        int     employee_id           PK,FK
        int     project_id            PK,FK
        decimal allocation_percentage "NOT NULL"
    }

    CLIENT_PROJECT {
        int client_id  PK,FK
        int project_id PK,FK
    }

    %% Core relationships
    DEPARTMENT      ||--o{ EMPLOYEE          : "employs (1 dept → many employees)"
    DEPARTMENT      ||--o{ DEPARTMENT_PROJECT : "linked via junction"
    PROJECT         ||--o{ DEPARTMENT_PROJECT : "linked via junction"
    EMPLOYEE        ||--o{ EMPLOYEE_PROJECT   : "assigned via junction"
    PROJECT         ||--o{ EMPLOYEE_PROJECT   : "assigned via junction"
    CLIENT          ||--o{ CLIENT_PROJECT     : "linked via junction"
    PROJECT         ||--o{ CLIENT_PROJECT     : "linked via junction"
```
