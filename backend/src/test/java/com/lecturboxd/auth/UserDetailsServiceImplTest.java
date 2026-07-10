package com.lecturboxd.auth;

import com.lecturboxd.entity.User;
import com.lecturboxd.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsernameReturnsPrincipal() {
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setEmail("a@freeuni.edu.ge");
        user.setPassword("hash");
        user.setVerified(true);
        when(userRepository.findByEmailIgnoreCase("a@freeuni.edu.ge")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("a@freeuni.edu.ge");

        assertTrue(details instanceof LecturboxdUserPrincipal);
        assertEquals(id, ((LecturboxdUserPrincipal) details).getId());
        assertEquals("a@freeuni.edu.ge", details.getUsername());
    }

    @Test
    void loadUserByUsernameThrowsWhenMissing() {
        when(userRepository.findByEmailIgnoreCase("missing@freeuni.edu.ge")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing@freeuni.edu.ge"));
    }
}
