package prjct;

import java.util.ArrayList;

public class Faculty extends Person {
    public ArrayList<String> assignedCourses;
    public Faculty(String id, String name, String email) {
        super(id, name, email );
        this.assignedCourses = new ArrayList<>();
    }

    public void assignCourse(String courseName) {
        if (!assignedCourses.contains(courseName)) {
            assignedCourses.add(courseName);
        }
    }

    @Override
    public void viewProfile() {
        System.out.println("Faculty Profile:");
        System.out.println("ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println();
        if (assignedCourses.isEmpty()) {
            System.out.println("No courses assigned.");
        }
        else {
            System.out.println("Assigned Courses:");
            for (String course : assignedCourses) {
                System.out.println("- " + course);
            }
        }
    }
    public ArrayList<String> getAssignedCourses() {
        return assignedCourses;
    }

}

