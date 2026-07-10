package com.lecturboxd.dto.mapper;

import com.lecturboxd.dto.response.UserResponse;
import com.lecturboxd.entity.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toResponseMapsFields() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setName("Nika");
        user.setEmail("n@freeuni.edu.ge");
        user.setVerified(true);

        UserResponse response = userMapper.toResponse(user);

        assertEquals(id, response.getId());
        assertEquals("Nika", response.getName());
        assertEquals("n@freeuni.edu.ge", response.getEmail());
        assertTrue(response.isVerified());
    }
}
