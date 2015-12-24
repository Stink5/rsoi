package stink5.oauth2.lab2.service;

import static java.lang.String.*;
import static java.util.Objects.*;

import static org.springframework.transaction.annotation.Propagation.*;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stink5.oauth2.lab2.model.User;
import stink5.oauth2.lab2.repository.UserRepository;

@Service
@Transactional(
    readOnly = true,
    propagation = REQUIRES_NEW
)
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepo;

    @Autowired
    public UserService(final UserRepository userRepo) {
        this.userRepo = requireNonNull(userRepo);
    }

    @PostConstruct
    public void postConstruct() {
        // add default user
        if (this.userRepo.count() == 0) {
            final User entity = new User();

            entity.setUsername("username");
            entity.setEmail("e@mail.com");
            entity.setClientId("384a85fd-8165-442c-998d-4f1a88356bd1");
            entity.setClientSecret("5e0df7fa-a600-4140-9b28-0ea0f9aeb934");

            entity.getDetails().setUser(entity);
            entity.getDetails().setPassword("password");

            this.userRepo.saveAndFlush(entity);
        }
    }

    public User findById(final long id) {
        final User user = this.userRepo.findOne(id);

        if (user == null) {
            final String message = format("User not found by id=%d.", id);
            log.error(message);
            throw new RuntimeException(message);
        }

        return user;
    }

    public boolean existsWithClientId(final String clientId) {
        return this.userRepo.findByClientId(clientId) != null;
    }

    public User findByClientSecret(final String clientSecret) {
        return this.userRepo.findByClientSecret(clientSecret);
    }

}
