package prjct;

import java.util.Scanner;

public class Authenticator {
    public static void main(String[] args) {
        Department de = new Department();
        Scanner input = new Scanner(System.in);
        boolean isrunning = true;

        while (isrunning) {
            System.out.println("======================================");
            System.out.println("============= EWU PORTAL =============");
            System.out.println("======================================");
            System.out.println("\n===== Login As =====");
            System.out.println("1. Department ");
            System.out.println("2. Student ");
            System.out.println("3. Faculty ");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = input.nextInt();
            input.nextLine();
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

    private static void departmentPortal(Department de, Scanner input) {
        boolean isrunning = true;

        while (isrunning) {
            System.out.println("\n===== Department Portal =====");
            System.out.println("Welcome Admin !");
            System.out.println("1. Add Students");
            System.out.println("2. Add Faculty");
            System.out.println("3. View Students");
            System.out.println("4. Add New Course");
            System.out.println("5. View Department Details");
            System.out.println("6. Assign Students to Course");
            System.out.println("7. Increase Course Seat Size");
            System.out.println("8. Log out");
            System.out.print("Enter your choice: ");

            int choice = input.nextInt();
            input.nextLine();
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
                    String courseName = input.nextLine();
                    System.out.print("Enter seat capacity for the course: ");
                    int seatCapacity = input.nextInt();
                    input.nextLine();
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

    private static void studentPortal(Department de, Scanner input) {

        System.out.print("\n===== Student Portal =====");
        System.out.print("\nEnter your Student ID to log in: ");
        String studentId = input.nextLine();

        Student student = de.findStudentById(studentId);
        if (student == null) {
            System.out.println("Invalid Student ID. Please try again.");
            return;
        }
        if (student.password == null || student.password.isEmpty()) {
            System.out.println("Please set a new password for your account.");
            System.out.print("Enter your new password: ");
            String newPassword = input.nextLine();
            student.setPassword(newPassword);
            de.saveStudents();
            System.out.println("Password set successfully! Please log in again...");
            return;
        }
        System.out.print("Enter your password: ");
        String enteredPassword = input.nextLine();
        if (!student.password.equals(enteredPassword)) {
            System.out.println("Incorrect password. Access denied.");
            return;
        }
        System.out.println("Login successful. Welcome to the portal, " + student.name + "!");
        boolean isrunning = true;

        while (isrunning) {
            System.out.println("\n===== Student Portal =====");
            System.out.println("1. View Profile");
            System.out.println("2. View Assigned Courses");
            System.out.println("3. Log out");
            System.out.print("Enter your choice: ");

            int choice = input.nextInt();
            input.nextLine();

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
                    System.out.println("Logged out Succesfully .....!");
                    isrunning = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void facultyPortal(Department de, Scanner input) {

         input = new Scanner(System.in);
        System.out.print("\nEnter your Faculty Email to log in: ");
        String facultyEmail = input.nextLine();
        Faculty faculty = de.findFacultyByEmail(facultyEmail);
        if (faculty == null) {
            System.out.println("Invalid Faculty Email. Please try again.");
            return;
        }
        System.out.println("Welcome to the portal, " + faculty.name + "!");

        boolean isrunning = true;
        while (isrunning) {
            System.out.println("\n===== Faculty Portal =====");
            System.out.println("1. View Faculty Details");
            System.out.println("2. Log out");
            System.out.print("Enter your choice: ");

            int choice = input.nextInt();
            input.nextLine();

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
