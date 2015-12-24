package stink5.oauth2.lab2.security;

import static java.util.Objects.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

public class OAuthAuthenticationFilter extends GenericFilterBean {

    private final TokenAuthenticationService tokenAuthenticationService;

    @Autowired
    protected OAuthAuthenticationFilter(final TokenAuthenticationService service) {
        this.tokenAuthenticationService = requireNonNull(service);
    }

    @Override
    public void doFilter(
        final ServletRequest req,
        final ServletResponse res,
        final FilterChain chain
    ) throws IOException, ServletException {
        final Authentication authentification = (
            this.tokenAuthenticationService.getAuthentication((HttpServletRequest) req)
        );
        if (authentification != null) {
            SecurityContextHolder.getContext().setAuthentication(authentification);
        }
        chain.doFilter(req, res); // always continue
    }

}