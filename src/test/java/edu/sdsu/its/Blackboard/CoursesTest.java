package edu.sdsu.its.Blackboard;

import edu.sdsu.its.Blackboard.Models.Course;
import edu.sdsu.its.Blackboard.Models.CourseUser;
import lombok.extern.log4j.Log4j;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Tom Paulus
 * Created on 12/29/17.
 */
@Log4j
public class CoursesTest {
    private static final String TEST_COURSE_ID = "PSY101-01-Spring2017";

    @Test
    public void getAllCourses() {
        Course[] courses = Courses.getAllCourses(250);
        assertNotNull(courses);
        assertTrue(courses.length > 0);

        for (Course course : courses) {
            log.debug(course.toString());
        }
    }

    @Test
    public void getUsersInCourse() {
        CourseUser[] users = Courses.getUsersInCourse(TEST_COURSE_ID);
        assertNotNull(users);
        assertTrue(users.length > 0);

        for (CourseUser user : users) {
            log.debug(user.toString());
        }
    }
}