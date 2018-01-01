package edu.sdsu.its.Blackboard;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.sdsu.its.Blackboard.Models.Course;
import edu.sdsu.its.Blackboard.Models.CourseUser;
import lombok.extern.log4j.Log4j;

import java.util.*;

import static edu.sdsu.its.Blackboard.Auth.BB_URL;

/**
 * @author Tom Paulus
 * Created on 12/29/17.
 */
@Log4j
public class Courses {
    /**
     * Get all Courses from Bb Learn. Requires No specific permissions.
     *
     * @param limit Maximum number of courses to get from API
     * @return {@link Course[]} Array of all Courses
     */
    public static Course[] getAllCourses(int limit) {
        return getAllCourses(0, limit, true);
    }

    private static Course[] getAllCourses(int offset, int limit, boolean retry) {
        String endpoint = "/learn/api/public/v1/courses?offset=" + offset;
        int page = 0;
        List<Course> courseList = new ArrayList<>();

        try {
            while (endpoint != null && (limit == 0 || courseList.size() < limit)) {
                log.debug(String.format("Requesting page %d of Courses", ++page));
                final HttpResponse httpResponse = Unirest.get(BB_URL + endpoint)
                        .header("Authorization", "Bearer " + Auth.getToken())
                        .asString();

                if (httpResponse.getStatus() / 100 != 2) {
                    log.error("Request to get Courses returned status - " + httpResponse.getStatus());
                    if (retry) {
                        log.debug("Retrying with a new Token");
                        Auth.resetToken();
                        return getAllCourses(offset, limit, false);
                    }

                    return null;
                } else
                    log.debug("Request to get Courses returned status - " + httpResponse.getStatus());

                Gson gson = new Gson();
                CourseListResponsePayload payload = gson.fromJson(httpResponse.getBody().toString(), CourseListResponsePayload.class);
                courseList.addAll(Arrays.asList(payload.results));

                log.info(String.format("Retrieved Courses Page %d", page));
                endpoint = payload.paging != null ? payload.paging.get("nextPage") : null;
            }
        } catch (UnirestException e) {
            log.warn("Problem getting course list", e);
        }

        return courseList.toArray(new Course[courseList.size()]);
    }

    /**
     * Get all enrolled users in a Blackboard Course. Requires "course.configure-properties.EXECUTE" permission.
     *
     * @param courseId {@link String} Course ID
     * @return {@link CourseUser[]} Course Users
     */
    public static CourseUser[] getUsersInCourse(final String courseId) {
        return getUsersInCourse(courseId, 0, true);
    }

    private static CourseUser[] getUsersInCourse(final String courseId, int offset, boolean retry) {
        String endpoint = "/learn/api/public/v1/courses/courseId:" + courseId + "/users?offset=" + offset;
        int page = 0;
        List<CourseUser> userList = new ArrayList<>();

        try {
            while (endpoint != null) {
                log.debug(String.format("Requesting page %d of Course Enrollments for Course %s", ++page, courseId));
                final HttpResponse httpResponse = Unirest.get(BB_URL + endpoint)
                        .header("Authorization", "Bearer " + Auth.getToken())
                        .asString();

                if (httpResponse.getStatus() / 100 != 2) {
                    log.error("Request to get Course Enrollments returned status - " + httpResponse.getStatus());
                    if (retry) {
                        log.debug("Retrying with a new Token");
                        Auth.resetToken();
                        return getUsersInCourse(courseId, offset, false);
                    }

                    return null;
                } else
                    log.debug("Request to get Courses returned status - " + httpResponse.getStatus());

                Gson gson = new Gson();
                EnrollmentsResponsePayload payload = gson.fromJson(httpResponse.getBody().toString(), EnrollmentsResponsePayload.class);
                userList.addAll(Arrays.asList(payload.results));

                log.info(String.format("Retrieved Course Enrollments Page %d", page));
                endpoint = payload.paging != null ? payload.paging.get("nextPage") : null;
            }
        } catch (UnirestException e) {
            log.warn("Problem getting course list", e);
        }

        return userList.toArray(new CourseUser[userList.size()]);
    }

    private static class CourseListResponsePayload {
        Course[] results;
        Map<String, String> paging;
    }

    private static class EnrollmentsResponsePayload {
        CourseUser[] results;
        Map<String, String> paging;
    }
}
