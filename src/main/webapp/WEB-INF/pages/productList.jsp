<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
    <p>
        Welcome to Expert-Soft training!
    </p>
    <c:if test="${not empty param.message}">
        <div class="success">
                ${param.message}
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="error">${param.error}</div>
    </c:if>
    <form action="">
        <label>
            <input name="query" value="${param.query}">
        </label>
        <button>Search</button>
    </form>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>Description
                <tags:sortLink sort="description" order="asc"/>
                <tags:sortLink sort="description" order="desc"/>
            </td>
            <td class="quantity">Quantity</td>
            <td class="price">Price
                <tags:sortLink sort="price" order="asc"/>
                <tags:sortLink sort="price" order="desc"/>
            </td>
            <td></td>
        </tr>
        </thead>
        <c:forEach var="product" items="${products}">
            <tr>
                <td>
                    <img class="product-tile"
                         src="${product.imageUrl}"
                         alt="error">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">${product.description}</a>
                </td>
                <td class="quantity">
                    <form id="addToCart${product.id}" method="post">
                        <input class="quantity" name="quantity" type="number" max="${product.stock}" min="1" value="1">
                        <input type="hidden" name="productId" value="${product.id}">
                        <input type="hidden" name="query" value="${param.query}">
                        <input type="hidden" name="sort" value="${param.sort}">
                        <input type="hidden" name="order" value="${param.order}">
                    </form>
                </td>
                <td class="price">
                    <c:if test="${fn:length(product.priceHistory) > 0}">
                    <a href="${pageContext.servletContext.contextPath}/price-history/${product.id}">
                        </c:if>
                            <fmt:formatNumber value="${product.price}" type="currency"
                                              currencySymbol="${product.currency.symbol}"/>
                </td>
                <td>
                    <button form="addToCart${product.id}">Add to cart</button>
                </td>
            </tr>
        </c:forEach>
    </table>
    <table>
        <tr>
            <h2>Recently viewed</h2>
            <c:forEach var="product" items="${recent}">
                <td>
                    <img src="${product.imageUrl}">
                    <div>
                        <a href="${pageContext.servletContext.contextPath}/products/${product.id}">${product.description}</a>
                    </div>
                    <div>
                        <fmt:formatNumber value="${product.price}" type="currency"
                                          currencySymbol="${product.currency.symbol}"/>
                    </div>
                </td>
            </c:forEach>
        </tr>
    </table>

</tags:master>