package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.HttpSessionCartService;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteCartItemServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init(ServletConfig config) {
        cartService = HttpSessionCartService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String productIdString = request.getPathInfo().substring(1);
        Long productId = Long.parseLong(productIdString);

        Cart cart = cartService.getCart(request);
        cartService.delete(cart,productId);

        response.sendRedirect(request.getContextPath() + "/cart?message=Cart item removed");

    }

}
