package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        long id = 100;
        User user = new User();
        user.setUsername("test");
        user.setPassword("test123");
        user.setId(id);

        when(userRepo.findByUsername("test")).thenReturn(user);
        when(userRepo.findById(id)).thenReturn(Optional.of(user));
    }

    @Test
    public void create_user_happy_path() throws Exception{
        when(encoder.encode("test123")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("test123");
        r.setConfirmPassword("test123");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());

    }

    @Test
    public void find_user_by_id_happy_path(){
        long id = 100;
        ResponseEntity<User> response = userController.findById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(id, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("test123", u.getPassword());
    }

    @Test
    public void find_user_by_name_happy_path(){
        long id = 100;
        ResponseEntity<User> response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(id, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("test123", u.getPassword());
    }

    @Test
    public void find_user_by_id_sad_path() {
        final ResponseEntity<User> response = userController.findById(200L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
