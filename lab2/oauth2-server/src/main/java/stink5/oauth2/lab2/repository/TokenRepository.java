package stink5.oauth2.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stink5.oauth2.lab2.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByCode(String code);

    Token findByClientIdAndCode(String clientId, String code);

    Token findByAccessToken(String accessToken);

}
