package com.lecturboxd.auth;

import com.lecturboxd.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * EN: Spring Security UserDetails adapter wrapping a LecturBoxd User entity.
 * KA: Spring Security-ის UserDetails ადაპტერი, რომელიც LecturBoxd-ის User ენთითის ახვევს.
 */
public class LecturboxdUserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final boolean verified;

    /**
     * EN: Copies identity fields from the persisted User into this principal.
     * KA: კოპირებს იდენტობის ველებს შენახული User-იდან ამ პრინციპალში.
     */
    public LecturboxdUserPrincipal(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.verified = user.isVerified();
    }

    /**
     * EN: Returns the user's database UUID.
     * KA: აბრუნებს მომხმარებლის მონაცემთა ბაზის UUID-ს.
     */
    public UUID getId() {
        return id;
    }

    /**
     * EN: Grants a single ROLE_USER authority to every authenticated LecturBoxd user.
     * KA: ანიჭებს ერთ ROLE_USER უფლებამოსილებას ყველა ავთენტიფიცირებულ LecturBoxd მომხმარებელს.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * EN: Returns the encoded password used by Spring Security authentication.
     * KA: აბრუნებს დაშიფრულ პაროლს, რომელსაც Spring Security ავთენტიფიკაცია იყენებს.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * EN: Uses email as the Spring Security username.
     * KA: იყენებს ელფოსტას Spring Security-ის მომხმარებლის სახელად.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * EN: Account expiry is not used; always returns true.
     * KA: ანგარიშის ვადის ამოწურვა არ გამოიყენება; ყოველთვის აბრუნებს true-ს.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * EN: Account locking is not used; always returns true.
     * KA: ანგარიშის დაბლოკვა არ გამოიყენება; ყოველთვის აბრუნებს true-ს.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * EN: Credential expiry is not used; always returns true.
     * KA: რწმუნებათა ვადის ამოწურვა არ გამოიყენება; ყოველთვის აბრუნებს true-ს.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * EN: Always enabled here; verification is enforced at login/OTP, not via isEnabled.
     * KA: აქ ყოველთვის ჩართულია; ვერიფიკაცია ხდება ლოგინზე/OTP-ზე, არა isEnabled-ით.
     */
    @Override
    public boolean isEnabled() {
        // EN: Account access is gated at login/verify; do not block JWT requests via isEnabled | KA: ანგარიშზე წვდომა იბლოკება ლოგინზე/ვერიფიკაციაზე; JWT მოთხოვნები isEnabled-ით არ იბლოკოს
        return true;
    }
}
