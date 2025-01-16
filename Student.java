package prjct;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Student extends Prsn {
    protected String password;
    private ArrayList<String> assignedCourses;

    public Student(String id, String name, String email,String password) {
        super(id, name, email);
        this.assignedCourses = new ArrayList<>();
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    // Getter for assignedCourses
    public ArrayList<String> getAssignedCourses() {
        return new ArrayList<>(assignedCourses); // Return a copy of the list to maintain encapsulation
    }

    // Assign a course to the student
    public void assignCourse(String courseName) {
        if (!assignedCourses.contains(courseName)) {
            assignedCourses.add(courseName);
            saveStudentData();
        } else {
            System.out.println("Course " + courseName + " is already assigned to the student.");
        }
    }

    // View Profile Implementation
    @Override
    public void viewProfile() {
        System.out.println("Student Profile:");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Assigned Courses: ");
        if (assignedCourses.isEmpty()) {
            System.out.println("No courses assigned.");
        } else {
            for (String course : assignedCourses) {
                System.out.println("- " + course);
            }
        }
    }

    // Save student data to a file
    private void saveStudentData() {
        try (FileWriter writer = new FileWriter("students.txt", false)) { // Overwrite mode
            writer.write("Id : " + id + " - " +"Name : " + name + " - " +"Email : " + email + " - " +"Password : " + password + "\n");
            writer.write("Assigned Courses:\n");
            for (String course : assignedCourses) {
                writer.write("- " + course + "\n");
            }
            writer.write("\n"); // Separate each student's data with a blank line
        } catch (IOException e) {
            System.out.println("Error saving student data: " + e.getMessage());
        }
    }


    // Load student data from the file
    public static Student loadStudentData(String studentId, String inputPassword) {
        File file = new File("students.txt");
        if (!file.exists()) {
            System.out.println("No student data file found.");
            return null;
        }

        try (Scanner scanner = new Scanner(file)) {
            boolean studentFound = false;
            String name = null;
            String email = null;
            String password = null;
            ArrayList<String> courses = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith(studentId)) {
                    String[] parts = line.split(" - ");
                    if (parts.length == 4) {
                        name = parts[1];
                        email = parts[2];
                        password = parts[3];
                        studentFound = true;
                    }
                } else if (studentFound && line.startsWith("- ")) {
                    courses.add(line.substring(2));
                } else if (studentFound && line.isEmpty()) {
                    break; // End of this student's data
                }
            }

            if (studentFound && name != null && email != null && password != null) {
                if (password.equals(inputPassword)) {
                    Student student = new Student(studentId, name, email, password);
                    student.assignedCourses.addAll(courses);
                    return student;
                } else {
                    System.out.println("Invalid password. Access denied.");
                }
            } else {
                System.out.println("Student not found or incomplete data.");
            }
        } catch (IOException e) {
            System.out.println("Error loading student data: " + e.getMessage());
        }

        return null;
    }


    @Override
    public String toString() {
        return "Student ID: " + id + ", Name: " + name + ", Email: " + email + ", Assigned Courses: " + assignedCourses;
    }
}
