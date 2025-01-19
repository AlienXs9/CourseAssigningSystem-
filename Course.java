package prjct;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Course {
    private String courseName;
    private int seatCapacity;
    private int enrolledCount;
    private String facultyName;
    private ArrayList<String> studentIds;
    private ArrayList<String> studentNames;
    private ArrayList<String> studentEmails;

    public Course(String courseName, int seatCapacity, String facultyName) {
        this.courseName = courseName;
        this.seatCapacity = seatCapacity;
        this.enrolledCount = 0;
        this.studentIds = new ArrayList<>();
        this.studentNames = new ArrayList<>();
        this.studentEmails = new ArrayList<>();
        this.facultyName = facultyName;
    }

    public Course(String courseName, int seatCapacity) {
        this(courseName, seatCapacity, null);
    }

    public String getCourseName() {
        return courseName;
    }

    public int getSeatCapacity() {
        return seatCapacity;
    }

    public boolean isFull() {
        return enrolledCount >= seatCapacity;
    }

    public void assignFaculty(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void enrollStudent(String studentId, String studentName, String studentEmail) {
        if (!isFull()) {
            studentIds.add(studentId);
            studentNames.add(studentName);
            studentEmails.add(studentEmail);
            enrolledCount++;
        }
    }

    public void displayCourseDetails() {
        System.out.println("Course Name: " + courseName);
        System.out.println("Seats Available: " + (seatCapacity - enrolledCount));
        System.out.println("Enrolled Students: " + enrolledCount);
        if (facultyName != null) {
            System.out.println("Faculty: " + facultyName);
        }
        else {
            System.out.println("Faculty: Not Assigned");
        }

    }

    public void increaseSeatCapacity(int newCapacity) {
        if (newCapacity > seatCapacity) {
            this.seatCapacity = newCapacity;
        }
    }

    public void saveCourseData() {

        File courseDir = new File("courses");
        if (!courseDir.exists()) {
            courseDir.mkdir();
        }
        try (FileWriter writer = new FileWriter("courses/" + courseName + ".txt")) {
            writer.write("Course Name: " + courseName + "\n");
            writer.write("Seat Capacity: " + seatCapacity + "\n");
            writer.write("Enrolled Students: " + enrolledCount + "\n");
            if (facultyName != null) {
                writer.write("Faculty Name: " + facultyName + "\n");
            }
            else {
                writer.write("Faculty Name: Not Assigned\n");
            }
            for (int i = 0; i < studentIds.size(); i++) {
                writer.write("Student - Name: " + studentNames.get(i) +
                        " - ID: " + studentIds.get(i) +
                        " - Email: " + studentEmails.get(i) + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving course data: " + e.getMessage());
        }
    }

    public static Course loadCourseFromFile(String filename) {

        try (Scanner input = new Scanner(new File(filename))) {
            String courseName = null;
            int seatCapacity = 0;
            String facultyName = null;
            ArrayList<String> studentIds = new ArrayList<>();
            ArrayList<String> studentNames = new ArrayList<>();
            ArrayList<String> studentEmails = new ArrayList<>();

            while (input.hasNextLine()) {
                String line = input.nextLine();
                if (line.startsWith("Course Name: "))
                {
                    courseName = line.substring("Course Name: ".length());
                }
                else if (line.startsWith("Seat Capacity: ")) {
                    seatCapacity = Integer.parseInt(line.substring("Seat Capacity: ".length()));
                }
                else if (line.startsWith("Faculty Name: ")) {
                    facultyName = line.substring("Faculty Name: ".length());
                    if (facultyName.equals("Not Assigned")) {
                        facultyName = null;
                    }
                }
                else if (line.startsWith("Student - ")) {
                    String[] parts = line.split(" - ");
                    if (parts.length == 4) {
                        String name = parts[1].substring("Name: ".length());
                        String id = parts[2].substring("ID: ".length());
                        String email = parts[3].substring("Email: ".length());
                        studentNames.add(name);
                        studentIds.add(id);
                        studentEmails.add(email);
                    }
                }
            }
            if (courseName != null && seatCapacity > 0) {
                Course course = new Course(courseName, seatCapacity, facultyName);
                for (int i = 0; i < studentIds.size(); i++) {
                    course.enrollStudent(studentIds.get(i), studentNames.get(i), studentEmails.get(i));
                }
                return course;
            }
        } catch (IOException e) {
            System.out.println("Error loading course from file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number from file: " + e.getMessage());
        }
        return null;
    }

}
