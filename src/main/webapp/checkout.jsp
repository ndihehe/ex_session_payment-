<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Checkout</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/style/main.css">
    </head>

    <body>
        <h1>CheckOut</h1>

        <c:if test="${not empty errorMessage}">
            <p style="color: red;">${errorMessage}</p>
        </c:if>

        <table>
            <thead>
                <tr>
                    <th>Description</th>
                    <th>Price</th>
                    <th>Quantity</th>
                    <th>Amount</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${cart.items}">
                <tr>
                    <td>${item.product.description}</td>
                    <td>${item.product.price}</td>
                    <td>${item.quantity}</td>
                    <td>${item.total}</td>
                </tr>
                </c:forEach>
            </tbody>
        </table>

        <h3>Total: $${cart.total}</h3>

        <div style="background-color: #f9f9f9; border: 1px solid #ddd; padding: 12px 16px; margin: 20px 0; max-width: 600px;">
            <h3 style="margin-top: 0; color: #006666;">Customer Information</h3>
            <p style="margin: 6px 0;"><b>Username:</b> ${sessionScope.user.username}</p>
            <p style="margin: 6px 0;"><b>Email to receive receipt:</b> ${sessionScope.user.email}</p>
        </div>

        <form action="${pageContext.request.contextPath}/checkout" method="post" style="max-width: 600px;">
            <p>
                <label style="display: inline-block; width: 140px;"><b>Payment Method:</b></label>
                <label style="margin-right: 15px;">
                    <input type="radio" name="paymentMethod" value="vnpay" checked>
                    VNPay Sandbox Gateway
                </label>
                <label>
                    <input type="radio" name="paymentMethod" value="simulator">
                    Test Simulator (Instant)
                </label>
            </p>
            <div style="margin-top: 15px;">
                <button type="submit">Proceed to Payment</button>
            </div>
        </form>

        <a href="${pageContext.request.contextPath}/cart">
            <button type="button">Back to Cart</button>
        </a>
        <a href="${pageContext.request.contextPath}/home">
            <button type="button">Continue Shopping</button>
        </a>
    </body>
</html>