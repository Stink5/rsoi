package stink5.oauth2.lab1.server;

import static java.lang.String.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.api.ContentResponse;

public class PrivateServlet extends AuthHttpClientServlet {

    @Override
    protected void doGet(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) throws ServletException, IOException {
        final String url = getUrl();

        log(format("Get user info:%n\t%s%n", url));
        log(format("Try to get token"));
        // получаем токен из настроек
        final String token = this.auth.getAccessToken();
        // если токена в настройках нет, то
        if (token == null) {
            log("Token is not found. Try get it.");
            // перенаправляем запрос на oauth2-сервер
            resp.sendRedirect(getAuthorizeUrl());
        } else {
            log(format("Token is found: %s.", token));
            try {
                final ContentResponse answer = this.client.newRequest(getUrl())
                    .header("Authorization", "Bearer " + token)
                    .header("Accept","application/vnd.heroku+json; version=3")
                    .send();

                final String content = answer.getContentAsString();
                log("Content: " + content);

                resp.getWriter().append(content);
            } catch (
                InterruptedException
                | TimeoutException
                | ExecutionException e
            ) {
                throw new ServletException(e.getMessage(), e);
            }
        }
    }

    private String getAuthorizeUrl() {
        return format(getInitParameter(AUTHORIZE_URL_PARAM), this.auth.getClientId());
    }

}
