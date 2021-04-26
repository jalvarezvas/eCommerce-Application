package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository mockItemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", mockItemRepository);
    }

    @Test
    public void get_items_happy_path() {
        List<Item> items = getItemList();
        when(mockItemRepository.findAll()).thenReturn(items);

        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(2, responseItems.size());
        assertEquals(1, responseItems.get(1).getId().longValue());
        assertEquals("Square Widget", responseItems.get(1).getName());
        assertEquals(new BigDecimal(1.99), responseItems.get(1).getPrice());
        assertEquals("A widget that is square", responseItems.get(1).getDescription());

        List<Item> equalItems = getItemList();
        assertEquals(equalItems, responseItems);
    }

    @Test
    public void find_by_id_happy_path() {
        when(mockItemRepository.findById(0L)).thenReturn(java.util.Optional.of(getItem()));

        final ResponseEntity<Item> response = itemController.getItemById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item item = response.getBody();
        assertNotNull(item);
        assertEquals(3, item.getId().longValue());
        assertEquals("Triangular Widget", item.getName());
        assertEquals(new BigDecimal(3.99), item.getPrice());
        assertEquals("A widget that is triangular", item.getDescription());
    }

    @Test
    public void find_by_id_user_not_found() {
        final ResponseEntity<Item> response = itemController.getItemById(-10L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        Item item = response.getBody();
        assertNull(item);
    }

    @Test
    public void find_by_user_name_happy_path() {
        List<Item> items = getItemList();
        when(mockItemRepository.findByName("Square Widget")).thenReturn(items);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Square Widget");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(2, responseItems.size());
        assertEquals(0, responseItems.get(0).getId().longValue());
        assertEquals("Round Widget", responseItems.get(0).getName());
        assertEquals(new BigDecimal(2.99), responseItems.get(0).getPrice());
        assertEquals("A widget that is round", responseItems.get(0).getDescription());
    }

    @Test
    public void find_by_user_name_not_found() {
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Absurd Widget");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        List<Item> responseItems = response.getBody();
        assertNull(responseItems);
    }

    private List<Item> getItemList() {
        List<Item> items = new ArrayList<>();

        Item item = new Item(0L, "Round Widget", new BigDecimal(2.99), "A widget that is round");
        items.add(item);

        item = new Item(1L, "Square Widget", new BigDecimal(1.99), "A widget that is square");
        items.add(item);

        return items;
    }

    private Item getItem() {
        Item item = new Item();
        item.setId(3L);
        item.setName("Triangular Widget");
        item.setPrice(new BigDecimal(3.99));
        item.setDescription("A widget that is triangular");

        return item;
    }
}
