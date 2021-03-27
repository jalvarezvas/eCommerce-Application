package com.example.demo.security;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UserDetailsServiceImplTest {
    private UserDetailsServiceImpl userDetailsService;
    private UserRepository mockUserRepository = mock(UserRepository.class);


    @Before
    public void setUp() {
        userDetailsService = new UserDetailsServiceImpl();
        TestUtils.injectObjects(userDetailsService, "userRepository", mockUserRepository);
    }

    @Test
    public void load_user_by_username_happy_path() {
        doReturn(createUser()).when(mockUserRepository).findByUsername("test");

        UserDetails userDetails = userDetailsService.loadUserByUsername("test");
        assertNotNull(userDetails);
        assertEquals("test", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());
    }


    @Test(expected = UsernameNotFoundException.class)
    public void load_user_by_username_user_not_found() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("test");
    }

    private User createUser() {
        List<Item> items = new ArrayList<>();

        Item item = new Item(0L, "Round Widget", new BigDecimal(2.99), "A widget that is round");
        items.add(item);

        Cart cart = new Cart(0L, new ArrayList<>(), null, null);
        User user = new User(0L, "test", "hashedPassword");
        cart.setUser(user);
        cart.setItems(items);
        user.setCart(cart);

        return user;
    }
}
