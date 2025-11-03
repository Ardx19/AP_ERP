# AP_ERP

UniversityERP/
├── .gitignore               // Ignores files like .class, .log, and 'config.properties'
├── pom.xml                  // Or build.gradle. Defines dependencies (Java, Swing, MySQL, jBCrypt)
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── edu/
    │   │       └── univ/
    │   │           └── erp/
    │   │               ├── Main.java              // Main entry point. Creates and shows the LoginWindow.
    │   │               │
    │   │               ├── access/
    │   │               │   └── AccessControl.java // Helper: isMaintenanceOn(), checkRolePermission()
    │   │               │
    │   │               ├── auth/
    │   │               │   ├── AuthService.java       // "Brain": login(u, p), logout(), changePassword()
    │   │               │   ├── AuthRepository.java    // "Data": Talks *only* to Auth DB. e.g., getUserByUsername()
    │   │               │   ├── HashingUtil.java       // "Util": hashPassword(), verifyPassword() (uses jBCrypt)
    │   │               │   └── UserSession.java       // Singleton: holds current user's ID and Role.
    │   │               │
    │   │               ├── data/
    │   │               │   ├── DbConnectionManager.java // Manages connection pool (e.g., HikariCP)
    │   │               │   ├── StudentDao.java        // SQL for Students (get, create, etc.)
    │   │               │   ├── InstructorDao.java     // SQL for Instructors
    │   │               │   ├── CourseDao.java         // SQL for Courses
    │   │               │   ├── SectionDao.java        // SQL for Sections
    │   │               │   ├── EnrollmentDao.java     // SQL for Enrollments
    │   │               │   ├── GradeDao.java          // SQL for Grades
    │   │               │   └── SettingsDao.java       // SQL for Maintenance Mode (get/set flag)
    │   │               │
    │   │               ├── domain/
    │   │               │   ├── Student.java           // Data class: studentId, userId, rollNo, ...
    │   │               │   ├── Instructor.java        // Data class
    │   │               │   ├── Course.java            // Data class
    │   │               │   ├── Section.java           // Data class
    │   │               │   ├── Enrollment.java        // Data class
    │   │               │   └── Grade.java             // Data class
    │   │               │
    │   │               ├── service/
    │   │               │   ├── StudentService.java    // "Brain": register(sId, secId), drop(sId, secId)
    │   │               │   ├── InstructorService.java // "Brain": updateGrades(...), computeFinalGrade(...)
    │   │               │   ├── AdminService.java      // "Brain": createStudent(...), createCourse(...)
    │   │               │   └── CatalogService.java    // "Brain": getFullCourseCatalog()
    │   │               │
    │   │               ├── ui/
    │   │               │   ├── LoginWindow.java       // The first screen (JFrame)
    │   │               │   ├── MainDashboard.java     // Main app window (JFrame) after login
    │   │               │   │
    │   │               │   ├── admin/
    │   │               │   │   ├── AdminDashboardPanel.java // Main panel for Admin (JPanel)
    │   │               │   │   ├── UserManagementPanel.java // Panel to add users
    │   │               │   │   └── CourseManagementPanel.java // Panel to add courses/sections
    │   │               │   │
    │   │               │   ├── student/
    │   │               │   │   ├── StudentDashboardPanel.java // Main panel for Student (JPanel)
    │   │               │   │   ├── RegisterCoursePanel.java   // Panel with course catalog table
    │   │               │   │   ├── MyGradesPanel.java         // Panel to view grades
    │   │               │   │   └── MyTimetablePanel.java      // Panel for timetable
    │   │               │   │
    │   │               │   ├── instructor/
    │   │               │   │   ├── InstructorDashboardPanel.java // Main panel for Instructor (JPanel)
    │   │               │   │   └── GradeEntryPanel.java        // Panel to enter grades
    │   │               │   │
    │   │               │   └── common/
    │   │               │       ├── MaintenanceBannerPanel.java // The banner that shows when mode is ON
    │   │               │       └── ViewUtil.java               // Helper for common UI tasks (e.g., show error)
    │   │               │
    │   │               └── util/
    │   │                   ├── CsvExporter.java     // Logic to export data to CSV
    │   │                   └── PdfExporter.java     // Logic to export transcript to PDF
    │   │
    │   └── resources/
    │       ├── config.properties        // DB URL, user, pass for *both* DBs (add this to .gitignore!)
    │       ├── logback.xml              // (Optional) Configuration for logging
    │       └── banner_icon.png          // (Optional) Images for UI
    │
    └── test/
        └── java/
            └── edu/
                └── univ/
                    └── erp/
                        ├── auth/
                        │   └── AuthServiceTest.java    // Test login logic (use Mocks)
                        └── service/
                            └── StudentServiceTest.java // Test registration logic (e.all)
