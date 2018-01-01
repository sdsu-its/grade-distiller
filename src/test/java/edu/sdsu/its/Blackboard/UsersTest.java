package edu.sdsu.its.Blackboard;

import edu.sdsu.its.Blackboard.Models.CourseUser;
import edu.sdsu.its.Blackboard.Models.User;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Tom Paulus
 * Created on 12/30/17.
 */
public class UsersTest {
    private static final String TEST_COURSE_ID = "PSY101-01-Spring2017";

    @Test
    public void getUserById() {
        CourseUser testUser = Courses.getUsersInCourse(TEST_COURSE_ID)[0];
        User user = Users.getUserById(testUser.getUserId());
        assertNotNull(user);
        assertNotNull(user.getUserName());
    }
}