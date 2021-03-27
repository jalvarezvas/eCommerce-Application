package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository mockUserRepository = mock(UserRepository.class);
    private OrderRepository mockOrderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", mockUserRepository);
        TestUtils.injectObjects(orderController, "orderRepository", mockOrderRepository);
    }

    @Test
    public void submit_happy_path() {
        doReturn(createUser()).when(mockUserRepository).findByUsername("test");

        final ResponseEntity<UserOrder> response = orderController.submit("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);

        User user = userOrder.getUser();
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());

        List<Item> items = userOrder.getItems();
        assertEquals(1, items.size());
        assertEquals(0, items.get(0).getId().longValue());
        assertEquals("Round Widget", items.get(0).getName());
        assertEquals(new BigDecimal(2.99), items.get(0).getPrice());
        assertEquals("A widget that is round", items.get(0).getDescription());
    }

    @Test
    public void submit_user_not_found() {
        final ResponseEntity<UserOrder> response = orderController.submit("test");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        UserOrder userOrder = response.getBody();
        assertNull(userOrder);
    }

    @Test
    public void get_orders_from_user_happy_path() {
        doReturn(createUser()).when(mockUserRepository).findByUsername("test");
        doReturn(getUserOrders()).when(mockOrderRepository).findByUser(Mockito.any(User.class));
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> userOrders = response.getBody();
        assertNotNull(userOrders);
        assertEquals(1, userOrders.size());

        User user = userOrders.get(0).getUser();
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());

        List<Item> items = userOrders.get(0).getItems();
        assertEquals(2, items.size());
        assertEquals(1, items.get(1).getId().longValue());
        assertEquals("Square Widget", items.get(1).getName());
        assertEquals(new BigDecimal(1.99), items.get(1).getPrice());
        assertEquals("A widget that is square", items.get(1).getDescription());
    }

    @Test
    public void get_orders_from_user_user_not_found() {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertNotNull(response);

        List<UserOrder> userOrders = response.getBody();
        assertNull(userOrders);
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

    private ModifyCartRequest getModifyCartRequest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(1);

        return modifyCartRequest;
    }

    private List<UserOrder> getUserOrders() {
        List<UserOrder> userOrders = new ArrayList<>();

        UserOrder userOrder = new UserOrder();
        userOrder.setId(1L);
        userOrder.setUser(createUser());
        userOrders.add(userOrder);

        List<Item> items = new ArrayList<>();

        Item item = new Item(0L, "Round Widget", new BigDecimal(2.99), "A widget that is round");
        items.add(item);

        item = new Item(1L, "Square Widget", new BigDecimal(1.99), "A widget that is square");
        items.add(item);

        userOrder.setItems(items);

        return userOrders;
    }
}
