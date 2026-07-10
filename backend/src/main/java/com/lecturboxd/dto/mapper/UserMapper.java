package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.UserResponse;
import com.lecturboxd.entity.User;
import org.springframework.stereotype.Component;

/**
 * EN: Maps User entities to basic UserResponse DTOs for auth and profile surfaces.
 * KA: User ერთეულებს საბაზისო UserResponse DTO-ებად გარდაქმნის ავთენტიფიკაციისა და პროფილისთვის.
 */
@Component
public class UserMapper {

    /**
     * EN: Converts a User entity into a UserResponse (id, name, email, verified).
     * KA: User ერთეულს UserResponse-ად გარდაქმნის (id, სახელი, ელფოსტა, ვერიფიცირებული).
     */
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isVerified()
        );
    }
}
