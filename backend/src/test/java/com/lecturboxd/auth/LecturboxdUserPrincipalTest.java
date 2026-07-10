package com.lecturboxd.auth;

import com.lecturboxd.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LecturboxdUserPrincipalTest {

    @Test
    void exposesUserDetailsContract() {
        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setEmail("a@freeuni.edu.ge");
        user.setPassword("secret");
        user.setVerified(false);

        LecturboxdUserPrincipal principal = new LecturboxdUserPrincipal(user);

        assertEquals(id, principal.getId());
        assertEquals("a@freeuni.edu.ge", principal.getUsername());
        assertEquals("secret", principal.getPassword());
        assertTrue(principal.isAccountNonExpired());
        assertTrue(principal.isAccountNonLocked());
        assertTrue(principal.isCredentialsNonExpired());
        assertTrue(principal.isEnabled());
        assertEquals(1, principal.getAuthorities().size());
        GrantedAuthority authority = principal.getAuthorities().iterator().next();
        assertEquals("ROLE_USER", authority.getAuthority());
    }
}
