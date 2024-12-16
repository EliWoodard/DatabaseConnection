# Database Project

This project is a command-line Java application that manages a student grading system, connecting to a MySQL database to perform various operations such as managing classes, students, assignments, and grades.

## Features

- **Class Management**: Create, select, and display information about classes.
- **Category Management**: Add and display categories for a class.
- **Assignment Management**: Add and display assignments for a class.
- **Student Management**: Add students, enroll them in classes, and display student information.
- **Grading**: Assign grades to students for specific assignments.
- **Gradebook**: Display grades for a student or all students in a class.
- **Database Connection**: Connects to a MySQL database using JDBC.

---

## Setup

### Prerequisites
1. **Java**: Ensure you have Java 8 or above installed.
2. **MySQL**: Install MySQL and configure it with a valid username, password, and port.
3. **MySQL Workbench** (optional): For database management and queries.
4. **JDBC Driver**: Download the MySQL JDBC driver and add it to your classpath.

### Database Configuration
1. Create the database and tables:
   ```sql
   CREATE DATABASE IF NOT EXISTS FinalProject;
   USE FinalProject;

   CREATE TABLE Class( 
       classID INT AUTO_INCREMENT PRIMARY KEY,
       description TEXT,
       sectionNumber INT,
       term VARCHAR(10),
       courseNumber VARCHAR(10)
   );

   CREATE TABLE Student( 
       studentID INT PRIMARY KEY,
       email VARCHAR(150),
       username VARCHAR(50),
       firstname VARCHAR(20),
       lastname VARCHAR(20)
   );

   CREATE TABLE Category( 
       classID INT NOT NULL,
       categoryType VARCHAR(20) NOT NULL,
       weight INT NOT NULL,
       PRIMARY KEY (classID, categoryType),
       FOREIGN KEY (classID) REFERENCES Class(classID)
   );

   CREATE TABLE Enroll ( 
       studentID INT,
       classID INT,
       PRIMARY KEY (studentID, classID),
       FOREIGN KEY (studentID) REFERENCES Student(studentID),
       FOREIGN KEY (classID) REFERENCES Class(classID)
   );

   CREATE TABLE Assignment ( 
       assignmentID INT AUTO_INCREMENT PRIMARY KEY,
       classID INT,
       categoryType VARCHAR(20),
       name VARCHAR(100),
       description TEXT,
       pointValue INT,
       FOREIGN KEY (classID) REFERENCES Class(classID),
       UNIQUE (classID, name)
   );

   CREATE TABLE Grade( 
       grade INT,
       studentID INT NOT NULL,
       assignmentID INT NOT NULL,
       PRIMARY KEY (studentID, assignmentID),
       FOREIGN KEY (studentID) REFERENCES Student(studentID),
       FOREIGN KEY (assignmentID) REFERENCES Assignment(assignmentID)
   );

2.  Update `DatabaseConnection` to use your MySQL credentials:

    ```String masterHost = "127.0.0.1"; // Host
    int masterPort = 3306; // Port
    String masterUser = "your_username"; // User
    String masterPassword = "your_password"; // Password
    String dbName = "FinalProject"; // Database name```

How to Run
----------

1.  **Compile the Project**:
    `javac -cp <path_to_mysql_connector_jar> DatabaseConnection.java`

2.  **Run the Project**:

    `java -cp <path_to_mysql_connector_jar>: DatabaseConnection`

3.  **Enter Commands**: Use the following commands in the application.

* * * * *

Commands
--------

# Create a class
new-class <courseNumber> <term> <sectionNumber> <description>
Example: new-class CS410 Sp20 1 "Databases"

# Select a class
select-class <courseNumber> <term> <sectionNumber>
Example: select-class CS410 Sp20 1

# Show the current class
show-class

# Add a category
add-category <categoryType> <weight>
Example: add-category homework 50

# Show categories for a class
show-categories

# Add an assignment
add-assignment <name> <categoryType> "<description>" <pointValue>
Example: add-assignment HW1 homework "Complete the first homework" 100

# Show assignments for a class
show-assignment

# Add a student and enroll them in a class
add-student <username> <studentID> <lastName> <firstName>
Example: add-student john123 1 Doe John

# Show students in a class
show-students

# Grade an assignment for a student
grade <assignmentName> <username> <grade>
Example: grade HW1 john123 90

# Show grades for a student
student-grades <username>
Example: student-grades john123

# Show the gradebook for the current class
gradebook

# Exit the application
exit