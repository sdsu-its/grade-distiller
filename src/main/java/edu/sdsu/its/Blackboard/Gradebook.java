package edu.sdsu.its.Blackboard;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.sdsu.its.Blackboard.Models.Column;
import edu.sdsu.its.Blackboard.Models.Grade;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static edu.sdsu.its.Blackboard.Auth.BB_URL;

/**
 * @author Tom Paulus
 * Created on 12/29/17.
 */
@Log4j
public class Gradebook {
    /**
     * Get all Grade Center Columns from a Given Course. Requires "course.gradebook.MODIFY" permission.
     *
     * @param courseId {@link String} Bb Course ID
     * @return {@link Column[]} All Gradebook Columns
     */
    public static Column[] getColumns(final String courseId) {
        String endpoint = "/learn/api/public/v1/courses/courseId:" + courseId + "/gradebook/columns";
        int page = 0;
        List<Column> columnList = new ArrayList<>();

        try {
            while (endpoint != null) {
                log.debug(String.format("Requesting page %d of Columns in Course %s", ++page, courseId));
                final HttpResponse httpResponse = Unirest.get(BB_URL + endpoint)
                        .header("Authorization", "Bearer " + Auth.getToken())
                        .asString();

                if (httpResponse.getStatus() / 100 != 2) {
                    log.error("Request to get Columns returned status - " + httpResponse.getStatus());
                    return null;

                } else
                    log.debug("Request to get Columns returned status - " + httpResponse.getStatus());

                Gson gson = new Gson();
                ColumnResponsePayload payload = gson.fromJson(httpResponse.getBody().toString(), ColumnResponsePayload.class);
                columnList.addAll(Arrays.asList(payload.results));

                log.info(String.format("Retrieved Column Page %d", page));
                endpoint = payload.paging != null ? payload.paging.get("nextPage") : null;
            }
        } catch (UnirestException e) {
            log.warn(String.format("Problem getting gradebook columns in Course %s", courseId), e);
        }

        return columnList.toArray(new Column[columnList.size()]);
    }

    /**
     * Get all grades for a given gradebook column. Requires "course.gradebook.MODIFY" permission.
     *
     * @param courseId {@link String} Bb Course ID
     * @param columnId {@link String} Bb Column ID
     * @return {@link Grade[]} All grades for the given column
     */
    public static Grade[] getScore(final String courseId, final String columnId) {
        String endpoint = "/learn/api/public/v1/courses/courseId:" + courseId + "/gradebook/columns/" + columnId + "/users";
        int page = 0;
        List<Grade> gradeList = new ArrayList<>();

        try {
            while (endpoint != null) {
                log.debug(String.format("Requesting page %d of Scores in Course %s for Column %s", ++page, courseId, columnId));
                final HttpResponse httpResponse = Unirest.get(BB_URL + endpoint)
                        .header("Authorization", "Bearer " + Auth.getToken())
                        .asString();

                if (httpResponse.getStatus() / 100 != 2) {
                    log.error("Request to get Score returned status - " + httpResponse.getStatus());
                    return null;

                } else
                    log.debug("Request to get Score returned status - " + httpResponse.getStatus());

                Gson gson = new Gson();
                GradeResponsePayload payload = gson.fromJson(httpResponse.getBody().toString(), GradeResponsePayload.class);
                gradeList.addAll(Arrays.asList(payload.results));

                log.info(String.format("Retrieved Scores Page %d", page));
                endpoint = payload.paging != null ? payload.paging.get("nextPage") : null;
            }
        } catch (UnirestException e) {
            log.warn(String.format("Problem getting scores Course %s for Column %s", courseId, columnId), e);
        }

        return gradeList.toArray(new Grade[gradeList.size()]);
    }

    /**
     * Get a given user's grades for a given gradebook column. Requires "course.gradebook.MODIFY" permission.
     *
     * @param courseId {@link String} Bb Course ID
     * @param columnId {@link String} Bb Column ID
     * @param userId   {@link String} Bb User ID
     * @return {@link Grade} The given user's grade for the particular assignment
     */
    public static Grade getUserScore(final String courseId, final String columnId, final String userId) {
        String endpoint = "/learn/api/public/v1/courses/courseId:" + courseId + "/gradebook/columns/" + columnId + "/users/" + userId;
        Grade grade = null;

        try {
            log.debug(String.format("Requesting score for user %s in Course %s for Column %s", userId, courseId, columnId));
            final HttpResponse httpResponse = Unirest.get(BB_URL + endpoint)
                    .header("Authorization", "Bearer " + Auth.getToken())
                    .asString();

            if (httpResponse.getStatus() == 404) {
                log.debug(String.format("No grade has been populated for User %s in Course %s for Column %s - " +
                        "Saving score as 0", userId, courseId, columnId));
                grade = Grade.builder()
                        .userId(userId)
                        .columnId(columnId)
                        .score(0d)
                        .status("Not Graded")
                        .build();
            } else if (httpResponse.getStatus() / 100 != 2) {
                log.error("Request to get Score returned status - " + httpResponse.getStatus());
                return null;
            } else log.debug("Request to get Score returned status - " + httpResponse.getStatus());

            Gson gson = new Gson();
            grade = gson.fromJson(httpResponse.getBody().toString(), Grade.class);
        } catch (UnirestException e) {
            log.warn(String.format("Problem getting score for User %s in Course %s for Column %s", userId, courseId, columnId), e);
        }

        return grade;
    }

    private static class ColumnResponsePayload {
        Column[] results;
        Map<String, String> paging;
    }

    private static class GradeResponsePayload {
        Grade[] results;
        Map<String, String> paging;
    }
}
