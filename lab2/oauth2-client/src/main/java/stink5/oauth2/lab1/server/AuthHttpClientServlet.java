package stink5.oauth2.lab1.server;

import javax.servlet.ServletException;

import stink5.oauth2.lab1.AuthData;

public abstract class AuthHttpClientServlet extends HttpClientServlet {

    public static final String AUTHORIZE_URL_PARAM = "authUrl";
    public static final String TOKEN_URL_PARAM = "tokenUrl";

    protected AuthData auth;

    @Override
    public void init() throws ServletException {
        final Object auth = getServletContext().getAttribute("AUTH_DATA");
        if (!(auth instanceof AuthData)) {
            throw new ServletException("auth data is not defined");
        }
        this.auth = (AuthData) auth;
    }

    @Override
    protected String getUrl() {
        return getInitParameter(URL_PARAM);
    }

}
