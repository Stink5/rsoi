package stink5.oauth2.lab2.controller;

import static java.util.Collections.*;
import static java.util.Objects.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriTemplate;

import stink5.oauth2.lab2.model.Token;
import stink5.oauth2.lab2.model.User;
import stink5.oauth2.lab2.service.TokenService;
import stink5.oauth2.lab2.service.UserService;

@Controller
@RequestMapping("/oauth")
public class OAuth2Controller {

    private String codeRedirect;
    private UriTemplate location;

    private final UserService userService;
    private final TokenService tokenService;

    @Autowired
    public OAuth2Controller(final UserService userService, final TokenService tokenService) {
        this.userService = requireNonNull(userService, "userService can't be null");
        this.tokenService = requireNonNull(tokenService, "tokenService can't be null");
    }

    @PostConstruct
    public void init() {
        this.location = new UriTemplate(this.codeRedirect + "?code={0}");
    }

    @Autowired
    public void setCodeRedirect(@Value("${oauth2.redirect}") final String codeRedirect) {
        this.codeRedirect = codeRedirect;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, path = "/authorize")
    public ResponseEntity<Void> authorize(
        @RequestParam("client_id") final String clientId,
        @RequestParam("response_type") final String responseType
    ) {
        if (!"code".equals(responseType)) {
            return ResponseEntity.badRequest().build();
        }

        if (!this.userService.existsWithClientId(clientId)) {
            throw new RuntimeException("User hasn't been found by clientId=" + clientId);
        }

        final Token token = this.tokenService.generateNewTokenFor(clientId);
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(this.location.expand(token.getCode()))
            .build();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/token")
    public ResponseEntity<Map<String, Object>> token(
        @RequestParam("grant_type") final String grantType,
        @RequestParam("client_secret") final String clientSecret,
        @RequestParam("code") final String code
    ) {
        if (!"authorization_code".equals(grantType)) {
            return ResponseEntity.badRequest()
                .body(singletonMap("message", "incorrect grant type: " + grantType));
        }

        final User user = this.userService.findByClientSecret(clientSecret);
        if (user == null) {
            return ResponseEntity.badRequest()
                .body(singletonMap("message", "user has not been found by clientSecret"));
        }

        final Token token = this.tokenService.findByClientIdAndCode(user.getClientId(), code);
        if (token == null) {
            return ResponseEntity.badRequest()
                .body(singletonMap("message", "code '" + code + "' has not been found"));
        }

        final Map<String, Object> result = new HashMap<>();
        result.put("access_token", token.getAccessToken());
        result.put("refresh_token", token.getRefreshToken());
        return ResponseEntity.ok(result);
    }

}
