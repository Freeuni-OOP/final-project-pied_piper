package com.lecturboxd.dto.response;

/**
 * EN: Authentication success response containing a JWT and the authenticated user summary.
 * KA: ავთენტიფიკაციის წარმატების პასუხი JWT-ითა და ავთენტიფიცირებული მომხმარებლის შეჯამებით.
 */
public class AuthResponse {

    private String token;
    private UserResponse user;

    public AuthResponse() {
    }

    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
