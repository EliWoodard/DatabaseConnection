import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class DatabaseConnection {

    static int currentClass = -1;

    public static void main(String[] args) {
        Connection con = null;

        String masterHost = "127.0.0.1"; // Host
        int masterPort = 55466; // Port
        String masterUser = "msandbox"; // User
        String masterPassword = "a"; // Password
        String dbName = "FinalProject"; // Database name

        try {
            /*
             * STEP 1 and 2
             * LOAD the Database DRIVER and obtain a CONNECTION
             */
            Class.forName("com.mysql.cj.jdbc.Driver");
            String dbUrl = "jdbc:mysql://" + masterHost + ":" + masterPort + "/" + dbName
                    + "?verifyServerCertificate=false&useSSL=true&serverTimezone=UTC";

            System.out.println("Connecting to: " + dbUrl);
            con = DriverManager.getConnection(dbUrl, masterUser, masterPassword);

            // Connection success message
            System.out.println("Database [" + dbName + "] connection succeeded!");

            // Start writing statements from assignement:
            System.out.println("Enter a command:");
            Scanner scanner = new Scanner(System.in);

            String command = "";

            while (!command.equalsIgnoreCase("exit")) {
                command = scanner.nextLine();
                String firstPartCommand = command.split(" ")[0];

                switch (firstPartCommand) {
                    case "new-class":
                        createClass(command, con);
                        break;
                    case "show-class":
                        if (currentClass == -1) {
                            System.out.println("You need to select a class. Use `select-class`");
                        } else {
                            showClass(currentClass, con);
                        }
                        break;
                    case "select-class":
                        selectClass(command, con);
                        break;
                    case "show-categories":
                        showCategories(con);
                        break;
                    case "add-category":
                        addCategory(command, con);
                        break;
                    case "show-assignment":
                        showAssignments(con);
                        break;
                    case "add-assignment":
                        addAssignment(command, con);
                        break;
                    case "add-student":
                        addStudent(command, con);
                        break;
                    case "show-students":
                        showStudents(command, con);
                        break;
                    case "grade":
                        grade(command, con);
                        break;
                    case "student-grades":
                        studentGrades(command, con);
                        break;
                    case "gradebook":
                        gradebook(con);
                        break;
                    case "help":
                        help();
                        break;
                    case "exit":
                        break;
                    default:
                        System.out.println("Unknown command please type `help` if needed");
                        break;
                }
            }

            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Clean up resources
            try {
                if (con != null)
                    con.close();
            } catch (Exception closeEx) {
                closeEx.printStackTrace();
            }
        }
    }

    /**
     * Creates a new class in the database with the provided details.
     * Command format: new-class <courseNumber> <term> <sectionNumber> <description>
     * 
     * @param commandLine the user input command
     * @param con the database connection
     */
    public static void createClass(String commandLine, Connection con) {
        String[] arr = commandLine.split(" ");

        // Validate inputs
        if (arr.length < 5) {
            System.out.println("Error: Invalid number of arguments for `new-class`.");
            return;
        }

        String courseNumber = arr[1];
        String term = arr[2];
        int sectionNumber = Integer.parseInt(arr[3]);
        String description = arr[4];

        String insertQuery = "INSERT INTO Class (courseNumber, term, sectionNumber, description) VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement pstmt = con.prepareStatement(insertQuery);

            // Bind parameters
            pstmt.setString(1, courseNumber);
            pstmt.setString(2, term);
            pstmt.setInt(3, sectionNumber);
            pstmt.setString(4, description);

            // Execute the insert
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Class created successfully!");
            } else {
                System.out.println("Failed to create the class.");
            }
        } catch (Exception e) {
            System.out.println("Error during class creation: " + e.getMessage());
        }
    }

    /**
     * Displays details of the currently selected class.
     * 
     * @param currentClass the ID of the currently selected class
     * @param con the database connection
     */
    public static void showClass(int currentClass, Connection con) {
        String query = "SELECT * FROM Class WHERE classID = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, currentClass);

            ResultSet rs = pstmt.executeQuery();

            // Check if a class was found
            if (rs.next()) {
                System.out.println("-------------------------------------------");
                System.out.println("Currently Selected Class:");
                System.out.println("ClassID: " + rs.getInt("classID"));
                System.out.println("Course: " + rs.getString("courseNumber"));
                System.out.println("Term: " + rs.getString("term"));
                System.out.println("Section: " + rs.getInt("sectionNumber"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("-------------------------------------------");
            } else {
                System.out.println("No class found with the ClassID: " + currentClass);
            }
        } catch (Exception e) {
            System.out.println("Error during show-class: " + e.getMessage());
        }
    }

    /**
     * Selects a class based on the provided parameters (courseNumber, term, sectionNumber).
     * Command format: select-class <courseNumber> [<term>] [<sectionNumber>]
     * 
     * @param commandLine the user input command
     * @param con the database connection
     */
    public static void selectClass(String commandLine, Connection con) {
        String[] arr = commandLine.split(" ");
        String query = "";
        String courseNumber = null;
        String term = null;
        int sectionNumber = -1;
        int classID = -1;

        switch (arr.length) {
            case 2:
                query = "SELECT * FROM Class WHERE courseNumber = ?";
                courseNumber = arr[1];
                break;
            case 3:
                query = "SELECT * FROM Class WHERE courseNumber = ? AND term = ?";
                courseNumber = arr[1];
                term = arr[2];
                break;
            case 4:
                query = "SELECT * FROM Class WHERE courseNumber = ? AND term = ? AND sectionNumber = ?";
                courseNumber = arr[1];
                term = arr[2];
                sectionNumber = Integer.parseInt(arr[3]);
                break;
            default:
                System.out.println("Please try again with the section");
                break;
        }

        if (!query.isEmpty()) {
            try {
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1, courseNumber);

                if (term != null) {
                    pstmt.setString(2, term);
                }
                if (sectionNumber != -1) {
                    pstmt.setInt(3, sectionNumber);
                }

                ResultSet rs = pstmt.executeQuery();

                // Process the result
                if (rs.next()) {
                    classID = rs.getInt("classID"); // Get the classID value for a current class identifer.
                    currentClass = classID;
                    System.out.println("-------------------------------------------");
                    System.out.println("Class Selected:");
                    System.out.println("ClassID: " + rs.getInt("classID"));
                    System.out.println("Course: " + rs.getString("courseNumber"));
                    System.out.println("Term: " + rs.getString("term"));
                    System.out.println("Section: " + rs.getInt("sectionNumber"));
                    System.out.println("Description: " + rs.getString("description"));
                    System.out.println("-------------------------------------------");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Adds a category to the selected class.
     * Command format: add-category <categoryName> <weight>
     * 
     * @param commandLine the user input command
     * @param con the database connection
     */
    public static void addCategory(String commandLine, Connection con) {
        if (currentClass == -1) {
            System.out.println("You need to select a class before adding a category. Use `select-class`.");
            return;
        }

        String[] arr = commandLine.split(" ");

        // Validate inputs
        if (arr.length < 3) {
            System.out.println("Error: Invalid number of arguments for `add-category`.");
            System.out.println("Usage: add-category <Name> <Weight>");
            return;
        }

        String categoryName = arr[1];
        int weight;

        try {
            weight = Integer.parseInt(arr[2]); // Convert weight to an integer
        } catch (NumberFormatException e) {
            System.out.println("Error: Weight must be an integer.");
            return;
        }

        String insertQuery = "INSERT INTO Category (classID, categoryType, weight) VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = con.prepareStatement(insertQuery);

            // Bind parameters
            pstmt.setInt(1, currentClass);
            pstmt.setString(2, categoryName);
            pstmt.setInt(3, weight);

            // Execute the insert
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Category added successfully!");
            } else {
                System.out.println("Failed to add the category.");
            }
        } catch (Exception e) {
            System.out.println("Error during category addition: " + e.getMessage());
        }
    }

    /**
     * Displays all categories for the currently selected class.
     * 
     * @param con the database connection
     */
    public static void showCategories(Connection con) {
        if (currentClass == -1) {
            System.out.println("You need to select a class before showing categories. Use `select-class`.");
            return;
        }

        String query = "SELECT * FROM Category WHERE classID = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, currentClass); // Use the currentClass as the filter

            ResultSet rs = pstmt.executeQuery();

            // Check if categories exist for the selected class
            boolean hasCategories = false;
            System.out.println("-------------------------------------------");
            System.out.println("Categories for ClassID: " + currentClass);
            while (rs.next()) {
                hasCategories = true;
                System.out.println("Name: " + rs.getString("categoryType"));
                System.out.println("Weight: " + rs.getInt("weight"));
                System.out.println("-------------------------------------------");
            }

            if (!hasCategories) {
                System.out.println("No categories found for the selected class.");
            }
        } catch (Exception e) {
            System.out.println("Error during show-categories: " + e.getMessage());
        }
    }

    /**
     * Adds a new assignment to the selected class.
     * Command format: add-assignment <name> <category> "<description>" <points>
     * 
     * @param commandLine the user input command
     * @param con the database connection
     */
    public static void addAssignment(String commandLine, Connection con) {
        if (currentClass == -1) {
            System.out.println("You need to select a class before adding an assignment. Use `select-class`.");
            return;
        }

        // Use a regex to extract arguments for quoted argument
        String regex = "^add-assignment\\s+(\\S+)\\s+(\\S+)\\s+\"([^\"]+)\"\\s+(\\d+)$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(commandLine);

        if (!matcher.matches()) {
            System.out.println("Error: Invalid command format.");
            System.out.println("Usage: add-assignment <Name> <Category> \"<Description>\" <Points>");
            return;
        }

        String assignmentName = matcher.group(1); // First argument: assignment name
        String categoryName = matcher.group(2); // Second argument: category name
        String description = matcher.group(3); // Quoted description
        int points;

        try {
            points = Integer.parseInt(matcher.group(4)); // Points
        } catch (NumberFormatException e) {
            System.out.println("Error: Points must be an integer.");
            return;
        }

        try {
            // Validate that the category exists for the current class
            String categoryQuery = "SELECT * FROM Category WHERE classID = ? AND categoryType = ?";
            PreparedStatement categoryStmt = con.prepareStatement(categoryQuery);
            categoryStmt.setInt(1, currentClass);
            categoryStmt.setString(2, categoryName);
            ResultSet categoryRs = categoryStmt.executeQuery();

            if (!categoryRs.next()) {
                System.out.println("Error: Category '" + categoryName + "' does not exist in the current class.");
                return;
            }

            // Insert the new assignment into the Assignment table
            String insertQuery = "INSERT INTO Assignment (classID, categoryType, name, description, points) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(insertQuery);

            pstmt.setInt(1, currentClass);
            pstmt.setString(2, categoryName); 
            pstmt.setString(3, assignmentName);
            pstmt.setString(4, description);
            pstmt.setInt(5, points);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Assignment '" + assignmentName + "' added successfully!");
            } else {
                System.out.println("Failed to add the assignment.");
            }
        } catch (Exception e) {
            System.out.println("Error during assignment addition: " + e.getMessage());
        }
    }

    /**
     * Displays all assignments for the selected class, grouped by category.
     * 
     * @param con the database connection
     */
    public static void showAssignments(Connection con) {
        if (currentClass == -1) {
            System.out.println("You need to select a class before showing assignments. Use `select-class`.");
            return;
        }

        String query = "SELECT A.name AS AssignmentName, A.description, A.points, C.categoryType AS CategoryName " +
                "FROM Assignment A " +
                "JOIN Category C ON A.classID = C.classID AND A.categoryType = C.categoryType " +
                "WHERE A.classID = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, currentClass);

            ResultSet rs = pstmt.executeQuery();

            // Check if assignments exist for the selected class
            boolean hasAssignments = false;
            System.out.println("-------------------------------------------");
            System.out.println("Assignments for ClassID: " + currentClass);

            while (rs.next()) {
                hasAssignments = true;
                System.out.println("Category: " + rs.getString("CategoryName"));
                System.out.println("Assignment Name: " + rs.getString("AssignmentName"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Points: " + rs.getInt("points"));
                System.out.println("-------------------------------------------");
            }

            if (!hasAssignments) {
                System.out.println("No assignments found for the selected class.");
            }
        } catch (Exception e) {
            System.out.println("Error during show-assignments: " + e.getMessage());
        }
    }

    /**
     * Adds a new student and enrolls them in the selected class.
     * Command format: add-student <username> <studentID> <lastName> <firstName>
     * 
     * @param commandLine the user input command
     * @param con the database connection
     */
    public static void addStudent(String commandLine, Connection con) {
        if (currentClass == -1) {
            System.out.println("You need to select a class before adding a student. Use `select-class`.");
            return;
        }

        String[] args = commandLine.split(" ", 5);

        if (args.length != 5) {
            System.out.println("Error: Invalid arguments for `add-student`.");
            System.out.println("Usage: add-student <username> <studentID> <Last> <First>");
            return;
        }

        String username = args[1];
        int studentID = Integer.parseInt(args[2]);
        String lastName = args[3];
        String firstName = args[4];

        try {
            String selectQuery = "SELECT studentID, firstname, lastname FROM Student WHERE username = ?";
            PreparedStatement selectPstmt = con.prepareStatement(selectQuery);
            selectPstmt.setString(1, username);

            ResultSet rs = selectPstmt.executeQuery();

            if (rs.next()) {
                // Student exists, check if name needs to be updated
                int existingStudentID = rs.getInt("studentID");
                String existingFirstName = rs.getString("firstname");
                String existingLastName = rs.getString("lastname");

                if (existingStudentID != studentID) {
                    System.out.println("Error: The provided studentID does not match the existing student record.");
                    return;
                }

                if (!existingFirstName.equals(firstName) || !existingLastName.equals(lastName)) {
                    String updateQuery = "UPDATE Student SET firstname = ?, lastname = ? WHERE username = ?";
                    PreparedStatement updatePstmt = con.prepareStatement(updateQuery);
                    updatePstmt.setString(1, firstName);
                    updatePstmt.setString(2, lastName);
                    updatePstmt.setString(3, username);
                    updatePstmt.executeUpdate();
                    System.out.println("Warning: Name updated for existing student.");
                }
            } else {
                // Add new student
                String insertQuery = "INSERT INTO Student (studentID, username, firstname, lastname) VALUES (?, ?, ?, ?)";
                PreparedStatement insertPstmt = con.prepareStatement(insertQuery);
                insertPstmt.setInt(1, studentID);
                insertPstmt.setString(2, username);
                insertPstmt.setString(3, firstName);
                insertPstmt.setString(4, lastName);
                insertPstmt.executeUpdate();
                System.out.println("New student added successfully.");
            }

            // Enroll the student in the class
            enrollStudent(studentID, con);
        } catch (Exception e) {
            System.out.println("Error during add-student: " + e.getMessage());
        }
    }

    /**
     * Enrolls a student in the currently selected class.
     * Ensures the student is only enrolled once in the class.
     * 
     * @param studentID the ID of the student to enroll
     * @param con the database connection
     */
    private static void enrollStudent(int studentID, Connection con) {
        try {
            String enrollQuery = "INSERT IGNORE INTO Enroll (studentID, classID) VALUES (?, ?)";
            PreparedStatement pstmt = con.prepareStatement(enrollQuery);
            pstmt.setInt(1, studentID);
            pstmt.setInt(2, currentClass);
            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Student enrolled successfully.");
            } else {
                System.out.println("Student is already enrolled in this class.");
            }
        } catch (Exception e) {
            System.out.println("Error during enrollment: " + e.getMessage());
        }
    }

    /**
     * Displays all students enrolled in the selected class.
     * Optionally filters students based on a search string.
     * 
     * @param commandLine the user input command
     * @param con the database connection
     */
    public static void showStudents(String commandLine, Connection con) {
        if (currentClass == -1) {
            System.out.println("You need to select a class before showing students. Use `select-class`.");
            return;
        }

        String[] args = commandLine.split(" ", 2);
        String query;
        try {
            if (args.length == 1) {
                // Show all students in the current class
                query = "SELECT S.studentID, S.username, S.firstname, S.lastname " +
                        "FROM Student S " +
                        "JOIN Enroll E ON S.studentID = E.studentID " +
                        "WHERE E.classID = ?";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setInt(1, currentClass);

                ResultSet rs = pstmt.executeQuery();
                displayStudentResults(rs);
            } else if (args.length == 2) {
                // Show students matching the search string
                String filter = "%" + args[1].toLowerCase() + "%";
                query = "SELECT S.studentID, S.username, S.firstname, S.lastname " +
                        "FROM Student S " +
                        "JOIN Enroll E ON S.studentID = E.studentID " +
                        "WHERE E.classID = ? AND " +
                        "(LOWER(S.username) LIKE ? OR LOWER(S.firstname) LIKE ? OR LOWER(S.lastname) LIKE ?)";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setInt(1, currentClass);
                pstmt.setString(2, filter);
                pstmt.setString(3, filter);
                pstmt.setString(4, filter);

                ResultSet rs = pstmt.executeQuery();
                displayStudentResults(rs);
            } else {
                System.out.println("Error: Invalid arguments for `show-students`.");
            }
        } catch (Exception e) {
            System.out.println("Error during show-students: " + e.getMessage());
        }
    }
    /**
     * Displays the results of a query that retrieves student information.
     * Outputs student details such as ID, username, first name, and last name.
     * If no students are found, it indicates the absence of results.
     * 
     * @param rs the ResultSet containing student information
     * @throws Exception if an error occurs while processing the ResultSet
     */
    private static void displayStudentResults(ResultSet rs) throws Exception {
        boolean hasStudents = false;
        System.out.println("-------------------------------------------");
        System.out.println("Students for ClassID: " + currentClass);

        while (rs.next()) {
            hasStudents = true;
            System.out.println("StudentID: " + rs.getInt("studentID"));
            System.out.println("Username: " + rs.getString("username"));
            System.out.println("First Name: " + rs.getString("firstname"));
            System.out.println("Last Name: " + rs.getString("lastname"));
            System.out.println("-------------------------------------------");
        }

        if (!hasStudents) {
            System.out.println("No students found.");
        }
    }

    /**
     * Records a grade for a specific assignment for a student.
     * Command format: grade <assignmentName> <username> <grade>
     * 
     * @param commandLine the user input command
     * @param con the database connection
     */
    public static void grade(String commandLine, Connection con) {
        if (currentClass == -1) {
            System.out.println("You need to select a class before grading. Use `select-class`.");
            return;
        }

        String[] args = commandLine.split(" ", 4);

        if (args.length != 4) {
            System.out.println("Error: Invalid arguments for `grade`.");
            System.out.println("Usage: grade <assignmentName> <username> <grade>");
            return;
        }

        String assignmentName = args[1];
        String username = args[2];
        int gradeValue = Integer.parseInt(args[3]);

        try {
            // Validate assignment
            String assignmentQuery = "SELECT assignmentID, points FROM Assignment WHERE classID = ? AND name = ?";
            PreparedStatement assignmentStmt = con.prepareStatement(assignmentQuery);
            assignmentStmt.setInt(1, currentClass);
            assignmentStmt.setString(2, assignmentName);

            ResultSet assignmentRs = assignmentStmt.executeQuery();
            if (!assignmentRs.next()) {
                System.out.println("Error: Assignment '" + assignmentName + "' does not exist.");
                return;
            }

            int assignmentID = assignmentRs.getInt("assignmentID");
            int maxPoints = assignmentRs.getInt("points");

            if (gradeValue > maxPoints) {
                System.out.println("Warning: Grade exceeds maximum points (" + maxPoints + ").");
            }

            // Validate student
            String studentQuery = "SELECT studentID FROM Student WHERE username = ?";
            PreparedStatement studentStmt = con.prepareStatement(studentQuery);
            studentStmt.setString(1, username);

            ResultSet studentRs = studentStmt.executeQuery();
            if (!studentRs.next()) {
                System.out.println("Error: Student with username '" + username + "' does not exist.");
                return;
            }

            int studentID = studentRs.getInt("studentID");

            // Insert or update grade
            String gradeQuery = "INSERT INTO Grade (studentID, assignmentID, grade) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE grade = ?";
            PreparedStatement gradeStmt = con.prepareStatement(gradeQuery);
            gradeStmt.setInt(1, studentID);
            gradeStmt.setInt(2, assignmentID);
            gradeStmt.setInt(3, gradeValue);
            gradeStmt.setInt(4, gradeValue);

            gradeStmt.executeUpdate();
            System.out.println("Grade recorded successfully.");
        } catch (Exception e) {
            System.out.println("Error during grading: " + e.getMessage());
        }
    }

    /**
     * Displays grades for a specific student in the selected class.
     * Command format: student-grades <username>
     * 
     * @param commandLine the user input command
     * @param con the database connection
     */
    public static void studentGrades(String commandLine, Connection con) {
        if (currentClass == -1) {
            System.out.println("You need to select a class before showing student grades. Use `select-class`.");
            return;
        }

        String[] args = commandLine.split(" ", 2);
        if (args.length != 2) {
            System.out.println("Error: Invalid arguments for `student-grades`.");
            System.out.println("Usage: student-grades <username>");
            return;
        }

        String username = args[1];

        try {
            // Validate student
            String studentQuery = "SELECT studentID FROM Student WHERE username = ?";
            PreparedStatement studentStmt = con.prepareStatement(studentQuery);
            studentStmt.setString(1, username);

            ResultSet studentRs = studentStmt.executeQuery();
            if (!studentRs.next()) {
                System.out.println("Error: Student with username '" + username + "' does not exist.");
                return;
            }

            int studentID = studentRs.getInt("studentID");

            // Query grades by category
            String gradesQuery = "SELECT C.categoryType, A.name AS assignmentName, A.points AS maxPoints, " +
                    "IFNULL(G.grade, 0) AS grade " +
                    "FROM Assignment A " +
                    "JOIN Category C ON A.categoryType = C.categoryType AND A.classID = C.classID " +
                    "LEFT JOIN Grade G ON A.assignmentID = G.assignmentID AND G.studentID = ? " +
                    "WHERE A.classID = ? " +
                    "ORDER BY C.categoryType, A.name";
            PreparedStatement gradesStmt = con.prepareStatement(gradesQuery);
            gradesStmt.setInt(1, studentID);
            gradesStmt.setInt(2, currentClass);

            ResultSet gradesRs = gradesStmt.executeQuery();

            double totalGradePoints = 0.0;
            double totalMaxPoints = 0.0;
            double attemptedGradePoints = 0.0;
            double attemptedMaxPoints = 0.0;

            String currentCategory = null;

            System.out.println("-------------------------------------------");
            System.out.println("Grades for Student: " + username);

            while (gradesRs.next()) {
                String category = gradesRs.getString("categoryType");
                String assignmentName = gradesRs.getString("assignmentName");
                int grade = gradesRs.getInt("grade");
                int maxPoints = gradesRs.getInt("maxPoints");

                if (!category.equals(currentCategory)) {
                    currentCategory = category;
                    System.out.println("Category: " + currentCategory);
                }

                System.out.println(" - " + assignmentName + ": " + grade + "/" + maxPoints);
                totalGradePoints += grade;
                totalMaxPoints += maxPoints;

                if (grade > 0) {
                    attemptedGradePoints += grade;
                    attemptedMaxPoints += maxPoints;
                }
            }

            double totalPercentage = (totalGradePoints / totalMaxPoints) * 100.0;
            double attemptedPercentage = (attemptedGradePoints / attemptedMaxPoints) * 100.0;

            System.out.printf("Overall Grade: %.2f%%\n", totalPercentage);
            System.out.printf("Attempted Grade: %.2f%%\n", attemptedPercentage);
            System.out.println("-------------------------------------------");
        } catch (Exception e) {
            System.out.println("Error during student grades retrieval: " + e.getMessage());
        }
    }

    /**
     * Displays the gradebook for the selected class, showing grades for all students.
     * 
     * @param con the database connection
     */
    public static void gradebook(Connection con) {
        if (currentClass == -1) {
            System.out.println("You need to select a class before showing the gradebook. Use `select-class`.");
            return;
        }

        try {
            String gradebookQuery = "SELECT S.username, S.studentID, S.firstname, S.lastname, " +
                    "A.name AS assignmentName, A.points AS maxPoints, IFNULL(G.grade, 0) AS grade " +
                    "FROM Student S " +
                    "JOIN Enroll E ON S.studentID = E.studentID " +
                    "LEFT JOIN Grade G ON S.studentID = G.studentID " +
                    "LEFT JOIN Assignment A ON G.assignmentID = A.assignmentID " +
                    "WHERE E.classID = ? " +
                    "ORDER BY S.studentID, A.name";
            PreparedStatement gradebookStmt = con.prepareStatement(gradebookQuery);
            gradebookStmt.setInt(1, currentClass);

            ResultSet gradebookRs = gradebookStmt.executeQuery();

            String currentStudent = null;
            int studentID = 0;
            String firstName = null;
            String lastName = null;

            double totalGradePoints = 0.0;
            double totalMaxPoints = 0.0;
            double attemptedGradePoints = 0.0;
            double attemptedMaxPoints = 0.0;

            System.out.println("-------------------------------------------");

            while (gradebookRs.next()) {
                String username = gradebookRs.getString("username");
                int grade = gradebookRs.getInt("grade");
                int maxPoints = gradebookRs.getInt("maxPoints");

                if (!username.equals(currentStudent)) {
                    if (currentStudent != null) {
                        double totalPercentage = (totalMaxPoints > 0) ? (totalGradePoints / totalMaxPoints) * 100.0 : 0;
                        double attemptedPercentage = (attemptedMaxPoints > 0)
                                ? (attemptedGradePoints / attemptedMaxPoints) * 100.0
                                : 0;

                        System.out.printf("Student: %s | Total Grade: %.2f%% | Attempted Grade: %.2f%%\n",
                                currentStudent, totalPercentage, attemptedPercentage);
                    }

                    currentStudent = username;
                    studentID = gradebookRs.getInt("studentID");
                    firstName = gradebookRs.getString("firstname");
                    lastName = gradebookRs.getString("lastname");

                    totalGradePoints = 0.0;
                    totalMaxPoints = 0.0;
                    attemptedGradePoints = 0.0;
                    attemptedMaxPoints = 0.0;

                    System.out.printf("Username: %s | StudentID: %d\n", username, studentID);
                    System.out.printf("Name: %s %s\n", firstName, lastName);
                }

                totalGradePoints += grade;
                totalMaxPoints += maxPoints;

                if (grade > 0) {
                    attemptedGradePoints += grade;
                    attemptedMaxPoints += maxPoints;
                }
            }

            // Print the last student's grades
            if (currentStudent != null) {
                double totalPercentage = (totalMaxPoints > 0) ? (totalGradePoints / totalMaxPoints) * 100.0 : 0;
                double attemptedPercentage = (attemptedMaxPoints > 0)
                        ? (attemptedGradePoints / attemptedMaxPoints) * 100.0
                        : 0;

                System.out.printf("Student: %s | Total Grade: %.2f%% | Attempted Grade: %.2f%%\n",
                        currentStudent, totalPercentage, attemptedPercentage);
            }

            System.out.println("-------------------------------------------");
        } catch (Exception e) {
            System.out.println("Error during gradebook retrieval: " + e.getMessage());
        }
    }

    /**
     * Displays a help menu with available commands and their formats.
     */
    public static void help() {
        System.out.println("-------------------------------------------\n"
                + "Available Commands:\n"
                + "-------------------------------------------\n"
                + "Class Management:\n"
                + "  Create a class:\n"
                + "    new-class <courseNumber> <term> <sectionNumber> <description>\n"
                + "    Example: new-class CS410 Sp20 1 Databases\n\n"
                + "  Select a class:\n"
                + "    select-class <courseNumber> [term] [sectionNumber]\n"
                + "    Example: select-class CS410 Sp20 1\n\n"
                + "  Show current class:\n"
                + "    show-class\n\n"
                + "Categories and Assignments:\n"
                + "  Show class categories:\n"
                + "    show-categories\n\n"
                + "  Show assignments in a class:\n"
                + "    show-assignment\n\n"
                + "  Add an assignment:\n"
                + "    add-assignment <name> <category> \"<description>\" <points>\n"
                + "    Example: add-assignment HW1 homework \"Complete all exercises\" 100\n\n"
                + "Student Management:\n"
                + "  Add a student and enroll them:\n"
                + "    add-student <username> <studentID> <lastName> <firstName>\n"
                + "    Example: add-student TonyB 0 Beak Tony\n\n"
                + "  Show students in the current class:\n"
                + "    show-students\n\n"
                + "  Search students by name:\n"
                + "    show-students <string>\n"
                + "    Example: show-students Tony\n\n"
                + "Grading:\n"
                + "  Grade an assignment for a student:\n"
                + "    grade <assignmentName> <username> <grade>\n"
                + "    Example: grade HW1 TonyB 95\n\n"
                + "  Show grades for a specific student:\n"
                + "    student-grades <username>\n"
                + "    Example: student-grades TonyB\n\n"
                + "  Show the gradebook for the current class:\n"
                + "    gradebook\n"
                + "-------------------------------------------");
    }
}