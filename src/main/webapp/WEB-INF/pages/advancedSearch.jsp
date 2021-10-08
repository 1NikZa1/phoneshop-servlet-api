<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tags:master pageTitle="Advanced search">
    <p>

    </p>

    <form method="get" action="${pageContext.servletContext.contextPath}/advanced-search">
        <label>
            Description: <input name="description" value="${param.description}">
            <select name="searchOption">
                <option name="ALL_WORDS">ALL_WORDS</option>
                <option name="ANY_WORDS">ANY_WORDS</option>
            </select>
        </label>
        <br>
        <br>
        <label>
            Min price: <input type="text" name="minPrice"
                             value="${param.minPrice}">
        </label>
        <div class="error">${requestScope.errors.get("minPriceError")}</div>
        <br>
        <label>
            Max price: <input type="text" name="maxPrice"
                             value="${param.maxPrice}">
        </label>
        <div class="error">${requestScope.errors.get("maxPriceError")}</div>
        <br>
        <br>
        <button>Search</button>
    </form>

    <c:if test="${not empty requestScope.products and empty requestScope.errors}">
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>Description</td>
                <td class="price">Price</td>
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
                    <td class="price">
                        <c:if test="${fn:length(product.priceHistory) > 0}">
                        <a href="${pageContext.servletContext.contextPath}/price-history/${product.id}">
                            </c:if>
                                <fmt:formatNumber value="${product.price}" type="currency"
                                                  currencySymbol="${product.currency.symbol}"/>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</tags:master>