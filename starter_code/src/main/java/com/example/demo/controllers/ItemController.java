package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/item")
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping
    public ResponseEntity<List<Item>> getItems() {
        log.info("ItemController - getItems() invoked");
        return ResponseEntity.ok(itemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        log.info("ItemController - getItemById() invoked with: {}", "id: " + id);
//        return ResponseEntity.of(itemRepository.findById(id));
        Optional<Item> item = itemRepository.findById(id);

        if (item == null) {
            log.error("ItemController - getItemById() Item not found");
            return ResponseEntity.notFound().build();
        } else {
            log.info("ItemController - getItemById() OK, item id: {}", id);
            return ResponseEntity.ok(item.get());
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
        log.info("ItemController - getItemsByName() invoked with: {}", name);
        List<Item> items = itemRepository.findByName(name);

//        return items == null || items.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(items);

        if (items == null || items.isEmpty()) {
            log.error("ItemController - getItemsByName() Items null or empty");
            return ResponseEntity.notFound().build();
        } else {
            log.info("ItemController - getItemsByName() OK");
            return ResponseEntity.ok(items);
        }
    }

}
