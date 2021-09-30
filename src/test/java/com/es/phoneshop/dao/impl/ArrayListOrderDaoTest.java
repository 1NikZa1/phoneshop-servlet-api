package com.es.phoneshop.dao.impl;

import com.es.phoneshop.model.order.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArrayListOrderDaoTest {
    @Mock
    private Order order;
    @Spy
    private ArrayList<Order> orders;

    @InjectMocks
    private ArrayListOrderDao orderDao = ArrayListOrderDao.getInstance();

    @Before
    public void setup() {
        orders.addAll(List.of(order));
        when(order.getSecureId()).thenReturn("123-456-789");
    }

    @Test
    public void testGetOrderBySecureId() {
        assertEquals(order.getSecureId(), orderDao.getOrderBySecureId("123-456-789").getSecureId());
    }

}
