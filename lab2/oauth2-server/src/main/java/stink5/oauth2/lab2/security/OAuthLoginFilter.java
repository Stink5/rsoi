package stink5.oauth2.lab2.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class OAuthLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final TokenAuthenticationService tokenAuthenticationService;
    private final ThreadLocal<UserDetails> userDetails = new ThreadLocal<>();

    protected OAuthLoginFilter(
        final String urlMapping,
        final TokenAuthenticationService tokenAuthenticationService,
        final AuthenticationManager authManager
    ) {
        super(new AntPathRequestMatcher(urlMapping));

        this.tokenAuthenticationService = tokenAuthenticationService;
        setAuthenticationManager(authManager);

        setContinueChainBeforeSuccessfulAuthentication(true);
    }

    @Override
    public Authentication attemptAuthentication(
        final HttpServletRequest request,
        final HttpServletResponse response
    ) throws AuthenticationException {
        final UserDetails user = this.tokenAuthenticationService.getUserDetails(request);
        if (user != null) {
            this.userDetails.set(user);
            return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                    user.getUsername(),
                    user.getPassword()
                )
            );
        }
        throw new AuthenticationCredentialsNotFoundException("Credentials has not been found.");
    }

    @Override
    protected void successfulAuthentication(
        final HttpServletRequest request,
        final HttpServletResponse response,
        final FilterChain chain,
        final Authentication authentication
    ) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(
            new UserAuthentication(this.userDetails.get())
        );
    }

}
