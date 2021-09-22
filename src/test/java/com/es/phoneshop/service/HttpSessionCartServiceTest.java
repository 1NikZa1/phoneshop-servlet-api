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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    @InjectMocks
    private CartService cartService = HttpSessionCartService.getInstance();

    @Before
    public void setup() {
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(product, 4));
        when(cart.getItems()).thenReturn(cartItems);

        when(productDao.getProduct(1L)).thenReturn(product);

        when(product.getId()).thenReturn(1L);
        when(product.getStock()).thenReturn(100);
        when(product.getPrice()).thenReturn(BigDecimal.valueOf(600));

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
        cartService.add(cart, 1L, 6);

        assertEquals(1, cart.getItems().size());
        assertEquals(10, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void testUpdateProduct() throws OutOfStockException {
        cartService.update(cart, 1L, 5);
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void testDelete() {
        cartService.delete(cart, 1L);

        assertTrue(cart.getItems().isEmpty());
    }

    @Test(expected = OutOfStockException.class)
    public void testUpdateOutOfStock() throws OutOfStockException {
        cartService.update(cart, 1L, 101);
    }
}
