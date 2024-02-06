import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    Statement stmt;
    Connection conn;

    /**
     * Constructor for the Database class
     * This class is used to create a connection to the database and create a table if it does not exist
     */
    public Database(){
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "");
            stmt = conn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS history");
            stmt.executeUpdate("USE history");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS builds (commit VARCHAR(50) UNIQUE, date DATETIME, log VARCHAR(10000));");
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Method to retrieve the history of builds
     * @return List of strings containing the history of builds
     * @throws SQLException if the query fails
     */
    public List<String[]> getHistory() throws SQLException{
        List<String[]> history = new ArrayList<>();
        String query = "SELECT * FROM builds;";
        ResultSet rs = stmt.executeQuery(query);

        while(rs.next()){
            String[] build = new String[3];
            build[0] = rs.getString("commit");
            build[1] = rs.getString("date");
            build[2] = rs.getString("log");
            history.add(build);
        }
        return history;
    }

    /**
     * Method to retrieve the commit message of a specific commit
     * @param commitId the commit id of the commit
     * @return the log message of the commit
     * @throws SQLException if the query fails
     */
    public String[] getCommit(String commitId) throws SQLException{
        String[] commit = new String[3];
        String query = "SELECT * FROM builds WHERE commit = '" + commitId + "';";
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()){
            commit[0] = rs.getString("commit");
            commit[1] = rs.getString("date");
            commit[2] = rs.getString("log");
            return commit;
        }
        commit[0] = "Commit not found";
        return commit;
    }

    /**
     * Method to add a commit to the database
     *
     * @param commitId the commit id of the commit
     * @param date     the date of the commit
     * @param log      the log message of the commit
     * @throws SQLException if the query fails
     */
    public void addCommit(String commitId, String date, String log) throws SQLException{
        String insert = "INSERT INTO builds (commit, date, log) VALUES ('" + commitId + "', '" + date + "', '" + log + "');";
        stmt.execute(insert);
    }

    /**
     * Method to delete a commit to the database
     * @param commitId the commit id of the commit
     * @throws SQLException if the query fails
     */
    public void deleteCommit(String commitId) throws SQLException{
        String delete = "DELETE FROM builds WHERE commit = '" + commitId + "';";
        stmt.execute(delete);
    }
}
