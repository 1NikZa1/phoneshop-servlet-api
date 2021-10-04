package com.es.phoneshop.service;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
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
        tryAddItemToCart(cart, productId, quantity, true);
    }

    @Override
    public synchronized void update(Cart cart, Long productId, int quantity) throws OutOfStockException {
        tryAddItemToCart(cart, productId, quantity, false);
    }

    private void tryAddItemToCart(Cart cart, Long productId, int quantity, boolean isNewCartItem) throws OutOfStockException {
        if (quantity <= 0) {
            throw new IllegalArgumentException();
        }
        Product product = productDao.getProduct(productId);
        Optional<CartItem> cartItemOptional = findCartItem(cart, productId);

        if (isNewCartItem) {
            int cartItemQuantity = cartItemOptional.map(CartItem::getQuantity).orElse(0);

            if (product.getStock() < quantity + cartItemQuantity) {
                throw new OutOfStockException(product.getStock() - cartItemQuantity);
            }

            cartItemOptional.ifPresentOrElse(item -> item.setQuantity(item.getQuantity() + quantity),
                    () -> cart.getItems().add(new CartItem(product, quantity)));

        } else {

            if (product.getStock() < quantity) {
                throw new OutOfStockException(product.getStock());
            }

            cartItemOptional.ifPresent(item -> item.setQuantity(quantity));
        }
        recalculateCart(cart);
    }

    @Override
    public synchronized void delete(Cart cart, Long productId) {
        cart.getItems().removeIf(cartItem ->
                productId.equals(cartItem.getProduct().getId()));
        recalculateCart(cart);
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                .map(CartItem::getQuantity).mapToInt(q -> q).sum());
        cart.setTotalCost(cart.getItems().stream()
                .map(cartItem -> cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private Optional<CartItem> findCartItem(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }
}
