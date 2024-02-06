import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {
    private Database db;

    @AfterEach
    void tearDown() throws SQLException {
        db.conn.close();
    }

    @BeforeEach
    void setUp() {
        this.db = new Database();
    }

    /**
     * Test to check if the history is retrieved correctly
     * The connection is null beforehand and it should not be after the method is called
     */
    @Test
    void establishesConnection(){
        db = null;
        db = new Database();
        assertNotNull(db.conn);
    }

    /**
     * Test to check if the history is retrieved correctly
     * Fetches the commit that was just added to see if it in the database
     * @throws SQLException if the query fails
     */
    @Test
    void addsCommit() throws SQLException {
        db.addCommit("58932105892310358", "1111-11-11 11:11:11", "Test commit");
        String[] commit = db.getCommit("58932105892310358");
        assertEquals("Test commit", commit[2]);
        db.deleteCommit("58932105892310358");
    }

    /**
     * Test to check if entry is removed from the database
     * Adds a commit, then removes it and checks if it is still in the database
     * @throws SQLException if the query fails
     */
    @Test
    void removesCommit() throws SQLException {
        db.addCommit("58932105892310358", "1111-11-11 11:11:11", "Test commit");
        String[] commit = db.getCommit("58932105892310358");
        assertEquals("Test commit", commit[2]);
        db.deleteCommit("58932105892310358");
        String[] commit2 = db.getCommit("58932105892310358");
        assertEquals("Commit not found", commit2[0]);
    }


}
