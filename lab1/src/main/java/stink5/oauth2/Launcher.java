package stink5.oauth2;

import static java.nio.charset.StandardCharsets.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import stink5.oauth2.server.AuthHttpClientServlet;
import stink5.oauth2.server.HttpClientServlet;
import stink5.oauth2.server.PrivateServlet;
import stink5.oauth2.server.PublicServlet;
import stink5.oauth2.server.TokenServlet;

public class Launcher {

    private static final String PUBLIC_URL = "https://api.heroku.com/schema";
    private static final String PRIVATE_URL = "https://api.heroku.com/account";
    private static final String AUTHORIZE_URL = "https://id.heroku.com/oauth/authorize?client_id=%s&response_type=code";
    private static final String ACCESS_TOKEN_URL = "https://id.heroku.com/oauth/token";

//    private static final String PRIVATE_URL = "https://bitbucket.org/api/2.0/repositories/stink5";
//    private static final String AUTHORIZE_URL = "https://bitbucket.org/site/oauth2/authorize?client_id=%s&response_type=code";
//    private static final String ACCESS_TOKEN_URL = "https://bitbucket.org/site/oauth2/access_token";

    public static void main(final String[] args) throws Exception {
        final Server server = new Server(9000);
        server.setStopAtShutdown(true);

        final HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(createServletContextHandler());
        server.setHandler(handlers);

        server.start();
        server.join();
    }


    private static Handler createServletContextHandler() {
        final ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");

        servletContextHandler.addEventListener(
            new ServletContextListener() {

                @Override
                public void contextInitialized(final ServletContextEvent sce) {
                    sce.getServletContext().setAttribute("AUTH_DATA", authData());
                }

                @Override
                public void contextDestroyed(final ServletContextEvent sce) {
                    sce.getServletContext().removeAttribute("AUTH_DATA");
                }

            }
        );

        servletContextHandler.addServlet(publicHolder(), "/public");
        servletContextHandler.addServlet(privateHolder(), "/private");
        servletContextHandler.addServlet(tokenHolder(), "/token");

        return servletContextHandler;
    }


    private static ServletHolder publicHolder() {
        final ServletHolder holder = new ServletHolder(new PublicServlet());
        holder.setInitParameter(HttpClientServlet.URL_PARAM, PUBLIC_URL);
        return holder;
    }

    private static ServletHolder privateHolder() {
        final ServletHolder holder = new ServletHolder(new PrivateServlet());
        holder.setInitParameter(HttpClientServlet.URL_PARAM, PRIVATE_URL);
        holder.setInitParameter(AuthHttpClientServlet.AUTHORIZE_URL_PARAM, AUTHORIZE_URL);
        holder.setInitParameter(AuthHttpClientServlet.TOKEN_URL_PARAM, ACCESS_TOKEN_URL);
        return holder;
    }

    private static ServletHolder tokenHolder() {
        final ServletHolder holder = new ServletHolder(new TokenServlet());
        holder.setInitParameter(HttpClientServlet.URL_PARAM, PRIVATE_URL);
        holder.setInitParameter(AuthHttpClientServlet.AUTHORIZE_URL_PARAM, AUTHORIZE_URL);
        holder.setInitParameter(AuthHttpClientServlet.TOKEN_URL_PARAM, ACCESS_TOKEN_URL);
        return holder;
    }

    /**
     * Создание объекта, содержащего настройки авторизации
     * @return
     */
    private static AuthData authData() {
        final AuthData result = new AuthData();
        try (
            Reader reader = new InputStreamReader(
                Launcher.class.getResourceAsStream("/oauth2.properties"), UTF_8
            )
        ) {
            result.load(reader);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
