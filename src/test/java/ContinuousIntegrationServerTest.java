import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContinuousIntegrationServerTest {

    /**
     * Tests if the correct branch is fetched when the payload contains the branch
     */
    @Test
    void findBranchFromPayloadReturnCorrectBranchWhenValidPayload() {
        String payload = "{\"ref\":\"refs/heads/main\",\"before\":\"0193130b7850924c4b492b87e2d1d043e1dafb75\"}";
        assertEquals("main", ContinuousIntegrationServer.findBranchFromPayload(payload));
    }

    /**
     * Tests if null is returned when the payload does not contain a branch
     */
    @Test
    void findBranchFromPayloadReturnNullBranchWhenInalidPayload() {
        String payload = "{\"ref\":\"refs/head\",\"before\":\"0193130b7850924c4b492b87e2d1d043e1dafb75\"}";
        assertNull( ContinuousIntegrationServer.findBranchFromPayload(payload));
    }

    /**
     * Tests if the correct url is fetched when the payload contains the url
     */
    @Test
    void findGitURLFromPayloadReturnCorrectURLhWhenValidPayload() {
        String payload = " \"git_url\": \"git://github.com/group19-se24/assignment-2.git\",\"ssh_url\": \"git@github.com:group19-se24/assignment-2.git\",\"clone_url\": \"https://github.com/group19-se24/assignment-2.git\"," ;
        assertEquals("https://github.com/group19-se24/assignment-2.git", ContinuousIntegrationServer.findGitURLFromPayload(payload));
    }

    /**
     * Tests if null is returned when the payload contain a valid git url
     */
    @Test
    void findGitURLFromPayloadReturnNullBranchWhenInalidPayload() {
        String payload = " \"git_url\": \"git://github.com/group19-se24/assignment-2.git\",\"ssh_url\": \"git@github.com:group19-se24/assignment-2.git\",\"clone_url\": \"https://github.com/group19-se24/assignment-2\"," ;
        assertNull( ContinuousIntegrationServer.findGitURLFromPayload(payload));
    }

}