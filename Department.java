package prjct;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Department {
    private ArrayList<Course> courses;
    private ArrayList<Student> students;
    private ArrayList<Faculty> facultyList;

    public Department() {
        this.courses = new ArrayList<>();
        this.students = new ArrayList<>();
        this.facultyList = new ArrayList<>();

        loadFaculty();
        loadStudents();
        loadCourses();
        addDefaultCourses();
    }

    private void addDefaultCourses() {
        if (courses.isEmpty()) {
            File courseDir = new File("courses");
            if (!courseDir.exists()) {
                courseDir.mkdir();
            }

            String[] defaultFacultyNames = {"Aasr","Dmim","Srzn","Imran"};
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
                    if (facultyName.equals("Aasr")) {
                        newFaculty.assignCourse("CSE110");
                    }
                    else if (facultyName.equals("Dmim")) {
                        newFaculty.assignCourse("CSE103");
                    }
                    else if (facultyName.equals("Imran")){
                        newFaculty.assignCourse("CSE103");
                    }
                }
            }
            Course course1 = new Course("CSE110", 30, "Aasr");
            Course course2 = new Course("CSE103", 30, "Dmim");
            Course course3 = new Course("CSE106",30,"Imran");
            courses.add(course1);
            courses.add(course2);
            courses.add(course3);
            saveCourses();
            saveFaculty();
        }
    }

    public void addFaculty() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter Faculty ID: ");
        String facultyId = input.nextLine();
        System.out.print("Enter Faculty Name: ");
        String facultyName = input.nextLine();
        System.out.print("Enter Faculty Email: ");
        String facultyEmail = input.nextLine();
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
        return null;
    }

    public void addStudents() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the number of students to add: ");
        int numStudents = input.nextInt();
        input.nextLine();
        for (int i = 0; i < numStudents; i++) {
            String studentId;
            boolean idExists;
            do {
                System.out.print("Enter student ID (format: 2025-1-60-xx): ");
                studentId = input.nextLine();
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
            String studentName = input.nextLine();
            Student newStudent = new Student(studentId, studentName, studentId + "@std.ewubd.edu","");
            students.add(newStudent);
            saveStudents();
            System.out.println("Student added: ID = " + studentId + ", Name = " + studentName);
        }
    }

    public void viewStudents() {
        if (students.isEmpty()) {
            System.out.println("No students found.");
        }
        else {
            System.out.println("Students:");
            for (Student student : students) {
                System.out.println(student);
            }
        }
    }

    public void addNewCourse(String courseName, int seatCapacity) {
        Scanner input = new Scanner(System.in);
        File courseDir = new File("courses");
        if (!courseDir.exists()) {
            courseDir.mkdir();
        }
        if (findCourseByName(courseName) != null) {
            System.out.println("Course with name " + courseName + " already exists.");
            return;
        }
        System.out.print("Enter the faculty name to assign: ");
        String facultyName = input.nextLine();

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

    public void viewFacultyDetails(String loggedInEmail) {

        Faculty loggedInFaculty = findFacultyByEmail(loggedInEmail);
        if (loggedInFaculty != null) {
            loggedInFaculty.viewProfile();
        }
        else {
            System.out.println("No faculty member found with email: " + loggedInEmail);
        }
    }

    public void viewDepartmentDetails() {
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        }
        else {
            System.out.println("Courses Offered:");
            for (Course course : courses) {
                course.displayCourseDetails();
            }
        }
    }

    public void increaseCourseSeatSize() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the course name to increase seat size: ");
        String courseName = input.nextLine();
        Course course = findCourseByName(courseName);

        if (course == null) {
            System.out.println("Course not found: " + courseName);
            return;
        }
        System.out.print("Enter the additional seat count: ");

        int additionalSeats = input.nextInt();
        input.nextLine();
        course.increaseSeatCapacity(additionalSeats);
        saveCourses();
        System.out.println("Seat size increased for course " + courseName);
    }

    public void assignCoursesToStudents() {

        Scanner input = new Scanner(System.in);
        System.out.print("Enter the course name to assign: ");
        String courseName = input.nextLine();
        Course course = findCourseByName(courseName);
        if (course == null) {
            System.out.println("Course not found: " + courseName);
            return;
        }
        System.out.print("Enter the number of students to assign: ");
        int numStudents = input.nextInt();
        input.nextLine();

        for (int i = 0; i < numStudents; i++) {
            System.out.print("Enter student ID: ");
            String studentId = input.nextLine();
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
            saveStudents();
        }
        saveCourses();
        System.out.println("Assignment complete.");
    }

    public Student findStudentById(String studentId) {
        for (Student student : students) {
            if (student.id.equals(studentId)) {
                return student;
            }
        }
        return null;
    }

    public Faculty findFacultyByEmail(String facultyEmail) {
        for (Faculty faculty : facultyList) {
            if (faculty.email.equalsIgnoreCase(facultyEmail)) {
                return faculty;
            }
        }
        return null;
    }

    public Course findCourseByName(String courseName) {
        for (Course course : courses) {
            if (course.getCourseName().equalsIgnoreCase(courseName)) {
                return course;
            }
        }
        return null;
    }

    private void loadFaculty()
    {
        File file = new File("faculty.txt");
        if (!file.exists()) {
            return;
        }
        try (Scanner input = new Scanner(file)) {
            Faculty currentFaculty = null;
            while (input.hasNextLine()) {
                String line = input.nextLine().trim();
                if (line.startsWith("ID : ")) {
                    String[] parts = line.split(" - ");
                    if (parts.length == 3) {
                        String facultyId = parts[0].replace("ID : ", "");
                        String facultyName = parts[1].replace("Name : ", "");
                        String facultyEmail = parts[2].replace("Email : ", "");
                        currentFaculty = new Faculty(facultyId, facultyName, facultyEmail);
                        facultyList.add(currentFaculty);
                    }
                }
                else if (line.startsWith("COURSE : ") && currentFaculty != null) {
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

    private void saveFaculty() {
        try (FileWriter writer = new FileWriter("faculty.txt")) {
            for (Faculty faculty : facultyList) {
                writer.write("ID : " + faculty.id + " - Name : " + faculty.name + " - Email : " + faculty.email + "\n");
                for (String course : faculty.getAssignedCourses()) {
                    writer.write("COURSE : " + faculty.id + " - " + course + "\n");
                }
            }

        } catch (IOException e) {
            System.out.println("Error saving faculty: " + e.getMessage());
        }
    }

    private void loadStudents() {
        File file = new File("students.txt");
        if (!file.exists()) return;

        try (Scanner input = new Scanner(file)) {
            Student currentStudent = null;

            while (input.hasNextLine()) {
                String line = input.nextLine();
                String[] parts = line.split(" - ");

                if (parts.length == 4) {
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

    private void loadCourses() {
        courses.clear();
        File courseDir = new File("courses");
        if (!courseDir.exists()) {
            courseDir.mkdir();
            return;
        }
        File[] courseFiles = courseDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });
        if (courseFiles != null) {
            for (File courseFile : courseFiles) {
                Course course = Course.loadCourseFromFile(courseFile.getAbsolutePath());
                if (course != null) {
                    courses.add(course);
                }
            }
        }
    }

    private void saveCourses() {
        File courseDir = new File("courses");
        if (!courseDir.exists()) {
            courseDir.mkdir();
        }
        for (Course course : courses) {
            course.saveCourseData();
        }
    }

    private void saveStudentCoursesToFile(Student student) {

        File file = new File("students.txt");
        try {
            Scanner input = new Scanner(file);
            ArrayList<String> existingLines = new ArrayList<>();

            while (input.hasNextLine()) {
                existingLines.add(input.nextLine());
            }
            input.close();
            FileWriter writer = new FileWriter(file, true);
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
