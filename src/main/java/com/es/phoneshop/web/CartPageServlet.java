package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.impl.HttpSessionCartService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CartPageServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init(ServletConfig config) {
        cartService = HttpSessionCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        Map<Long, String> errors = updateCart(request, cart);

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated");
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }

    private Map<Long, String> updateCart(HttpServletRequest request, Cart cart) {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        Map<Long, String> errors = new HashMap<>();

        for (int i = 0; i < productIds.length; i++) {
            if (quantities != null) {
                Long productId = Long.parseLong(productIds[i]);
                int quantityInt;
                try {
                    NumberFormat format = NumberFormat.getInstance(request.getLocale());
                    quantityInt = format.parse(quantities[i]).intValue();
                    cartService.update(cart, productId, quantityInt);
                } catch (ParseException ex) {
                    errors.put(productId, "not a number");
                } catch (IllegalArgumentException ex) {
                    errors.put(productId, "invalid value");
                } catch (OutOfStockException ex) {
                    errors.put(productId, "out of stock");
                }
            }
        }
        return errors;
    }

}
