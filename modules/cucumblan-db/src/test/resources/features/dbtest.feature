Feature: Testing the sql validation
Scenario: Testing the sql validation
    Given Execute DDL for the given sql for employees on employee
        | create table employees (emp_no int, birth_date date,first_name VARCHAR(50),last_name VARCHAR(50), gender VARCHAR(50),hire_date date)  |
    When Execute INSERT for the given sql for employees on employee
        |insert into employees (emp_no,birth_date,first_name,last_name, gender,hire_date) values  (2,'1978-01-08','ELan', 'Thangamani', 'Male', '2007-10-10')|
    Then Verify details with the given sql for employees on employee
        | select * from employees where emp_no = 2                  |
        | EMP_NO,BIRTH_DATE,FIRST_NAME,LAST_NAME,GENDER,HIRE_DATE   |
        |   i~2,l~253087200000,ELan,Thangamani,Male,l~1191992400000          |
    And Execute DELETE for the given sql for employees on employee
        | delete from employees where emp_no = 2    |
