package edu.sdsu.its.Blackboard.Models;

import lombok.Data;

import java.util.Map;

/**
 * Models a User for the Blackboard Learn API.
 *
 * @author Tom Paulus
 *         Created on 1/27/17.
 */
@Data
public class User {
    private String id;
    private String studentId;
    private String userName;
    private String externalId;

    private Map<String, String> availability;
    private Map<String, String> name;
    private Map<String, String> job;
    private Map<String, String> contact;
}
