package edu.sdsu.its.Blackboard.Models;

import lombok.Data;

/**
 * @author Tom Paulus
 * Created on 12/30/17.
 */
@Data
public class CourseUser {
    private String userId;
    private String courseId;
    private String childCourseId;
    private String courseRoleId;
}
