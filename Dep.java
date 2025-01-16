package prjct;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Dep {
    private ArrayList<Course> courses;
    private ArrayList<Student> students;
    private ArrayList<Faculty> facultyList;

    public Dep() {
        this.courses = new ArrayList<>();
        this.students = new ArrayList<>();
        this.facultyList = new ArrayList<>();

        loadFaculty(); // Load existing faculty before adding defaults
        loadStudents();
        loadCourses();
        addDefaultCourses(); // Add default courses only if necessary
    }


    private void addDefaultCourses() {
        if (courses.isEmpty()) { // Only add defaults if no courses are already loaded
            // List of default faculty names and their default passwords
            String[][] defaultFacultyInfo = {
                    {"Dr. Smith", "smith123"},
                    {"Dr. Johnson", "johnson123"},
                    {"Dr. Williams", "williams123"},
                    {"Dr. Brown", "brown123"}
            };

            // Loop through each default faculty info and check if they already exist
            for (String[] facultyInfo : defaultFacultyInfo) {
                String facultyName = facultyInfo[0];
                String defaultPassword = facultyInfo[1];
                boolean facultyExists = false;

                // Check if the faculty already exists in the faculty list
                for (Faculty faculty : facultyList) {
                    if (faculty.name.equals(facultyName)) {
                        facultyExists = true;
                        break;
                    }
                }

                // If the faculty doesn't exist, create and add them with default password
                if (!facultyExists) {
                    Faculty newFaculty = new Faculty("F" + (facultyList.size() + 1), facultyName, facultyName.toLowerCase().replace(" ", "") + "@university.edu", defaultPassword);  // Set default password);
                    facultyList.add(newFaculty);

                    // Assign default courses to the new faculty based on their name
                    if (facultyName.equals("Dr. Smith")) {
                        newFaculty.assignCourse("CSE110");
                    } else if (facultyName.equals("Dr. Johnson")) {
                        newFaculty.assignCourse("CSE103");
                    }

                    System.out.println("Default faculty created: " + facultyName);
                    System.out.println("Default email: " + newFaculty.email);
                    System.out.println("Default password: " + defaultPassword);
                    System.out.println("--------------------");
                }
            }

            // Add the default courses for each faculty
            Course course1 = new Course("CSE110", 30, "Dr. Smith");
            Course course2 = new Course("CSE103", 40, "Dr. Johnson");
            courses.add(course1);
            courses.add(course2);

            // Save data after adding
            saveCourses();
            saveFaculty();
            System.out.println("Default courses and faculty data saved successfully.");
        }
    }



    public void addFaculty() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Faculty ID: ");
        String facultyId = scanner.nextLine();

        System.out.print("Enter Faculty Name: ");
        String facultyName = scanner.nextLine();

        System.out.print("Enter Faculty Email: ");
        String facultyEmail = scanner.nextLine();

        Faculty newFaculty = new Faculty(facultyId, facultyName, facultyEmail,"");
        facultyList.add(newFaculty);
        saveFaculty();
        System.out.println("Faculty added: " + facultyName);
    }

    public Faculty findFacultyByName(String facultyName) {
        for (Faculty faculty : facultyList) {
            if (faculty.name.equalsIgnoreCase(facultyName)) {
                return faculty;
            }
        }
        return null; // Return null if no matching faculty is found
    }

    public void addStudents() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of students to add: ");

        int numStudents = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        for (int i = 0; i < numStudents; i++) {
            String studentId;
            boolean idExists;
            do {
                System.out.print("Enter student ID (format: 2025-1-60-xx): ");
                studentId = scanner.nextLine();

                // Check if the student ID already exists
                idExists = false;
                for (Student student : students) {
                    if (student.id.equals(studentId)) {
                        idExists = true;
                        System.out.println("Student ID " + studentId + " already exists. Please enter a unique ID.");
                        break;
                    }
                }
            } while (idExists);

            System.out.print("Enter student name: ");
            String studentName = scanner.nextLine();

            // Generate a default password
//            String defaultPassword = studentId + "@123";

            // Add student to list and save to file
            Student newStudent = new Student(studentId, studentName, studentId + "@std.ewubd.edu","");
            students.add(newStudent);
            saveStudents();
            System.out.println("Student added: ID = " + studentId + ", Name = " + studentName);
        }
    }


    public void viewStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
        } else {
            System.out.println("Students:");
            for (Student student : students) {
                System.out.println(student);
            }
        }
    }

    // Method to add a new course with faculty assignment
    public void addNewCourse(String courseName, int seatCapacity) {
        Scanner scanner = new Scanner(System.in);

        if (findCourseByName(courseName) != null) {
            System.out.println("Course with name " + courseName + " already exists.");
            return;
        }

        System.out.print("Enter the faculty name to assign: ");
        String facultyName = scanner.nextLine();

        Faculty faculty = findFacultyByName(facultyName);
        if (faculty == null) {
            System.out.println("Faculty not found. Please add the faculty first.");
            return;
        }

        Course newCourse = new Course(courseName, seatCapacity, faculty.name);
        courses.add(newCourse);
        faculty.assignCourse(courseName);
        try {
            saveCourses();
            saveFaculty();
            System.out.println("Course added and assigned to faculty: '" + courseName + "' -> " + faculty.name);
        } catch (Exception e) {
            System.out.println("An error occurred while saving course or faculty data: " + e.getMessage());
        }
    }

    // Method to view faculty details
    public void viewFacultyDetails(String loggedInEmail) {
        // Find the faculty member based on the logged-in email
        Faculty loggedInFaculty = findFacultyByEmail(loggedInEmail);

        if (loggedInFaculty != null) {
            // Print the details of the logged-in faculty
            loggedInFaculty.viewProfile();
        } else {
            System.out.println("No faculty member found with email: " + loggedInEmail);
        }
    }


    // Method to load faculty from a file
    private void loadFaculty() {
        File file = new File("faculty.txt");
        if (!file.exists()) {
            System.out.println("Faculty file not found. Starting with an empty faculty list.");
            return;
        }
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                String[] parts = line.split(" - ");
                if (parts.length >= 4) {  // Updated to handle password field
                    String facultyId = parts[0].replace("ID : ", "");
                    String facultyName = parts[1].replace("Name : ", "");
                    String facultyEmail = parts[2].replace("Email : ", "");
                    String facultyPassword = parts[3].replace("Password : ", "");

                    Faculty faculty = new Faculty(facultyId, facultyName, facultyEmail, facultyPassword);
                    facultyList.add(faculty);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading faculty: " + e.getMessage());
        }
    }




    // Method to save faculty to a file
    public void saveFaculty() {
        try (FileWriter writer = new FileWriter("faculty.txt")) {
            // Use a set to avoid duplicate faculty records
            for (Faculty faculty : facultyList) {
                // Write each unique faculty entry to the file
                writer.write("ID : " + faculty.id + " - Name : " + faculty.name + " - Email : " + faculty.email + " - Password : " +
                        (faculty.getPassword() != null ? faculty.getPassword() : "") + "\n");
            }
            System.out.println("Faculty data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving faculty: " + e.getMessage());
        }
    }



    public void viewDepartmentDetails() {
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            System.out.println("Courses Offered:");
            for (Course course : courses) {
                course.displayCourseDetails();
            }
        }
    }


    public void assignCoursesToStudents() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the course name to assign: ");
        String courseName = scanner.nextLine();

        Course course = findCourseByName(courseName);
        if (course == null) {
            System.out.println("Course not found: " + courseName);
            return;
        }

        System.out.print("Enter the number of students to assign: ");
        int numStudents = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        for (int i = 0; i < numStudents; i++) {
            System.out.print("Enter student ID: ");
            String studentId = scanner.nextLine();

            Student student = findStudentById(studentId);
            if (student == null) {
                System.out.println("Student not found: " + studentId);
                continue;
            }

            if (course.isFull()) {
                System.out.println("Course " + courseName + " is full. Cannot assign more students.");
                break;
            }

            course.enrollStudent(student.id, student.name,student.email);
            student.assignCourse(courseName);
            saveStudents(); // Save changes to students and courses
        }
        saveCourses();
        System.out.println("Assignment complete.");
    }

    public void increaseCourseSeatSize() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the course name to increase seat size: ");
        String courseName = scanner.nextLine();

        Course course = findCourseByName(courseName);
        if (course == null) {
            System.out.println("Course not found: " + courseName);
            return;
        }

        System.out.print("Enter the additional seat count: ");
        int additionalSeats = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        course.increaseSeatCapacity(additionalSeats);
        saveCourses();
        System.out.println("Seat size increased for course " + courseName);
    }

    public Student findStudentById(String studentId) {
        for (Student student : students) {
            if (student.id.equals(studentId)) {
                return student;
            }
        }
        return null; // Return null if no matching student is found
    }

    public Faculty findFacultyByEmail(String facultyEmail) {
        for (Faculty faculty : facultyList) {
            if (faculty.email.equalsIgnoreCase(facultyEmail)) {
                return faculty;
            }
        }
        return null; // Return null if no matching faculty is found
    }

    public Course findCourseByName(String courseName) {
        for (Course course : courses) {
            if (course.getCourseName().equals(courseName)) {
                return course;
            }
        }
        return null; // Return null if no matching course is found
    }


    public void saveStudents() {
        try (FileWriter writer = new FileWriter("students.txt")) { // Overwrite mode
            for (Student student : students) {
                writer.write(student.id + " - " + student.name + " - " + student.email + " - " + student.password + "\n");
                for (String course : student.getAssignedCourses()) {
                    writer.write(student.id + " - " + course + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving students: " + e.getMessage());
        }
    }



    private void loadStudents() {
        File file = new File("students.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Student currentStudent = null;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length == 4) { // Include password
                    String id = parts[0];
                    String name = parts[1];
                    String email = parts[2];
                    String password = parts[3];
                    currentStudent = new Student(id, name, email, password);
                    students.add(currentStudent);
                } else if (parts.length == 2 && currentStudent != null) {
                    currentStudent.assignCourse(parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading students: " + e.getMessage());
        }
    }


    private void saveCourses() {
        for (Course course : courses) {
            course.saveCourseData();
        }
    }


    private void loadCourses() {
        File courseDir = new File("courses");
        if (!courseDir.exists()) {
            if (courseDir.mkdir()) {
                System.out.println("Created 'courses' directory.");
            } else {
                System.out.println("Failed to create 'courses' directory.");
                return;
            }
        }

        if (courseDir.isDirectory()) {
            File[] courseFiles = courseDir.listFiles();
            if (courseFiles != null && courseFiles.length > 0) {
                for (File courseFile : courseFiles) {
                    Course course = Course.loadCourseFromFile(courseFile.getAbsolutePath());
                    if (course != null) {
                        courses.add(course);
                    }
                }
            }
        }
        else {
            System.out.println("'courses' is not a directory.");
        }
    }



    private void saveStudentCoursesToFile(Student student) {
        File file = new File("students.txt");

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ArrayList<String> existingLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                existingLines.add(line);
            }
            reader.close();

            FileWriter writer = new FileWriter(file, true); // Append mode
            for (String course : student.getAssignedCourses()) {
                String courseLine = student.id + " - " + course;
                if (!existingLines.contains(courseLine)) {
                    writer.write(courseLine + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving student courses: " + e.getMessage());
        }
    }

}
