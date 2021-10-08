package com.es.phoneshop.web;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ArrayListProductDao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdvancedSearchPageServlet extends HttpServlet {
    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) {
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String minPriceString = request.getParameter("minPrice");
        String maxPriceString = request.getParameter("maxPrice");
        String searchOption = request.getParameter("searchOption");
        String description = request.getParameter("description");

        Map<String, String> errors = new HashMap<>();

        Double minPrice = parseDouble(minPriceString, errors, "minPriceError");
        Double maxPrice = parseDouble(maxPriceString, errors, "maxPriceError");

        if (errors.isEmpty()) {
            request.setAttribute("products", productDao.findProducts(description, searchOption, minPrice, maxPrice));
        } else {
            request.setAttribute("errors", errors);
        }

        request.getRequestDispatcher("/WEB-INF/pages/advancedSearch.jsp").forward(request, response);

    }

    private Double parseDouble(String doubleValue, Map<String, String> errors, String errorName) {
        if (doubleValue != null && !doubleValue.isEmpty()) {
            try {
                return Double.parseDouble(doubleValue);
            } catch (NumberFormatException ex) {
                errors.put(errorName, "Not a number");
            }
        }
        return null;
    }

}
