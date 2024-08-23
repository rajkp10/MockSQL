# MockSQL

MockSQL is a simple MySQL database management system clone created using core Java. The project aims to mimic basic functionalities of MySQL, allowing users to interact with databases, create tables, insert data, and execute SQL queries, all within a Java environment. This project is built entirely without using any third-party libraries.

## Features

- Authentication
  - User login with User ID and Password.
  - CAPTCHA verification to enhance security.
- Database Management
  - List Database
  - Switch Database
- Table Management
  - Create Table
  - Add Primary Key Constraint
  - Insert Rows
  - Select Query
  - Drop Table
- Transactions
  - Manage multiple SQL statements as a single transaction to maintain data integrity.

## Usage

Upon running the project, you'll be prompted to log in using your User ID and Password, along with a CAPTCHA verification. Once authenticated, you can execute various SQL-like commands to manage databases and tables.

Here are some basic commands you can use:

### Show all databases

```
SHOW DATABASES;
```

### Create a database

```
CREATE DATABASE my_database;
```

### Switch to a database

```
USE my_database;
```

### Show all tables in current database

```
SHOW TABLES;
```

### Create a table

```
CREATE TABLE my_table (id integer primary key, name varchar(255), age integer);
```

### Insert data into a table:

```
INSERT INTO my_table (id, name, age) VALUES (1, 'John Doe', 30);
```

### Select data from a table

```
SELECT * FROM my_table;
```

### Drop a table

```
DROP TABLE my_table;
```

### Drop a database

```
DROP DATABASE my_database;
```

### Transaction Commit

```
BEGIN TRASACTION
    INSERT INTO my_table VALUES (1, 'John', 23);
    INSERT INTO my_table VALUES (2, 'Doe', 22);
    COMMIT;
END TRANSACTION;
```

### Transaction Rollback

```
BEGIN TRASACTION
    INSERT INTO my_table VALUES (1, 'John', 23);
    INSERT INTO my_table VALUES (2, 'Doe', 22);
    ROLLBACK;
END TRANSACTION;
```