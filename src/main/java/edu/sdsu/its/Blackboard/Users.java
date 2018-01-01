package edu.sdsu.its.Blackboard;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.sdsu.its.Blackboard.Models.User;
import lombok.extern.log4j.Log4j;

import static edu.sdsu.its.Blackboard.Auth.BB_URL;

/**
 * @author Tom Paulus
 * Created on 12/30/17.
 */
@Log4j
public class Users {
    /**
     * Get a User from Bb based off of their internal ID. Requires at least one of the following permissions:
     * 'system.user.properties.MODIFY', 'system.useradmin.generic.VIEW', or 'system.user.VIEW'
     *
     * @param userId {@link String} User ID
     * @return {@link User} Corresponding User
     */
    public static User getUserById(final String userId) {
        String endpoint = "/learn/api/public/v1/users/" + userId;
        User user = null;

        try {
            log.debug(String.format("Requesting User Profile for user %s", userId));
            final HttpResponse httpResponse = Unirest.get(BB_URL + endpoint)
                    .header("Authorization", "Bearer " + Auth.getToken())
                    .asString();

            if (httpResponse.getStatus() / 100 != 2) {
                log.error("Request to get User returned status - " + httpResponse.getStatus());
                return null;
            } else log.debug("Request to get User returned status - " + httpResponse.getStatus());

            Gson gson = new Gson();
            user = gson.fromJson(httpResponse.getBody().toString(), User.class);
        } catch (UnirestException e) {
            log.warn(String.format("Problem User profile for User %s", userId), e);
        }

        return user;
    }
}
