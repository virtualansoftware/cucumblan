Feature: Testing the sql validation
Scenario: Testing the sql validation
    Given As a sql test user on employees
    When create the given sql for employees on employee
    |insert into employees (emp_no,birth_date,first_name,last_name, gender,hire_date) values  (1,'1978-01-08','ELan', 'Thangamani', 'Male', '2007-10-10')|
    When insert the given sql for employees on employee
    |insert into employees (emp_no,birth_date,first_name,last_name, gender,hire_date) values  (1,'1978-01-08','ELan', 'Thangamani', 'Male', '2007-10-10')|
    Then select the given sql for employees on employee
    | select * from employees where emp_no = 1                  |
    | EMP_NO,BIRTH_DATE,FIRST_NAME,LAST_NAME,GENDER,HIRE_DATE   |
    |   i~1,l~253087200000,ELan,Thangamani,Male,l~1191992400000          |
    And delete the given sql for employees on employee
    | delete from employees where emp_no = 1    |
