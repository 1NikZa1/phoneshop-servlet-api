package com.es.phoneshop.service.impl;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.RecentlyViewedService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class HttpSessionRecentlyViewedService implements RecentlyViewedService {
    private static final String RECENT_SESSION_ATTRIBUTE = HttpSessionRecentlyViewedService.class.getName() + ".recently";
    public static final int RECENTLY_VIEWED_AMOUNT = 3;

    private static final class InstanceHolder {
        static final HttpSessionRecentlyViewedService instance = new HttpSessionRecentlyViewedService();
    }

    public static HttpSessionRecentlyViewedService getInstance() {
        return HttpSessionRecentlyViewedService.InstanceHolder.instance;
    }

    private HttpSessionRecentlyViewedService() {
    }

    @Override
    public synchronized void add(HttpServletRequest request, Product product) {
        List<Product> recentlyViewed = get(request);
        if (recentlyViewed.contains(product)) {
            recentlyViewed.remove(product);
        } else if (recentlyViewed.size() == RECENTLY_VIEWED_AMOUNT) {
            recentlyViewed.remove(recentlyViewed.size() - 1);
        }
        recentlyViewed.add(0, product);
    }

    @Override
    public synchronized List<Product> get(HttpServletRequest request) {
        List<Product> recentlyViewed = (List<Product>) request.getSession().getAttribute(RECENT_SESSION_ATTRIBUTE);
        if (recentlyViewed == null) {
            recentlyViewed = new ArrayList<>();
            request.getSession().setAttribute(RECENT_SESSION_ATTRIBUTE, recentlyViewed);
        }
        return recentlyViewed;
    }
}
