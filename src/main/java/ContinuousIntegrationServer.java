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
            String compileResult = compileProjectWithMaven("/Users/admin/Documents/assignment-2"); //How to fix global path?
            response.getWriter().println(compileResult);
        }




        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        response.getWriter().println("CI job done");
    }

    private String compileProjectWithMaven(String pathOfProject) {
        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("mvn", "-f", pathOfProject, "clean", "install");  //could be verify or package instead of install but install might be most robust

            pb.directory(new File(pathOfProject));
            Process p = pb.start();

            StringBuilder out = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while ((line = br.readLine()) != null) {
                out.append(line).append("\n");
            }

            if (p.waitFor() == 0) {
                return "Succesfull compile:\n" + out.toString();
            } else {
                return "Failed compile:\n" + out.toString();
            }


        } catch (IOException e) {
            e.printStackTrace();
            return "IOException " + e.getMessage();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "InterruptedException " + e.getMessage();
        }

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