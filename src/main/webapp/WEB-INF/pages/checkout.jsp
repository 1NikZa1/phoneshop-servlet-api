<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Checkout">
    <p></p>
    <c:if test="${not empty errors}">
        <p class="error">Order confirmation error</p>
    </c:if>
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
    <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
        <table>
            <tags:orderFormRow label="First name" name="firstname" errors="${errors}"/>
            <tags:orderFormRow label="Last name" name="lastname" errors="${errors}"/>
            <tags:orderFormRow label="Phone number" name="phone" errors="${errors}"/>
            <tags:orderFormRow label="Delivery date" name="deliveryDate" errors="${errors}"/>
            <tags:orderFormRow label="Delivery address" name="deliveryAddress" errors="${errors}"/>

            <tr>
                <td>Payment method<span style="color: red">*</span></td>
                <td>
                    <label>
                        <select name="paymentMethod">
                            <option>${param['paymentMethod']}</option>
                            <c:forEach var="paymentMethod" items="${paymentMethods}">
                                <c:if test="${paymentMethod != param['paymentMethod']}">
                                    <option>${paymentMethod}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </label>
                    <c:set var="error" value="${errors.get('paymentMethod')}"/>
                    <c:if test="${not empty error}">
                        <p class="error">
                                ${error}
                        </p>
                    </c:if>
                </td>
            </tr>
        </table>
        <p>
            <button>Place order</button>
        </p>
    </form>

</tags:master>