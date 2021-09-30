package com.es.phoneshop.service.impl;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.RecentlyViewedService;
import com.es.phoneshop.service.impl.HttpSessionRecentlyViewedService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpSessionRecentlyViewedServiceTest {
    private static final String RECENT_SESSION_ATTRIBUTE = HttpSessionRecentlyViewedService.class.getName() + ".recently";
    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Product product1;
    @Mock
    private Product product2;
    @Mock
    private Product product3;
    @Mock
    private Product product4;

    @Spy
    private ArrayList<Product> recentlyViewed;
    @InjectMocks
    private RecentlyViewedService recentlyViewedService = HttpSessionRecentlyViewedService.getInstance();

    @Before
    public void setup() {
        when(request.getSession()).thenReturn(session);
        when(request.getSession().getAttribute(RECENT_SESSION_ATTRIBUTE)).thenReturn(recentlyViewed);
    }

    @Test
    public void testGetProducts() {
        when(session.getAttribute(RECENT_SESSION_ATTRIBUTE)).thenReturn(null);
        recentlyViewedService.get(request);
        verify(session).setAttribute(eq(RECENT_SESSION_ATTRIBUTE), any());
    }

    @Test
    public void testAddProduct() {
        recentlyViewedService.add(request, product1);
        verify(recentlyViewed).add(0, product1);
    }

    @Test
    public void testAddEqualProduct() {
        recentlyViewedService.add(request, product1);
        recentlyViewedService.add(request, product1);
        assertEquals(List.of(product1),recentlyViewed);
    }
    @Test
    public void testAddProductMoreThanCapacity() {
        recentlyViewedService.add(request, product1);
        recentlyViewedService.add(request, product2);
        recentlyViewedService.add(request, product3);
        recentlyViewedService.add(request, product4);
        assertEquals(List.of(product4,product3,product2),recentlyViewed);
    }
}
