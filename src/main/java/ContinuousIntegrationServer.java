import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
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
        // 2nd compile the code

        response.getWriter().println("CI job done");
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