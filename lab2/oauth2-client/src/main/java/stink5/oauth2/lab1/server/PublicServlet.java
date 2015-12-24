package stink5.oauth2.lab1.server;

import static java.lang.String.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.IO;

public class PublicServlet extends HttpClientServlet {

    @Override
    protected void doGet(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) throws ServletException, IOException {
        final String url = getUrl();
        // шлём запрос на публичный url без авторизации
        log(format("Get repository list:%n\t%s%n", url));

        try {
            final ContentResponse answer = this.client
                .newRequest(url)
                .header("Accept","application/vnd.heroku+json; version=3")
                .send();
            IO.copy(
                new StringReader(answer.getContentAsString()),
                resp.getWriter()
            );
        } catch (
            InterruptedException
            | ExecutionException
            | TimeoutException e
        ) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

}
