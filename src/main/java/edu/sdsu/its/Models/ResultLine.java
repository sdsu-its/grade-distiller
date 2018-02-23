package edu.sdsu.its.Models;

import edu.sdsu.its.Blackboard.Models.Column;
import edu.sdsu.its.Blackboard.Models.Course;
import edu.sdsu.its.Blackboard.Models.Grade;
import edu.sdsu.its.Blackboard.Models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @author Tom Paulus
 * Created on 12/30/17.
 */
@Data
@AllArgsConstructor
public class ResultLine {
    private User user;
    private Course sourceCourse;
    private Map<Column, Grade> grades;

    @Override
    public String toString() {
        return "ResultLine{" +
                "user=" + user +
                ", sourceCourse=" + sourceCourse +
                ", grades=" + grades.toString() +
                '}';
    }
}
