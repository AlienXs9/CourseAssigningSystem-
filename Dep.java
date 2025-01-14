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
            Faculty faculty1 = new Faculty("F101", "Dr. Smith", "smith@university.edu");
            facultyList.add(faculty1);
            Course course1 = new Course("CSE110", 30, faculty1.name);
            Course course2 = new Course("CSE103", 40, faculty1.name);
            courses.add(course1);
            courses.add(course2);

            saveCourses();
            saveFaculty();

            faculty1.assignCourse(course1.getCourseName());
            faculty1.assignCourse(course2.getCourseName());
//            System.out.println("Default courses and faculty added.");
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

            // Add student to list and save to file
            Student newStudent = new Student(studentId, studentName, studentId + "@std.ewubd.edu");
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
        saveCourses();
        saveFaculty();
        System.out.println("Course added and assigned to faculty: " + courseName + " -> " + faculty.name);
    }

    // Method to view faculty details
    public void viewFacultyDetails() {
        if (facultyList.isEmpty()) {
            System.out.println("No faculty members found.");
        } else {
            for (Faculty faculty : facultyList) {
                faculty.viewProfile();
            }
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
            while (scanner.hasNextLine()) { // Read lines one by one
                String line = scanner.nextLine().trim();
                String[] parts = line.split(" - ");
                if (parts.length == 3) {
                    Faculty faculty = new Faculty(parts[0], parts[1], parts[2]);
                    facultyList.add(faculty);
                } else {
                    System.out.println("Skipping invalid line in faculty file: " + line);
                }
            }
            System.out.println("Faculty data loaded successfully. Total: " + facultyList.size() + " faculty members.");
        } catch (IOException e) {
            System.out.println("Error loading faculty: " + e.getMessage());
        }
    }


    // Method to save faculty to a file
    private void saveFaculty() {
        try (FileWriter writer = new FileWriter("faculty.txt")) {
            for (Faculty faculty : facultyList) {
                writer.write("ID : " + faculty.id + " - " + "Name : "+ faculty.name + " - " + "Email : " + faculty.email + "\n");
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


    private void saveStudents() {
        try (FileWriter writer = new FileWriter("students.txt")) { // Overwrite mode
            for (Student student : students) {
                writer.write("ID : " + student.id + " - " + "Name : "+ student.name + " - " + "Email : " + student.email + "\n");
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
                if (parts.length == 3) {
                    String id = parts[0];
                    String name = parts[1];
                    String email = parts[2];
                    currentStudent = new Student(id, name, email);
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



    private void saveStudentToFile(String studentId, String studentName) {
        File file = new File("students.txt");
        boolean alreadyExists = false;

        // Check if student already exists in the file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(studentId + " - " + studentName)) {
                    alreadyExists = true;
                    break;
                }
            }
        } catch (IOException e) {
            // If file doesn't exist, continue to save the new student
        }

        if (!alreadyExists) {
            try (FileWriter writer = new FileWriter(file, true)) { // Append mode
                writer.write(studentId + " - " + studentName + " - " + studentId + "@std.ewubd.edu\n");
            } catch (IOException e) {
                System.out.println("Error saving student: " + e.getMessage());
            }
        }
    }

    private void saveStudentCoursesToFile(Student student) {
        File file = new File("students.txt");

        try {
            // Read existing lines into a Set to prevent duplicates
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ArrayList<String> existingLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                existingLines.add(line);
            }
            reader.close();

            // Append only new course assignments
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
