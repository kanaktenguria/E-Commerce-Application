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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

        User user = new User();
        Cart cart = new Cart();
        user.setId(1);
        user.setUsername("test");
        user.setPassword("test123");
        user.setCart(cart);

        Item item = new Item();
        item.setId(100l);
        item.setName("Square Widget");
        BigDecimal price = BigDecimal.valueOf(1.99);
        item.setPrice(price);
        item.setDescription("A widget that is square");
//
        when(userRepo.findByUsername("test")).thenReturn(user);
        when(itemRepo.findById(100l)).thenReturn(java.util.Optional.of(item));
    }

    @Test
    public void add_to_cart_happy_path() {
        ModifyCartRequest r = new ModifyCartRequest();
        r.setItemId(100l);
        r.setQuantity(1);
        r.setUsername("test");
        ResponseEntity<Cart> response = cartController.addTocart(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart c = response.getBody();
        assertNotNull(c);
        assertEquals(BigDecimal.valueOf(1.99), c.getTotal());
    }


    @Test
    public void remove_from_cart_happy_path() {
        ModifyCartRequest r = new ModifyCartRequest();
        r.setItemId(100l);
        r.setQuantity(1);
        r.setUsername("test");

        ResponseEntity<Cart> response = cartController.addTocart(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        response = cartController.removeFromcart(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart c = response.getBody();
        assertNotNull(c);
        assertEquals(0, c.getTotal().intValue());
    }

    @Test
    public void remove_from_cart_invalid_user_sad_path() {
        ModifyCartRequest r = new ModifyCartRequest();
        r.setItemId(100l);
        r.setQuantity(1);
        r.setUsername("invalider");
        ResponseEntity<Cart> response = cartController.removeFromcart(r);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
