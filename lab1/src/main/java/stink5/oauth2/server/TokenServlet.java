package stink5.oauth2.server;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.api.ContentResponse;

import com.google.gson.Gson;

public class TokenServlet extends AuthHttpClientServlet {

    @Override
    protected void doGet(
        final HttpServletRequest req,
        final HttpServletResponse resp
    ) throws ServletException, IOException {
        final String code = req.getParameter("code");
        if (code == null || code.isEmpty()) {
            throw new ServletException("missing required param 'code'");
        }

        try {
            final ContentResponse answer = (
                this.client
                    .POST(getAccessTokenUrl())
                        .param("grant_type", "authorization_code")
                        .param("client_secret", this.auth.getClientSecret())
                        .param("code", code)
                        .send()
            );

            final String content = answer.getContentAsString();
            log("Content: " + content);
            final Map<String, Object> tokens = (
                new Gson().fromJson(content, Map.class)
            );

            final int status = answer.getStatus();
            if (status != HttpServletResponse.SC_OK) {
                resp.sendError(status, (String)tokens.get("message"));
            } else {
                // сохраняем в настройках
                this.auth.setAccessToken((String) tokens.get("access_token"));
//                this.auth.setRefreshToken((String) tokens.get("refresh_token"));

                // перенаправляемся на получение контента авторизованного контента
                resp.sendRedirect("/private");
            }
        } catch (
            InterruptedException
            | TimeoutException
            | ExecutionException e
        ) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    private String getAccessTokenUrl() {
        return getInitParameter(TOKEN_URL_PARAM);
    }

}
