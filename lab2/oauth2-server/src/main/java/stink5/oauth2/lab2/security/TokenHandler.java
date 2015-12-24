package stink5.oauth2.lab2.security;

import static java.util.Objects.*;

import static org.apache.commons.lang3.StringUtils.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import stink5.oauth2.lab2.model.Token;
import stink5.oauth2.lab2.model.User;
import stink5.oauth2.lab2.repository.TokenRepository;
import stink5.oauth2.lab2.repository.UserRepository;

@Component
public final class TokenHandler {

    private static final String AUTH_VALUE_PREFIX = "Bearer ";
    private static final int AUTH_VALUE_PREFIX_LENGTH = AUTH_VALUE_PREFIX.length();

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public TokenHandler(final UserRepository userRepo, final TokenRepository tokenRepo) {
        this.userRepository = requireNonNull(userRepo, "userRepo can't be null");
        this.tokenRepository = requireNonNull(tokenRepo, "tokenRepo can't be null");
    }

    public UserDetails userFromTokenInHeader(final String header) {
        if (
            isNotBlank(header)
            && header.startsWith(AUTH_VALUE_PREFIX)
        ) {
            final String accessToken = header.substring(AUTH_VALUE_PREFIX_LENGTH);
            final Token token = this.tokenRepository.findByAccessToken(accessToken);
            if (token != null) {
                final User userEntity = this.userRepository.findByClientId(token.getClientId());
                if (userEntity != null) {
                    return userEntity.getDetails();
                }
            }
        }
        return null;
    }

}
