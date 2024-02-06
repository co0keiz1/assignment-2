import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


class GitDownload {


    static void gitClone() {

        new File("temp/repository");
        System.out.println("REPO DOWNLOADED");
    }
}



/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
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

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-16");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        System.out.println(request.toString());
        System.out.println("HERE:");

        StringBuilder payload = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                payload.append(line).append('\n');
            }
        }

        // Here, you can convert the payload to a JSON object using your preferred JSON library (e.g., Gson, Jackson)
        // Example:
        // JsonObject jsonObject = JsonParser.parseString(payload.toString()).getAsJsonObject();
        // System.out.println(jsonObject.toString());

        System.out.println("Payload: " + payload.toString());


        deleteDirectory(new File("temp/repository"));

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(
                new String[]{"git", "clone", "https://github.com/group19-se24/assignment-2.git", "temp/repository"});
        Process process = processBuilder.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }














        System.out.println();
        // 2nd compile the code

        response.getWriter().println("CI job done");
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