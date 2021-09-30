package com.es.phoneshop.web;

import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.*;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.HttpSessionCartService;
import com.es.phoneshop.service.impl.HttpSessionRecentlyViewedService;
import com.es.phoneshop.service.RecentlyViewedService;
import org.apache.http.client.utils.URIBuilder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {

    private ProductDao productDao;
    private RecentlyViewedService recentlyViewedService;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) {
        productDao = ArrayListProductDao.getInstance();
        recentlyViewedService = HttpSessionRecentlyViewedService.getInstance();
        cartService = HttpSessionCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String sort = request.getParameter("sort");
        String order = request.getParameter("order");
        List<Product> productList = productDao.findProducts(query,
                Optional.ofNullable(sort).map(name -> SortField.valueOf(name.toUpperCase())).orElse(null),
                Optional.ofNullable(order).map(name1 -> SortOrder.valueOf(name1.toUpperCase())).orElse(null));
        request.setAttribute("products", productList);
        request.setAttribute("recent", recentlyViewedService.get(request));
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long productId = Long.parseLong(request.getParameter("productId"));
        String quantity = request.getParameter("quantity");
        int quantityInt;
        try {
            NumberFormat format = NumberFormat.getInstance(request.getLocale());
            quantityInt = format.parse(quantity).intValue();
            Cart cart = cartService.getCart(request);
            cartService.add(cart, productId, quantityInt);
        } catch (OutOfStockException ex) {
            doRedirect(request, response, "Out of stock for product " + productId);
            return;
        } catch (ParseException ex) {
            doRedirect(request, response, "parse exception");
            return;
        }
        doRedirect(request, response, "Product " + productId + " added to cart");
    }

    private void doRedirect(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        Map<String, String> params = Map.of(
                "query", request.getParameter("query"),
                "sort", request.getParameter("sort"),
                "order", request.getParameter("order"),
                "message", message
        );

        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(request.getContextPath() + "/products");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        params.entrySet().stream()
                .filter(t -> !t.getValue().isEmpty())
                .forEach(t -> uriBuilder.addParameter(t.getKey(), t.getValue()));

        response.sendRedirect(uriBuilder.toString());
    }
}
