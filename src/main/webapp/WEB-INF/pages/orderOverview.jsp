<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Order overview">
    <h1>Order overview</h1>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>Description</td>
            <td class="quantity">Quantity</td>
            <td class="price">Price</td>
        </tr>
        </thead>
        <c:forEach var="item" items="${order.items}" varStatus="status">
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
                        ${item.quantity}
                </td>
                <td class="price">
                    <c:if test="${fn:length(item.product.priceHistory) > 0}">
                    <a href="${pageContext.servletContext.contextPath}/price-history/${item.product.id}">
                        </c:if>
                            <fmt:formatNumber value="${item.product.price}" type="currency"
                                              currencySymbol="${item.product.currency.symbol}"/>
                </td>
            </tr>
        </c:forEach>
        <tr>
            <td colspan="2">Subtotal</td>
            <td colspan="2" class="price">
                <fmt:formatNumber value="${order.subtotal}" type="currency"
                                  currencySymbol="${item.product.currency.symbol}"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">Delivery cost</td>
            <td colspan="2" class="price">
                <fmt:formatNumber value="${order.deliveryCost}" type="currency"
                                  currencySymbol="${item.product.currency.symbol}"/>
            </td>
        </tr>
        <tr>
            <td colspan="2">Total cost</td>
            <td colspan="2" class="price">
                <fmt:formatNumber value="${order.totalCost}" type="currency"
                                  currencySymbol="${item.product.currency.symbol}"/>
            </td>
        </tr>
    </table>
    <h2>Your details</h2>
    <table>
        <tags:orderOverviewRow label="First name" name="firstname" order="${order}"/>
        <tags:orderOverviewRow label="Last name" name="lastname" order="${order}"/>
        <tags:orderOverviewRow label="Phone number" name="phone" order="${order}"/>
        <tags:orderOverviewRow label="Delivery date" name="deliveryDate" order="${order}"/>
        <tags:orderOverviewRow label="Delivery address" name="deliveryAddress" order="${order}"/>
        <tr>
            <td>Payment method</td>
            <td>
                ${order.paymentMethod}
            </td>
        </tr>
    </table>
</tags:master>