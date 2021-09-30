package com.es.phoneshop.web;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.RecentlyViewedService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private Product product;
    @Mock
    private ProductDao productDao;
    @Mock
    private CartService cartService;
    @Mock
    private RecentlyViewedService recentlyViewedService;
    @Mock
    private Cart cart;

    @InjectMocks
    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void setup() {
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getLocale()).thenReturn(Locale.US);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(cartService.getCart(request)).thenReturn(cart);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        when(productDao.get(1L)).thenReturn(product);
        when(recentlyViewedService.get(request)).thenReturn(List.of(product));
        servlet.doGet(request, response);

        verify(request).setAttribute("product", product);
        verify(request).setAttribute("recent",recentlyViewedService.get(request));
        verify(requestDispatcher).forward(request, response);
    }

    @Test(expected = NoSuchElementException.class)
    public void testDoGetWithWrongId() throws ServletException, IOException {
        when(productDao.get(1L)).thenThrow(new NoSuchElementException());
        servlet.doGet(request, response);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDoGetWithNullId() throws ServletException, IOException {
        when(productDao.get(1L)).thenThrow(new IllegalArgumentException());
        servlet.doGet(request, response);
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("100");

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoPostParseException() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("qwerty");

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Not a number");
    }

    @Test
    public void testDoPostOutOfStockException() throws ServletException, IOException, OutOfStockException {
        when(request.getParameter("quantity")).thenReturn("5");
        doThrow(new OutOfStockException(1))
                .when(cartService).add(cart, 10L, 5);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Out of stock, available " + request.getParameter("quantity"));
    }

}
