package prjct;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Student extends Person {
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

    public ArrayList<String> getAssignedCourses() {
        return new ArrayList<>(assignedCourses);
    }

    public void assignCourse(String courseName) {
        if (!assignedCourses.contains(courseName)) {
            assignedCourses.add(courseName);
            saveStudentData();
        }
        else {
            System.out.println("Course " + courseName + " is already assigned to the student.");
        }
    }

    @Override
    public void viewProfile() {
        System.out.println("Student Profile:");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);

    }

    private void saveStudentData() {
        try (FileWriter writer = new FileWriter("students.txt", true)) {
            writer.write("Id : " + id + " - " +"Name : " + name + " - " +"Email : " + email + " - " +"Password : " + password + "\n");
            writer.write("Assigned Courses:\n");
            for (String course : assignedCourses) {
                writer.write("- " + course + "\n");
            }
            writer.write("\n");
        } catch (IOException e) {
            System.out.println("Error saving student data: " + e.getMessage());
        }
    }

    public static Student loadStudentData(String studentId, String inputPassword) {
        File file = new File("students.txt");
        if (!file.exists()) {
            System.out.println("No student data file found.");
            return null;
        }
        try (Scanner input = new Scanner(file)) {
            boolean studentFound = false;
            String name = null;
            String email = null;
            String password = null;
            ArrayList<String> courses = new ArrayList<>();

            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.startsWith(studentId)) {
                    String[] parts = line.split(" - ");
                    if (parts.length == 4) {
                        name = parts[1];
                        email = parts[2];
                        password = parts[3];
                        studentFound = true;
                    }
                }
                else if (studentFound && line.startsWith("- ")) {
                    courses.add(line.substring(2));
                }
                else if (studentFound && line.isEmpty()) {
                    break;
                }
            }
            if (studentFound && name != null && email != null && password != null) {
                if (password.equals(inputPassword)) {
                    Student student = new Student(studentId, name, email, password);
                    student.assignedCourses.addAll(courses);
                    return student;
                }
                else {
                    System.out.println("Invalid password. Access denied.");
                }
            }
            else {
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
