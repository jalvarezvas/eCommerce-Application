package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository mockUserRepository = mock(UserRepository.class);
    private CartRepository mockCartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder mockBCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", mockUserRepository);
        TestUtils.injectObjects(userController, "cartRepository", mockCartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", mockBCryptPasswordEncoder);
    }

    @Test
    public void find_by_id_happy_path() {
        when(mockUserRepository.findById(0L)).thenReturn(java.util.Optional.of(new User(0L, "test", "hashedPassword")));

        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void find_by_id_user_not_found() {
        final ResponseEntity<User> response = userController.findById(-10L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void find_by_user_name_happy_path() {
        when(mockUserRepository.findByUsername("test")).thenReturn(new User(0L, "test", "hashedPassword"));

        final ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void find_by_user_name_user_not_found() {
        final ResponseEntity<User> response = userController.findByUserName("testNotFound");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void create_user_happy_path() {
        when(mockBCryptPasswordEncoder.encode("somePassword")).thenReturn("hashedPassword");

        CreateUserRequest createUserRequest = getCreateUserRequest();

        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void create_user_password_not_valid() {
        when(mockBCryptPasswordEncoder.encode("somePassword")).thenReturn("hashedPassword");

        CreateUserRequest createUserRequest = getCreateUserRequest();
        createUserRequest.setConfirmPassword("wrongPassword");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        User user = response.getBody();
        assertNull(user);
    }

    private CreateUserRequest getCreateUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("somePassword");
        createUserRequest.setConfirmPassword("somePassword");

        return createUserRequest;
    }

}
