package prjct;

import java.util.Scanner;

public class Authenticator {
    public static void main(String[] args) {
        Department de = new Department();
        Scanner input = new Scanner(System.in);
        boolean isrunning = true;

        while (isrunning) {
            System.out.println("\n===== Main Portal =====");
            System.out.println("\n===== Select in which portal you want to Interact =====");
            System.out.println("1. Department Portal");
            System.out.println("2. Student Portal");
            System.out.println("3. Faculty Portal");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = input.nextInt();
            input.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    departmentPortal(de, input);
                    break;

                case 2:
                    studentPortal(de, input);
                    break;

                case 3:
                    facultyPortal(de, input);
                    break;

                case 4:
                    isrunning = false;
                    System.out.println("Exiting Portal. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        input.close();
    }

    private static void departmentPortal(Department de, Scanner scanner) {
        boolean isrunning = true;

        while (isrunning) {
            System.out.println("\n===== Department Portal =====");
            System.out.println("1. Add Students");
            System.out.println("2. Add Faculty");
            System.out.println("3. View Students");
            System.out.println("4. Add New Course");
            System.out.println("5. View Department Details");
            System.out.println("6. Assign Students to Course");
            System.out.println("7. Increase Course Seat Size");
            System.out.println("8. Log out");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    de.addStudents();
                    break;

                case 2:
                    de.addFaculty();
                    break;

                case 3:
                    de.viewStudents();
                    break;

                case 4:
                    System.out.print("Enter course name: ");
                    String courseName = scanner.nextLine();
                    System.out.print("Enter seat capacity for the course: ");
                    int seatCapacity = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    de.addNewCourse(courseName, seatCapacity);
                    break;
                case 5:
                    de.viewDepartmentDetails();
                    break;
                case 6:
                    de.assignCoursesToStudents();
                    break;
                case 7:
                    de.increaseCourseSeatSize();
                    break;
                case 8:
                    isrunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void studentPortal(Department de, Scanner scanner) {
        System.out.print("\n===== Student Portal =====");
        System.out.print("\nEnter your Student ID to log in: ");
        String studentId = scanner.nextLine();

        Student student = de.findStudentById(studentId);
        if (student == null) {
            System.out.println("Invalid Student ID. Please try again.");
            return;
        }
        // Check if the student has set a password
        if (student.password == null || student.password.isEmpty()) {
            System.out.println("You need to set a password for your account.");
            System.out.print("Enter your new password: ");
            String newPassword = scanner.nextLine();
            student.setPassword(newPassword);
            de.saveStudents(); // Save the updated student data, including the password
            System.out.println("Password set successfully! Please log in again.");
            return;
        }
        // Prompt for password
        System.out.print("Enter your password: ");
        String enteredPassword = scanner.nextLine();

        if (!student.password.equals(enteredPassword)) {
            System.out.println("Incorrect password. Access denied.");
            return;
        }

        System.out.println("Login successful. Welcome, " + student.name + "!");
        boolean isrunning = true;

        while (isrunning) {
            System.out.println("\n===== Student Portal =====");
            System.out.println("1. View Profile");
            System.out.println("2. View Assigned Courses");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    student.viewProfile();
                    break;
                case 2:
                    System.out.println("Assigned Courses:");
                    for (String course : student.getAssignedCourses()) {
                        System.out.println("- " + course);
                    }
                    break;
                case 3:
                    isrunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    private static void facultyPortal(Department de, Scanner scanner) {

        Scanner input = new Scanner(System.in);
        System.out.print("\nEnter your Faculty Email to log in: ");
        String facultyEmail = input.nextLine();
        Faculty faculty = de.findFacultyByEmail(facultyEmail);
        if (faculty == null) {
            System.out.println("Invalid Faculty Email. Please try again.");
            return;
        }

        // Successful login
        System.out.println("Welcome to the portal, " + faculty.name + "!");
        // Add additional functionality for logged-in faculty, e.g., view courses or update profile

        boolean isrunning = true;

        while (isrunning) {
            System.out.println("\n===== Faculty Portal =====");
            System.out.println("1. View Faculty Details");
            System.out.println("2. Log out");
            System.out.print("Enter your choice: ");

            int choice = input.nextInt();
            input.nextLine(); // Consume newline

            switch (choice) {

                case 1:
                    de.viewFacultyDetails(facultyEmail);
                    break;
                case 2:
                    System.out.println("Logged out Succesfully .....!");
                    isrunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
