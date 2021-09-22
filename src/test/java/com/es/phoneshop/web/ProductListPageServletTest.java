package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private Product product1;
    @Mock
    private Product product2;
    @Mock
    private ProductDao productDao;
    @Mock
    private CartService cartService;
    @Mock
    private Cart cart;
    @Mock
    private RecentlyViewedService recentlyViewedService;

    @InjectMocks
    private final ProductListPageServlet servlet = new ProductListPageServlet();

    private final String sortOrder = "ASC";
    private final String sortField = "PRICE";
    private final String query = "Samsung";

    @Before
    public void setup() {
        when(request.getParameter("query")).thenReturn(query);
        when(request.getParameter("sort")).thenReturn(sortField);
        when(request.getParameter("order")).thenReturn(sortOrder);
        when(productDao.findProducts(query, SortField.valueOf(sortField), SortOrder.valueOf(sortOrder))).thenReturn(Arrays.asList(product1, product2));
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(recentlyViewedService.get(request)).thenReturn(List.of(product1));
        when(request.getLocale()).thenReturn(new Locale("ru"));
        when(cartService.getCart(request)).thenReturn(cart);

    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
        verify(request).setAttribute("recent", recentlyViewedService.get(request));
        verify(request).setAttribute("products", productDao.findProducts(query, SortField.valueOf(sortField), SortOrder.valueOf(sortOrder)));
    }

    @Test
    public void testDoPost() throws IOException {
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("5");

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
        verify(request).getContextPath();
    }

    @Test
    public void doPostIllegalQuantity() throws IOException {
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("ggg");
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(response).sendRedirect(request.getContextPath() + "/products" + "?error=parse exception");
    }
}