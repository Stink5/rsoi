package stink5.oauth2.lab2.service;

import static java.lang.String.*;
import static java.util.Objects.*;

import static org.springframework.transaction.annotation.Propagation.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stink5.oauth2.lab2.model.Token;
import stink5.oauth2.lab2.repository.TokenRepository;

@Service
@Transactional(
    readOnly = true,
    propagation = REQUIRES_NEW
)
public class TokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    private final ThreadLocal<Random> random = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new SecureRandom();
        }
    };

    private final TokenRepository tokenRepo;

    @Autowired
    public TokenService(final TokenRepository userRepo) {
        this.tokenRepo = requireNonNull(userRepo);
    }

    public Token findByCode(final String code) {
        final Token token = this.tokenRepo.findByCode(code);

        if (token == null) {
            final String message = format("Token not found by code=%s.", code);
            log.error(message);
            throw new RuntimeException(message);
        }

        return token;
    }

    @Transactional(readOnly=false)
    public Token generateNewTokenFor(final String clientId) {
        final Random random = this.random.get();
        final BigInteger code = BigInteger.probablePrime(80, random);
        final BigInteger accessToken = BigInteger.probablePrime(80, random);
        final BigInteger refreshToken = BigInteger.probablePrime(80, random);

        final Token result = new Token();
        result.setClientId(clientId);
        result.setCode(code.toString(16));
        result.setAccessToken(accessToken.toString(16));
//        result.setAccessToken("test-token");
        result.setRefreshToken(refreshToken.toString(16));

        return this.tokenRepo.saveAndFlush(result);
    }

    public Token findByClientIdAndCode(final String clientId, final String code) {
        return this.tokenRepo.findByClientIdAndCode(clientId, code);
    }
}
