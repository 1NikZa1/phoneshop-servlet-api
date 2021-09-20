package com.es.phoneshop.web;

import com.es.phoneshop.model.product.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {

    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) {
        productDao = ArrayListProductDao.getInstance();
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
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }

}
