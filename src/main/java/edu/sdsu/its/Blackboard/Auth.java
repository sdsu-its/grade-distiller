package edu.sdsu.its.Blackboard;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.log4j.Log4j;

/**
 * Authenticate with the Blackboard.
 *
 * @author Tom Paulus
 * Created on 1/27/17.
 */
@Log4j
public class Auth {
    static final String BB_URL = System.getenv("BB_URL");
    private static final String BB_API_KEY = System.getenv("BB_KEY");
    private static final String BB_API_SECRET = System.getenv("BB_SECRET");

    private static String token = null;

    private static void BbAuthenticate() throws UnirestException {
        HttpResponse<String> httpResponse = Unirest.post(
                BB_URL + "/learn/api/public/v1/oauth2/token")
                .queryString("grant_type", "client_credentials")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .basicAuth(BB_API_KEY, BB_API_SECRET)
                .asString();

        if (httpResponse.getStatus() / 100 != 2) {
            log.fatal("Problem Authenticating with Learn Server", new Exception(httpResponse.getBody()));
            return;
        }

        Gson gson = new Gson();
        AuthPayload payload = gson.fromJson(httpResponse.getBody(), AuthPayload.class);
        log.debug("Received token from LEARN Server - " + payload.access_token);
        token = payload.access_token;
    }

    public static String getToken() {
        try {
            if (token == null) BbAuthenticate();
        } catch (UnirestException e) {
            log.error("Problem Authenticating with Learn Server", e);
        }
        return token;
    }

    public static String resetToken() {
        token = null;
        return getToken();
    }

    /**
     * Models Blackboard Authentication Payload
     *
     * @author Tom Paulus
     * Created on 1/27/17.
     */
    private static class AuthPayload {
        public String access_token;
        public String token_type;
        public int expires_in;
    }

}
