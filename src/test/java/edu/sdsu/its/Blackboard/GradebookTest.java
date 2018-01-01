package edu.sdsu.its.Blackboard;

import edu.sdsu.its.Blackboard.Models.Column;
import edu.sdsu.its.Blackboard.Models.CourseUser;
import edu.sdsu.its.Blackboard.Models.Grade;
import lombok.extern.log4j.Log4j;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Tom Paulus
 * Created on 12/29/17.
 */
@Log4j
public class GradebookTest {
    private static final String TEST_COURSE = "PSY101-01-Spring2017";

    @Test
    public void getColumns() {
        Column[] columns = Gradebook.getColumns(TEST_COURSE);
        assertNotNull(columns);
        assertTrue(columns.length > 0);

        for (Column column : columns) {
            log.debug(column.toString());
        }
    }

    @Test
    public void getScore() {
        Column[] columns = Gradebook.getColumns(TEST_COURSE);
        assertNotNull(columns);
        assertTrue(columns.length > 0);

        Grade[] grades = Gradebook.getScore(TEST_COURSE, columns[0].getId());
        assertNotNull(grades);

        for (Grade grade : grades) {
            log.debug(grade.toString());

        }
    }

    @Test
    public void getUserScore() {
        final CourseUser[] usersInCourse = Courses.getUsersInCourse(TEST_COURSE);
        CourseUser testUser = usersInCourse[0];
        for (CourseUser user : usersInCourse) {
            if (user.getCourseRoleId().equals("Student")) {
                testUser = user;
                break;
            }
        }

        for (Column column : Objects.requireNonNull(Gradebook.getColumns(TEST_COURSE))) {
            Grade grade = Gradebook.getUserScore(TEST_COURSE, column.getId(), testUser.getUserId());
            assertNotNull(grade);

            log.debug(grade.toString());
        }
    }
}