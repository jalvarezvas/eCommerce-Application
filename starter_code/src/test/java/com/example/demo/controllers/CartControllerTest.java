package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository mockUserRepository = mock(UserRepository.class);
    private CartRepository mockCartRepository = mock(CartRepository.class);
    private ItemRepository mockItemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", mockUserRepository);
        TestUtils.injectObjects(cartController, "cartRepository", mockCartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", mockItemRepository);
    }

    @Test
    public void add_to_cart_happy_path() {
        when(mockUserRepository.findByUsername("test")).thenReturn(createUser());
        when(mockItemRepository.findById(1L)).thenReturn(java.util.Optional.of(new Item(1L, "Square Widget", new BigDecimal(1.99), "A widget that is square")));

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(0, cart.getId().longValue());
        assertEquals(new BigDecimal(1.99), cart.getTotal());
        assertEquals(1, cart.getItems().size());
    }

    @Test
    public void add_to_cart_user_not_found() {
        ModifyCartRequest modifyCartRequest = getModifyCartRequest();

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNull(cart);
    }

    @Test
    public void add_to_cart_item_not_found() {
        when(mockUserRepository.findByUsername("test")).thenReturn(createUser());

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        Cart cart = response.getBody();
    }

    @Test
    public void remove_from_cart_happy_path() {
        when(mockUserRepository.findByUsername("test")).thenReturn(createUser());
        when(mockItemRepository.findById(1L)).thenReturn(java.util.Optional.of(new Item(1L, "Square Widget", new BigDecimal(1.99), "A widget that is square")));

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(0, cart.getId().longValue());
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void remove_from_cart_user_not_found() {
        ModifyCartRequest modifyCartRequest = getModifyCartRequest();

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        Cart cart = response.getBody();
        assertNull(cart);
    }

    @Test
    public void remove_from_cart_item_not_found() {
        when(mockUserRepository.findByUsername("test")).thenReturn(createUser());

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        Cart cart = response.getBody();
    }

    private User createUser() {
        Cart cart = new Cart();
        cart.setId(0L);

        User user = new User(0L, "test", "hashedPassword");
        user.setCart(cart);

        return user;
    }

    private ModifyCartRequest getModifyCartRequest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);

        return modifyCartRequest;
    }

}
