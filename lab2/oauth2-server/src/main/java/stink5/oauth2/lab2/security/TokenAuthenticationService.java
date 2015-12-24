package stink5.oauth2.lab2.security;

import static java.util.Objects.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TokenAuthenticationService {

    private final TokenHandler tokenHandler;

    @Autowired
    public TokenAuthenticationService(final TokenHandler tokenHandler) {
        this.tokenHandler = requireNonNull(tokenHandler, "tokenHandler can't be null");
    }

    public UserDetails getUserDetails(final HttpServletRequest request) {
        return this.tokenHandler.userFromTokenInHeader(
            request.getHeader(HttpHeaders.AUTHORIZATION)
        );
    }

    public Authentication getAuthentication(final HttpServletRequest request) {
        final UserDetails user = getUserDetails(request);
        return user == null ? null : new UserAuthentication(user);
    }

}
