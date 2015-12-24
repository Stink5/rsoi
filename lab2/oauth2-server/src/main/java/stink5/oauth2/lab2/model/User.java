package stink5.oauth2.lab2.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue
    private long id;

    @Column(
        nullable = false,
        unique = true
    )
    private String username;

    @Column(
        nullable = false,
        unique = true
    )
    private String email;

    @Column(
        nullable = false,
        unique = true,
        name = "client_id"
    )
    private String clientId;

    @Column(
        nullable = false,
        unique = true,
        name = "client_secret"
    )
    private String clientSecret;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private AuthDetails details = new AuthDetails();

    public long getId() {
        return this.id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public void setClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public AuthDetails getDetails() {
        return this.details;
    }

    public void setDetails(final AuthDetails details) {
        this.details = details;
    }

}
