package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);

        Item item = new Item();
        item.setId(100l);
        item.setName("Square Widget");
        BigDecimal price = BigDecimal.valueOf(1.99);
        item.setPrice(price);
        item.setDescription("A widget that is square");

        when(itemRepo.findAll()).thenReturn(Collections.singletonList(item));
        when(itemRepo.findById(100l)).thenReturn(java.util.Optional.of(item));
        when(itemRepo.findByName("Square Widget")).thenReturn(Collections.singletonList(item));
    }

    @Test
    public void find_all_items_happy_path() {
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    public void find_item_by_id_happy_path() {
        ResponseEntity<Item> response = itemController.getItemById(100l);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item i = response.getBody();
        assertNotNull(i);
//        Double actualPrice = i.getPrice().doubleValue();
        assertEquals((Double)1.99, new Double(String.valueOf(i.getPrice())));
    }

    @Test
    public void find_item_by_id_sad_path() {
        ResponseEntity<Item> response = itemController.getItemById(200l);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void find_item_by_name_happy_path() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Square Widget");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }
}
