package com.es.phoneshop.service;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class HttpSessionCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = HttpSessionCartService.class.getName() + ".cart";

    private ProductDao productDao;

    private static final class InstanceHolder {
        static final HttpSessionCartService instance = new HttpSessionCartService();
    }

    public static HttpSessionCartService getInstance() {
        return HttpSessionCartService.InstanceHolder.instance;
    }

    private HttpSessionCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    public synchronized Cart getCart(HttpServletRequest request) {
        Cart cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);

        if (cart == null) {
            cart = new Cart();
            request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart);
        }
        return cart;
    }

    @Override
    public synchronized void add(Cart cart, Long productId, int quantity) throws OutOfStockException, IllegalArgumentException {
        if (quantity <= 0) {
            throw new IllegalArgumentException();
        }

        Product product = productDao.getProduct(productId);

        Optional<CartItem> cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        int cartItemQuantity = cartItem.map(CartItem::getQuantity).orElse(0);

        if (product.getStock() < quantity + cartItemQuantity) {
            throw new OutOfStockException(product.getStock() - cartItemQuantity);
        }

        cartItem.ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + quantity),
                () -> cart.getItems().add(new CartItem(product, quantity)));

    }
}
