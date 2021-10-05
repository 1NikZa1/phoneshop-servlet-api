<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<tags:master pageTitle="Cart">
    <p>total quantity: ${cart.totalQuantity}</p>
    <c:if test="${not empty param.message and empty errors}">
        <div class="success">
                ${param.message}
        </div>
    </c:if>
    <c:if test="${not empty requestScope.errors}">
        <div class="error">Cart update error</div>
    </c:if>
    <form method="post">
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>Description</td>
                <td class="quantity">Quantity</td>
                <td class="price">Price</td>
                <td></td>
            </tr>
            </thead>
            <c:forEach var="item" items="${cart.items}" varStatus="status">
                <c:set var="product" value="${item.product}"/>
                <tr>
                    <td>
                        <img class="product-tile"
                             src="${item.product.imageUrl}"
                             alt="error">
                    </td>
                    <td>
                        <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">${item.product.description}</a>
                    </td>
                    <td class="quantity">
                        <input class="quantity" type="text" name="quantity"
                               value="${not empty paramValues.quantity[status.index] ?
                               paramValues.quantity[status.index] : item.quantity}">

                        <c:if test="${not empty requestScope.errors[product.id]}">
                            <p class="error">${requestScope.errors[product.id]}</p>
                        </c:if>
                        <input type="hidden" name="productId" value="${product.id}">
                    </td>
                    <td class="price">
                        <c:if test="${fn:length(item.product.priceHistory) > 0}">
                        <a href="${pageContext.servletContext.contextPath}/price-history/${item.product.id}">
                            </c:if>
                                <fmt:formatNumber value="${item.product.price}" type="currency"
                                                  currencySymbol="${item.product.currency.symbol}"/>
                    </td>
                    <td>
                        <button form="deleteCartItem"
                                formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${item.product.id}">
                            Delete
                        </button>
                    </td>
                </tr>
            </c:forEach>
            <tr>
                <td></td>
                <td></td>
                <td>Total cost</td>
                <td>${cart.totalCost}</td>
            </tr>
        </table>
        <p>
            <button>Update</button>
        </p>
    </form>
    <c:if test="${cart.items.size()!=0}">
    <form action="${pageContext.servletContext.contextPath}/checkout" method="get">
        <button>Checkout</button>
    </form>
</c:if>
    <form id="deleteCartItem" method="post">
    </form>

</tags:master>