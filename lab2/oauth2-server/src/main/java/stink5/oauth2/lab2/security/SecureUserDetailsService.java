package stink5.oauth2.lab2.security;

import static java.util.Objects.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stink5.oauth2.lab2.model.User;
import stink5.oauth2.lab2.repository.UserRepository;

@Service
@Transactional(readOnly=true)
public class SecureUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(final UserRepository repo) {
        this.userRepository = requireNonNull(repo);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User userEntity = this.userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User '" + username + "' has not been found.");
        }
        return userEntity.getDetails();
    }

}