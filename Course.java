package prjct;
//adwadawdaw
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
        this.facultyName = facultyName; // Initially no faculty assigned
    }

    public Course(String courseName, int seatCapacity) {
        this(courseName, seatCapacity, null); // Default facultyName to null
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
        } else {
            System.out.println("Faculty: Not Assigned");
        }

    }

    public void increaseSeatCapacity(int newCapacity) {
        if (newCapacity > seatCapacity) {
            this.seatCapacity = newCapacity;
        }
    }

    public void saveCourseData() {
        try (FileWriter writer = new FileWriter(courseName + ".txt")) {
            writer.write("Course Name: " + courseName + "\n");
            writer.write("Seat Capacity: " + seatCapacity + "\n");
            writer.write("Enrolled Students: " + enrolledCount + "\n");
            for (int i = 0; i < studentIds.size(); i++) {
                writer.write("Name : " + studentNames.get(i) + " - " + "Id : " + studentIds.get(i) + " - " + "Email : " +  studentEmails.get(i) + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error saving course data: " + e.getMessage());
        }
    }

    public static Course loadCourseFromFile(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            String courseName = null;
            int seatCapacity = 0;
            ArrayList<String> studentIds = new ArrayList<>();
            ArrayList<String> studentNames = new ArrayList<>();
            ArrayList<String> studentEmails = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(": ")) {
                    String[] parts = line.split(": ");
                    if (parts.length == 2) {
                        switch (parts[0].trim()) {
                            case "Course Name":
                                courseName = parts[1].trim();
                                break;
                            case "Seat Capacity":
                                seatCapacity = Integer.parseInt(parts[1].trim());
                                break;
                            default:
                                break;
                        }
                    }
                } else if (line.contains(" - ")) {
                    String[] studentData = line.split(" - ");
                    if (studentData.length == 3) { // Expecting ID, name, and email
                        studentIds.add(studentData[0].trim());
                        studentNames.add(studentData[1].trim());
                        studentEmails.add(studentData[2].trim());
                    }
                }
            }

            if (courseName != null && seatCapacity > 0) {
                Course course = new Course(courseName, seatCapacity, ""); // Faculty can be updated later
                for (int i = 0; i < studentIds.size(); i++) {
                    course.enrollStudent(studentIds.get(i), studentNames.get(i), studentEmails.get(i));
                }
                return course;
            } else {
                System.out.println("Error: Missing course details in file.");
            }
        } catch (IOException e) {
            System.out.println("Error loading course from file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number from file: " + e.getMessage());
        }
        return null;
    }


}
