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
        if (courses.isEmpty()) {
            // Create courses directory if it doesn't exist
            File courseDir = new File("courses");
            if (!courseDir.exists()) {
                courseDir.mkdir();
            }

            String[] defaultFacultyNames = {"Dr.Smith", "Dr.Johnson", "Dr.Williams", "Dr.Brown"};

            for (String facultyName : defaultFacultyNames) {
                boolean facultyExists = false;

                for (Faculty faculty : facultyList) {
                    if (faculty.name.equals(facultyName)) {
                        facultyExists = true;
                        break;
                    }
                }

                if (!facultyExists) {
                    Faculty newFaculty = new Faculty("F" + (facultyList.size() + 1),
                            facultyName,
                            facultyName.toLowerCase().replace(" ", "") + "@university.edu");
                    facultyList.add(newFaculty);

                    if (facultyName.equals("Dr.Smith")) {
                        newFaculty.assignCourse("CSE110");
                    } else if (facultyName.equals("Dr.Johnson")) {
                        newFaculty.assignCourse("CSE103");
                    }
                }
            }

            Course course1 = new Course("CSE110", 30, "Dr.Smith");
            Course course2 = new Course("CSE103", 40, "Dr.Johnson");
            courses.add(course1);
            courses.add(course2);

            saveCourses();
            saveFaculty();
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

        Faculty newFaculty = new Faculty(facultyId, facultyName, facultyEmail);
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

        // Create courses directory if it doesn't exist
        File courseDir = new File("courses");
        if (!courseDir.exists()) {
            courseDir.mkdir();
        }

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
    private void loadFaculty()
    {
        File file = new File("faculty.txt");
        if (!file.exists()) {
            System.out.println("Faculty file not found. Starting with an empty faculty list.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            Faculty currentFaculty = null;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("ID : ")) {
                    // Parse faculty basic info
                    String[] parts = line.split(" - ");
                    if (parts.length == 3) {
                        String facultyId = parts[0].replace("ID : ", "");
                        String facultyName = parts[1].replace("Name : ", "");
                        String facultyEmail = parts[2].replace("Email : ", "");

                        currentFaculty = new Faculty(facultyId, facultyName, facultyEmail);
                        facultyList.add(currentFaculty);
                    }
                } else if (line.startsWith("COURSE : ") && currentFaculty != null) {
                    // Parse and add course assignment
                    String[] parts = line.split(" - ");
                    if (parts.length == 2) {
                        String courseName = parts[1];
                        currentFaculty.assignCourse(courseName);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading faculty: " + e.getMessage());
        }
    }


    // Method to save faculty to a file
    private void saveFaculty() {
        try (FileWriter writer = new FileWriter("faculty.txt")) {
            for (Faculty faculty : facultyList) {
                // Save basic faculty info
                writer.write("ID : " + faculty.id + " - Name : " + faculty.name + " - Email : " + faculty.email + "\n");
                // Save assigned courses
                for (String course : faculty.getAssignedCourses()) {
                    writer.write("COURSE : " + faculty.id + " - " + course + "\n");
                }
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
            if (course.getCourseName().equalsIgnoreCase(courseName)) { // Made case-insensitive
                return course;
            }
        }
        return null;
    }


    public void saveStudents() {
        try (PrintWriter writer = new PrintWriter(new File("students.txt"))) {
            for (Student student : students) {
                writer.println(student.id + " - " + student.name + " - " +
                        student.email + " - " + student.password);
                for (String course : student.getAssignedCourses()) {
                    writer.println(student.id + " - " + course);
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving students: " + e.getMessage());
        }
    }




    private void loadStudents() {
        File file = new File("students.txt");
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            Student currentStudent = null;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(" - ");

                if (parts.length == 4) { // New student entry
                    String id = parts[0];
                    String name = parts[1];
                    String email = parts[2];
                    String password = parts[3];
                    currentStudent = new Student(id, name, email, password);
                    students.add(currentStudent);
                } else if (parts.length == 2 && currentStudent != null) { // Course entry
                    currentStudent.assignCourse(parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading students: " + e.getMessage());
        }
    }



    private void saveCourses() {
        // Create courses directory if it doesn't exist
        File courseDir = new File("courses");
        if (!courseDir.exists()) {
            courseDir.mkdir();
        }

        for (Course course : courses) {
            course.saveCourseData();
        }
    }


    private void loadCourses() {
        courses.clear(); // Clear existing courses before loading

        File courseDir = new File("courses");
        if (!courseDir.exists()) {
            courseDir.mkdir();
            return;
        }

        File[] courseFiles = courseDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (courseFiles != null) {
            for (File courseFile : courseFiles) {
                Course course = Course.loadCourseFromFile(courseFile.getAbsolutePath());
                if (course != null) {
                    courses.add(course);
                }
            }
        }
    }



    private void saveStudentCoursesToFile(Student student) {
        File file = new File("students.txt");

        try {
            Scanner scanner = new Scanner(file);
            ArrayList<String> existingLines = new ArrayList<>();

            while (scanner.hasNextLine()) {
                existingLines.add(scanner.nextLine());
            }
            scanner.close();

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
