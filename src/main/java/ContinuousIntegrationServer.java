import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

class GitDownload {
    public static void main(String[] args) throws InvalidRemoteException, TransportException, GitAPIException {
        Git.cloneRepository()
                .setURI("https://github.com/group19-se24/assignment-2.git")
                .setDirectory(new File("temp/repository"))
                .call();
    }


    static void gitClone() throws InvalidRemoteException, TransportException, GitAPIException {
        Git.cloneRepository()
                .setURI("https://github.com/group19-se24/assignment-2.git")
                .setDirectory(new File("temp/repository"))
                .call();
        System.out.println("REPO DOWNLOADED");
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

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        try {
            GitDownload.gitClone();
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        }
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