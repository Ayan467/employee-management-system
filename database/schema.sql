CREATE DATABASE IF NOT EXISTS employee_management;
USE employee_management;

CREATE TABLE IF NOT EXISTS employees (
    id           INT            NOT NULL AUTO_INCREMENT,
    name         VARCHAR(100)   NOT NULL,
    email        VARCHAR(100)   NOT NULL,
    phone        VARCHAR(15)    NOT NULL,
    department   VARCHAR(50)    NOT NULL,
    designation  VARCHAR(50)    NOT NULL,
    salary       DECIMAL(10,2)  NOT NULL,
    joining_date DATE           NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uq_employee_email (email),
    CONSTRAINT chk_salary CHECK (salary >= 0)
);
