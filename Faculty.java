package prjct;

import java.util.ArrayList;

public class Faculty extends Prsn {

    private String password;
    private ArrayList<String> assignedCourses; // List of courses assigned to the faculty

    public Faculty(String id, String name, String email,String password) {
        super(id, name, email );
        this.assignedCourses = new ArrayList<>();
        this.password = password;
    }

    // Method to assign a course to the faculty
    public void assignCourse(String courseName) {
        if (!assignedCourses.contains(courseName)) {
            assignedCourses.add(courseName);
        }
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    // Method to view the faculty's profile
    @Override
    public void viewProfile() {
        System.out.println("Faculty Profile:");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        if (assignedCourses.isEmpty()) {
            System.out.println("No courses assigned.");
        } else {
            System.out.println("Assigned Courses:");
            for (String course : assignedCourses) {
                System.out.println("- " + course);
            }
        }
    }

    // Getter for assigned courses
    public ArrayList<String> getAssignedCourses() {
        return assignedCourses;
    }


}

