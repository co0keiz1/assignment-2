import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.io.InputStreamReader;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

class ResultFromCompiling {
    private final int statusCode;
    private final String outp;

    public ResultFromCompiling(int statusCode, String output) {
        this.statusCode = statusCode;
        this.outp = output;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getOutp() {
        return outp;
    }
}

/**
 * Skeleton of a ContinuousIntegrationServer which acts as webhook
 * See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler {
    // https://www.baeldung.com/java-delete-directory
    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    /**
     * Clones the given branch of the given git repo to temp/repository
     *
     * @param url    HTTPS url of a git repo
     * @param branch name of branch that should be cloned
     */
    void downloadRepo(String url, String branch) {
        deleteDirectory(new File("temp/repository"));

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(
                new String[]{"git", "clone", "-b", branch, url, "temp/repository"});
        try {
            Process process = processBuilder.start();
            process.waitFor(); // TODO vi borde kolla på exit-koden för att se ifall den lyckades klona repot
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-16");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        System.out.println(target);
        //System.out.println("TAARGET^^^");
        /**
         * locally compile using Maven #4
         * */
        if ("/compile".equals(target)) {
            ResultFromCompiling result = compileProjectWithMaven("/Users/admin/Documents/assignment-2"); // Adjust the path as needed
            String compilationMessageForDebug = result.getStatusCode() == 0 ? "Compiled succefully:\n" : "Compilation was failed:\n";
            response.getWriter().println(compilationMessageForDebug + result.getOutp());
        }




        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        System.out.println(request.toString());
        System.out.println("HERE:");

        StringBuilder payload = new StringBuilder();
        String line;

        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            payload.append(line).append('\n');
        }


        System.out.println("Payload: " + payload.toString());
        String branch = findBranchFromPayload(payload.toString());
        String gitURL = findGitURLFromPayload(payload.toString());
        System.err.println(branch);
        System.err.println(gitURL);

        if (branch == null || gitURL == null) {
            System.err.println("something went wrong");
            // TODO  #7 return error to Github
        } else
            downloadRepo(gitURL, branch);


        // 2nd compile the code

        response.getWriter().println("CI job done");
    }

    /**
     * Extracts the branch from github webhook payload
     *
     * @param githubPayload a string of the payload
     * @return branch name
     */
    static String findBranchFromPayload(String githubPayload) {
        Pattern pattern = Pattern.compile( "\"ref\":\"refs/heads/([^\"]+)\"");
        Matcher matcher = pattern.matcher(githubPayload);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    /**
     * Extracts the github https url from github webhook payload
     *
     * @param githubPayload a string of the payload
     * @return github https url
     */
    static String findGitURLFromPayload(String githubPayload) {
        Pattern pattern = Pattern.compile("\"clone_url\":\\s*\"(https://github.com/[^\"]+.git)\"");
        Matcher matcher = pattern.matcher(githubPayload);
        if (matcher.find())
            return matcher.group(1);
        return null;

    }

    private ResultFromCompiling compileProjectWithMaven(String pathOfProject) {
        StringBuilder out = new StringBuilder();
        int statusCode = -1;

        try {
            ProcessBuilder pb = new ProcessBuilder("mvn", "-f", pathOfProject, "clean", "install");
            pb.directory(new File(pathOfProject));
            Process p = pb.start();

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                out.append(line).append("\n");
            }


            statusCode = p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            out.append("IOException: ").append(e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            out.append("InterruptedException: ").append(e.getMessage());
        }

        return new ResultFromCompiling(statusCode, out.toString());

    }

        // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}