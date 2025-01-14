package prjct;

import java.io.*;
import java.util.ArrayList;

public class Student extends Prsn {
    private ArrayList<String> assignedCourses;

    public Student(String id, String name, String email) {
        super(id, name, email);
        this.assignedCourses = new ArrayList<>();
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
        try (FileWriter writer = new FileWriter("students.txt", true)) { // Append mode
            writer.write(id + " - " + name + " - " + email + "\n");
            writer.write("Assigned Courses:\n");
            for (String course : assignedCourses) {
                writer.write("- " + course + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving student data: " + e.getMessage());
        }
    }

    // Load student data from the file
    public static Student loadStudentData(String studentId) {
        File file = new File("students.txt");
        if (!file.exists()) {
            System.out.println("No student data file found.");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean studentFound = false;
            String name = null;
            String email = null;
            ArrayList<String> courses = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith(studentId)) {
                    String[] parts = line.split(" - ");
                    if (parts.length == 3) {
                        name = parts[1];
                        email = parts[2];
                        studentFound = true;
                    }
                } else if (studentFound && line.startsWith("- ")) {
                    courses.add(line.substring(2)); // Remove "- " prefix
                } else if (studentFound && line.isEmpty()) {
                    break; // End of this student's data
                }
            }

            if (studentFound && name != null && email != null) {
                Student student = new Student(studentId, name, email);
                student.assignedCourses.addAll(courses);
                return student;
            }
        } catch (IOException e) {
            System.out.println("Error loading student data: " + e.getMessage());
        }

        return null;
    }




    public String toString() {
        return "Student ID: " + id + ", Name: " + name + ", Email: " + email + ", Assigned Courses: " + assignedCourses;
    }
}
