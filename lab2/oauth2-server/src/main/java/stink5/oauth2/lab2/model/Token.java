package stink5.oauth2.lab2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Token {

    @Id @GeneratedValue
    private long id;

    @Column(
        nullable = false,
        unique = true,
        name = "access_token"
    )
    private String accessToken;

    @Column(
        nullable = false,
        unique = true
    )
    private String code;

    @Column(
        nullable = false,
        name = "client_id"
    )
    private String clientId;

    @Column(
        nullable = false,
        unique = true,
        name = "refresh_token"
    )
    private String refreshToken;

    public long getId() {
        return this.id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(final String token) {
        this.accessToken = token;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public void setRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
