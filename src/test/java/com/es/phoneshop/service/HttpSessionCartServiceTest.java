package com.es.phoneshop.service;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpSessionCartServiceTest {
    private static final String CART_SESSION_ATTRIBUTE = HttpSessionCartService.class.getName() + ".cart";

    @Mock
    private ProductDao productDao;
    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest request;
    @Mock
    private Product product;
    @Mock
    private Cart cart;
    @Spy
    private ArrayList<CartItem> items;
    @InjectMocks
    private CartService cartService = HttpSessionCartService.getInstance();

    @Before
    public void setup() {
        when(cart.getItems()).thenReturn(items);
        when(productDao.getProduct(1L)).thenReturn(product);

        when(product.getId()).thenReturn(1L);
        when(product.getStock()).thenReturn(100);
    }

    @Test
    public void testGetCart() {
        when(request.getSession()).thenReturn(session);
        when(request.getSession().getAttribute(CART_SESSION_ATTRIBUTE)).thenReturn(null);

        cartService.getCart(request);

        verify(session).setAttribute(eq(CART_SESSION_ATTRIBUTE), any(Cart.class));
    }

    @Test
    public void testAddProduct() throws OutOfStockException {
        cartService.add(cart, 1L, 5);
        assertEquals(1, cart.getItems().size());
    }

    @Test(expected = OutOfStockException.class)
    public void testAddProductOutOfStock() throws OutOfStockException {
        cartService.add(cart, 1L, 101);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddProductWithIllegalQuantity() throws OutOfStockException {
        cartService.add(cart, 1L, -5);
    }

    @Test
    public void testAddProductsWithEqualId() throws OutOfStockException {
        cartService.add(cart, 1L, 4);
        cartService.add(cart, 1L, 6);

        assertEquals(1, cart.getItems().size());
        assertEquals(10, cart.getItems().get(0).getQuantity());
    }
}
