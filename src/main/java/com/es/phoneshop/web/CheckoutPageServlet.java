package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.service.CartService;
import com.es.phoneshop.service.OrderService;
import com.es.phoneshop.service.impl.DefaultOrderService;
import com.es.phoneshop.service.impl.HttpSessionCartService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class CheckoutPageServlet extends HttpServlet {
    private CartService cartService;
    private OrderService orderService;

    @Override
    public void init(ServletConfig config) {
        cartService = HttpSessionCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        request.setAttribute("order", orderService.createOrder(cart));
        request.setAttribute("paymentMethods", orderService.getPaymentMethods());
        request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        Order order = orderService.createOrder(cart);
        Map<String, String> errors = new HashMap<>();

        LocalDate deliveryDate = readDeliveryDate(request, errors);
        PaymentMethod paymentMethod = readPaymentMethod(request, errors);

        String firstname = parseParam(request, "firstname", errors);
        String lastname = parseParam(request, "lastname", errors);
        String phone = parseParam(request, "phone", errors);
        String deliveryAddress = parseParam(request, "deliveryAddress", errors);

        if (errors.isEmpty()) {
            populateOrder(order, firstname, lastname, phone, deliveryDate, deliveryAddress, paymentMethod);
            orderService.placeOrder(order);
            cartService.clearCart(cart);
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getSecureId());
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }

    }

    private String parseParam(HttpServletRequest request, String param, Map<String, String> errors) {
        String value = request.getParameter(param);
        if (value == null || value.isEmpty()) {
            errors.put(param, "value is required");
            return null;
        } else {
            return value;
        }
    }

    private PaymentMethod readPaymentMethod(HttpServletRequest request, Map<String, String> errors) {
        String paymentMethod = parseParam(request, "paymentMethod", errors);
        if (paymentMethod != null) {
            try {
                return PaymentMethod.valueOf(paymentMethod);
            } catch (IllegalArgumentException | NullPointerException e) {
                errors.put("paymentMethod", "wrong payment method");
            }
        }
        return null;
    }

    private LocalDate readDeliveryDate(HttpServletRequest request, Map<String, String> errors) {
        String deliveryDate = parseParam(request, "deliveryDate", errors);
        if (deliveryDate != null) {
            try {
                return LocalDate.parse(deliveryDate);
            } catch (DateTimeParseException e) {
                errors.put("deliveryDate", "wrong date format(yyyy-mm-dd)");
            }
        }
        return null;
    }

    private void populateOrder(Order order, String firstname, String lastname, String phone,
                               LocalDate deliveryDate, String deliveryAddress, PaymentMethod paymentMethod) {
        order.setFirstname(firstname);
        order.setLastname(lastname);
        order.setPhone(phone);
        order.setDeliveryDate(deliveryDate);
        order.setDeliveryAddress(deliveryAddress);
        order.setPaymentMethod(paymentMethod);
    }
}
