package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderServiceTest {

    @Mock
    private OrderDao orderDao;
    @Mock
    private Order order;
    @Mock
    private Cart cart;
    @Mock
    private CartItem cartItem1;
    @Mock
    private CartItem cartItem2;
    private List<CartItem> items;

    @InjectMocks
    private OrderService orderService = DefaultOrderService.getInstance();

    @Before
    public void setup() {
        items = List.of(cartItem1, cartItem2);

        when(cart.getItems()).thenReturn(items);
        when(cart.getTotalCost()).thenReturn(BigDecimal.valueOf(105));
    }

    @Test
    public void testGetOrder() {
        Order order = orderService.createOrder(cart);

        assertEquals(cart.getTotalCost(), order.getSubtotal());
    }

    @Test
    public void testGetPaymentMethod() {
        assertEquals(List.of(PaymentMethod.values()), orderService.getPaymentMethods());
    }

    @Test
    public void testPlaceOrder() {
        orderService.placeOrder(order);
        verify(order).setSecureId(anyString());
        verify(orderDao).save(order);
    }
}
