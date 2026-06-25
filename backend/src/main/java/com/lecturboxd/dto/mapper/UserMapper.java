package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.UserResponse;
import com.lecturboxd.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.isVerified()
        );
    }
}
