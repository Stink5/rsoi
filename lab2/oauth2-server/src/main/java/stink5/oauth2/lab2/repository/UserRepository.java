package stink5.oauth2.lab2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import stink5.oauth2.lab2.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByClientId(String clientId);

    User findByClientSecret(String clientSecret);

}
