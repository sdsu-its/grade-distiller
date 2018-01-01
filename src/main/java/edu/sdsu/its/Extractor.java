package edu.sdsu.its;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;
import edu.sdsu.its.Blackboard.Courses;
import edu.sdsu.its.Blackboard.Gradebook;
import edu.sdsu.its.Blackboard.Models.Column;
import edu.sdsu.its.Blackboard.Models.Course;
import edu.sdsu.its.Blackboard.Models.CourseUser;
import edu.sdsu.its.Blackboard.Models.Grade;
import edu.sdsu.its.Blackboard.Users;
import edu.sdsu.its.Models.ResultLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Tom Paulus
 * Created on 12/29/17.
 */
@Log4j
public class Extractor {
    private static final int THREAD_POOL_SIZE = 5;
    private static Gson gson = new Gson();

    private ExecutorService userExecutor; // Used to fetch users from the Learn API
    private ExecutorService gradeExecutor; // Used to fetch grades from the GradeCenter

    private String columnSelector;
    private String courseSelector;
    private Boolean updateCourseList;

    private Set<String> columnNames = new HashSet<>();
    private List<ResultLine> resultLines = new ArrayList<>();

    private Extractor() {
        final ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("Grade-Distiller-Thread-%d")
                .setDaemon(true)
                .build();

        userExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE, threadFactory);
        gradeExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE, threadFactory);
    }

    @Builder
    public Extractor(String columnSelector, String courseSelector, Boolean updateCourseList) {
        this();

        this.columnSelector = columnSelector;
        this.courseSelector = courseSelector;
        this.updateCourseList = updateCourseList;
    }

    private static void updateCourseDB() {
        log.info("Updating Master Course List");
        DB.getInstance().clear();
        log.debug("Current Course DB Cleared.");

        Course[] courses = Courses.getAllCourses(Integer.MAX_VALUE);

        if (courses == null || courses.length == 0) {
            log.error("Problem retrieving course list - aborting");
            throw new RuntimeException("Cannot Retrieve Course List");
        }

        log.info(String.format("Retrieved %d courses from Bb Learn", courses.length));

        for (Course course : courses) {
            DB.getInstance().put(course.getCourseId(), course);
        }

        log.info("Master Course List Updated");
    }

    public void extract(final String outputFilePath) throws InterruptedException, IOException {
        log.info(String.format("Beginning Grade Extraction for Course \"%s\"", courseSelector));

        if (updateCourseList || DB.getInstance().size() == 0) updateCourseDB();

        List<Course> courses = new ArrayList<>();

        for (String course : DB.getInstance().keySet()) {
            if (course.matches(courseSelector))
                courses.add(gson.fromJson(DB.getInstance().get(course), Course.class));
        }

        log.info(String.format("Found %d matching courses", courses.size()));
        if (courses.size() == 0) return;

        for (Course course : courses) {
            log.info(String.format("Matched Course - %s (%s)", course.getCourseId(), course.getName()));

            CourseUser[] courseUsers = Courses.getUsersInCourse(course.getCourseId());
            log.info(String.format("Course has %d enrolled users", courseUsers.length));

            Column[] columns = Gradebook.getColumns(course.getCourseId());
            if (columns == null || columns.length == 0) {
                log.warn(String.format("Course %s has no Grade Center Columns", course.getCourseId()));
                continue;
            }
            log.debug(String.format("Course has %d column in GradeCenter", columns.length));

            List<Column> matchedColumns = new ArrayList<>();
            for (Column column : columns) {
                if (column.getName().matches(columnSelector)) {
                    matchedColumns.add(column);
                    columnNames.add(column.getName());
                }
            }
            log.info(String.format("Course has %d columns that match selector", matchedColumns.size()));
            if (matchedColumns.size() == 0) continue;


            for (CourseUser user : courseUsers) {
                if (user.getCourseRoleId().equals("Student")) {
                    userExecutor.submit(new startResultLine(user, course, matchedColumns));
                }
            }
        }

        userExecutor.shutdown();
        log.info("Waiting for User Fetch to complete");
        userExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        log.info("User Fetch Completed");


        for (ResultLine resultLine : resultLines) {
            for (Column column : resultLine.getGrades().keySet()) {
                gradeExecutor.submit(new getScores(resultLine, column));
            }
        }

        gradeExecutor.shutdown();
        log.info("Waiting for Grade Fetch to complete");
        gradeExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
        log.info("Grade Fetch Completed");

        saveAsCSV(outputFilePath);
    }

    private void saveAsCSV(final String outputPath) throws IOException {
        // Username, Source Course, Columns
        SquareMap map = new SquareMap(2 + columnNames.size(), resultLines.size() + 1);
        String[] columns = columnNames.toArray(new String[columnNames.size()]);

        map.setIndex("Username", 0);
        map.setIndex("Source Course", 1);
        for (int i = 0; i < columns.length; i++) {
            String column = columns[i];
            map.setIndex(column, i + 2); // Username and Course are the first 2
        }
        // Header Row
        for (String key : map.getIndexes().keySet()) {
            map.set(key, 0, key);
        }

        // Data Cells
        for (int i = 1; i <= resultLines.size(); i++) {
            ResultLine line = resultLines.get(i - 1);
            map.set("Username", i, line.getUser().getUserName());
            map.set("Source Course", i, line.getSourceCourse().getCourseId());
            for (Column column : line.getGrades().keySet()) {
                Grade grade = line.getGrades().get(column);
                map.set(column.getName(), i, grade.getScore() != null ? grade.getScore().toString() : "0");
            }
        }

        CSVWriter writer = new CSVWriter(new FileWriter(outputPath));
        for (int i = 0; i < map.getHeight(); i++) {
            writer.writeNext(map.getLine(i));
        }
        writer.close();
    }

    @AllArgsConstructor
    private class startResultLine implements Runnable {
        private CourseUser courseUser;
        private Course course;
        private List<Column> columns;

        public void run() {
            ResultLine line = new ResultLine(
                    Users.getUserById(courseUser.getUserId()),
                    course,
                    new HashMap<Column, Grade>() {{
                        for (Column column : columns) {
                            put(column, null);
                        }
                    }}
            );

            resultLines.add(line);
        }
    }

    @AllArgsConstructor
    private class getScores implements Runnable {
        private ResultLine resultLine;
        private Column column;

        public void run() {
            resultLine.getGrades().put(column,
                    Gradebook.getUserScore(resultLine.getSourceCourse().getCourseId(),
                            column.getId(),
                            resultLine.getUser().getId()));
        }
    }

    private class SquareMap {
        private String[][] grid;
        private Map<String, Integer> indexes;

        SquareMap(int width, int height) {
            //noinspection unchecked
            grid = new String[height][width];
            indexes = new HashMap<>();
        }

        void setIndex(String name, int index) {
            indexes.put(name, index);
        }

        void set(String col, int row, String value) {
            grid[row][indexes.get(col)] = value;
        }

        String[] getLine(int row) {
            return grid[row];
        }

        int getHeight() {
            return grid.length;
        }

        Map<String, Integer> getIndexes() {
            return indexes;
        }
    }

}