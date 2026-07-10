package com.lecturboxd.auth;

import com.lecturboxd.entity.User;
import com.lecturboxd.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * EN: Loads LecturBoxd users by email for Spring Security authentication.
 * KA: ტვირთავს LecturBoxd მომხმარებლებს ელფოსტით Spring Security ავთენტიფიკაციისთვის.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * EN: Creates the service with the user repository dependency.
     * KA: ქმნის სერვისს მომხმარებლის რეპოზიტორიის დამოკიდებულებით.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * EN: Finds a user by email (case-insensitive) and wraps them as LecturboxdUserPrincipal.
     * KA: პოულობს მომხმარებელს ელფოსტით (რეგისტრის გაუთვალისწინებლად) და ახვევს LecturboxdUserPrincipal-ად.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new LecturboxdUserPrincipal(user);
    }
}
