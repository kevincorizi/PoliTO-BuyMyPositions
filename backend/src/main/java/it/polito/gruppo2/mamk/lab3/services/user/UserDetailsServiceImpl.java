package it.polito.gruppo2.mamk.lab3.services.user;

import it.polito.gruppo2.mamk.lab3.persistence.user.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// This Service is used by Spring Security to Load user Details during authentication process

@Primary //Otherwise ServerSecurityConfig can't autowire
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersRepository repo;

    @Autowired
    PasswordEncoder userPasswordEncoder;

    @Override
    // Not strictly needed in this case as CRUD method provided by JPA repository implementation
    // are already transactional, anyway this is a best practice and assures that in case of modifications
    // to this method involving multiple calls to repository everything happens within a transaction
    // Here we return User which is an implementation of UserDetails service
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        it.polito.gruppo2.mamk.lab3.persistence.user.User user = repo.findUserByUsername(username);

        if (user != null) { // Convert our user object into an object which can be used by the
            return User.withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
        }
        throw new UsernameNotFoundException(username);
    }

}
