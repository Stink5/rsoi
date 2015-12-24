package stink5.oauth2.lab2.security;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAuthentication implements Authentication {

    private final UserDetails user;
    private boolean authenticated = true;

    public UserAuthentication(final UserDetails user) {
        this.user = requireNonNull(user);
    }

    @Override
    public String getName() {
        return this.user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.user.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return this.user.getPassword();
    }

    @Override
    public UserDetails getDetails() {
        return this.user;
    }

    @Override
    public Object getPrincipal() {
        return this.user;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(final boolean authenticated) {
        this.authenticated = authenticated;
    }

}
