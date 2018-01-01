package edu.sdsu.its.Blackboard;

import lombok.extern.log4j.Log4j;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * @author Tom Paulus
 * Created on 12/29/17.
 */
@Log4j
public class AuthTest {
    @Test
    public void getToken() {
        String token = Auth.getToken();
        assertNotNull("Unable to retrieve token", token);
        assertFalse("Empty Token", token.isEmpty());
        log.info("Token Obtained from Learn Server");
        log.debug("Token: " + token);
    }
}