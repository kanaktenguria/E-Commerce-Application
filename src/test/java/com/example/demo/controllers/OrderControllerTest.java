package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private OrderRepository orderRepo = mock(OrderRepository.class);

    private UserRepository userRepo = mock(UserRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
        TestUtils.injectObjects(orderController, "userRepository", userRepo);

        Item item = new Item();
        item.setId(100l);
        item.setName("Square Widget");
        BigDecimal price = BigDecimal.valueOf(1.99);
        item.setPrice(price);
        item.setDescription("A widget that is square");

        List<Item> items = new ArrayList<Item>();
        items.add(item);

        User user = new User();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("test123");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(items);
        BigDecimal total = BigDecimal.valueOf(1.99);
        cart.setTotal(total);
        user.setCart(cart);

        when(userRepo.findByUsername("test")).thenReturn(user);
        when(userRepo.findByUsername("invalider")).thenReturn(null);
    }

    @Test
    public void create_order_for_user_happy_path() {
        ResponseEntity<UserOrder> response = orderController.submit("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder uo = response.getBody();
        assertNotNull(uo);
        assertEquals(1, uo.getItems().size());
    }

    @Test
    public void create_order_for_invalid_user_sad_path() {
        ResponseEntity<UserOrder> response = orderController.submit("invalider");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void find_order_for_user_happy_path() {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }
}
