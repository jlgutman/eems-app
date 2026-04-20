create database eems;
use eems;

DROP TABLE IF EXISTS Client_Project;
DROP TABLE IF EXISTS Employee_Project;
DROP TABLE IF EXISTS Department_Project;
DROP TABLE IF EXISTS Client;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Project;
DROP TABLE IF EXISTS Department;

CREATE TABLE Department (
    id            INT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(100)    NOT NULL,
    location      VARCHAR(100)    NOT NULL,
    annual_budget DECIMAL(15, 2)  NOT NULL
);

CREATE TABLE Employee (
    id            INT PRIMARY KEY AUTO_INCREMENT,
    full_name     VARCHAR(100)    NOT NULL,
    title         VARCHAR(100)    NOT NULL,
    hire_date     DATE            NOT NULL,
    salary        DECIMAL(10, 2)  NOT NULL,
    department_id INT             NOT NULL,
    FOREIGN KEY (department_id) REFERENCES Department(id)
);

CREATE TABLE Project (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100)   NOT NULL,
    description TEXT,
    start_date  DATE           NOT NULL,
    end_date    DATE           NOT NULL,
    budget      DECIMAL(15, 2) NOT NULL,
    status      VARCHAR(20)    NOT NULL DEFAULT 'Active'
);

CREATE TABLE Client (
    id            INT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    industry      VARCHAR(100) NOT NULL,
    contact_name  VARCHAR(100) NOT NULL,
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100)
);

CREATE TABLE Department_Project (
    department_id INT NOT NULL,
    project_id    INT NOT NULL,
    PRIMARY KEY (department_id, project_id),
    FOREIGN KEY (department_id) REFERENCES Department(id),
    FOREIGN KEY (project_id)    REFERENCES Project(id)
);

CREATE TABLE Employee_Project (
    employee_id           INT            NOT NULL,
    project_id            INT            NOT NULL,
    allocation_percentage DECIMAL(5, 2)  NOT NULL,
    PRIMARY KEY (employee_id, project_id),
    FOREIGN KEY (employee_id) REFERENCES Employee(id),
    FOREIGN KEY (project_id)  REFERENCES Project(id)
);

CREATE TABLE Client_Project (
    client_id  INT NOT NULL,
    project_id INT NOT NULL,
    PRIMARY KEY (client_id, project_id),
    FOREIGN KEY (client_id)  REFERENCES Client(id),
    FOREIGN KEY (project_id) REFERENCES Project(id)
);

INSERT INTO Department (name, location, annual_budget) VALUES
    ('Engineering', 'Chicago',  1000000.00),
    ('Marketing',   'New York',  500000.00),
    ('Finance',     'Dallas',    750000.00),
    ('HR',          'Seattle',   300000.00),
    ('Operations',  'Boston',    450000.00);

INSERT INTO Employee (full_name, title, hire_date, salary, department_id) VALUES
    ('Alice Johnson', 'Senior Engineer',    '2020-03-15', 120000.00, 1),
    ('Bob Smith',     'Marketing Manager',  '2019-07-01', 85000.00,  2),
    ('Carol White',   'Financial Analyst',  '2021-01-10', 90000.00,  3),
    ('David Brown',   'HR Specialist',      '2022-06-20', 70000.00,  4),
    ('Eve Davis',     'Operations Lead',    '2018-11-05', 95000.00,  5);

INSERT INTO Project (name, description, start_date, end_date, budget, status) VALUES
    ('Project Alpha',   'Core platform rebuild',          '2026-01-01', '2026-06-30', 200000.00, 'Active'),
    ('Project Beta',    'Marketing campaign launch',      '2026-02-01', '2026-04-30', 150000.00, 'Active'),
    ('Project Gamma',   'Legacy system migration',        '2025-01-01', '2025-12-31', 300000.00, 'Completed'),
    ('Project Delta',   'ERP integration',                '2026-03-01', '2026-09-30', 400000.00, 'Active'),
    ('Project Epsilon', 'Client onboarding automation',  '2026-04-01', '2026-05-20', 100000.00, 'Active');

INSERT INTO Client (name, industry, contact_name, contact_phone, contact_email) VALUES
    ('TechCorp',    'Technology', 'John Tech',   '555-1001', 'john@techcorp.com'),
    ('RetailMax',   'Retail',     'Jane Retail',  '555-1002', 'jane@retailmax.com'),
    ('HealthPlus',  'Healthcare', 'Bob Health',   '555-1003', 'bob@healthplus.com'),
    ('EduLearn',    'Education',  'Sue Edu',      '555-1004', 'sue@edulearn.com'),
    ('GreenEnergy', 'Energy',     'Tom Green',    '555-1005', 'tom@greenenergy.com');

INSERT INTO Department_Project (department_id, project_id) VALUES
    (1, 1),
    (1, 4),
    (2, 2),
    (2, 5),
    (3, 3);

INSERT INTO Employee_Project (employee_id, project_id, allocation_percentage) VALUES
    (1, 1, 50.00),
    (1, 4, 50.00),
    (2, 2, 100.00),
    (5, 4, 30.00);

INSERT INTO Client_Project (client_id, project_id) VALUES
    (1, 1),
    (1, 2),
    (2, 2),
    (3, 5),
    (4, 3);
