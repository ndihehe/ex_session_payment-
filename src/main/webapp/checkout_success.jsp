<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Payment Result</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/main.css">
</head>
<body>
    <h1>Order & Payment Status</h1>

    <c:choose>
        <c:when test="${order.paymentStatus eq 'SUCCESS'}">
            <div style="background-color: #e6f4ea; border: 1px solid #34a853; padding: 15px; border-radius: 4px; margin-bottom: 20px; max-width: 800px;">
                <h2 style="color: #137333; margin-top: 0;">Payment Successful!</h2>
                <p>Thank you, <b>${order.customerName}</b>. Your order has been placed and paid successfully.</p>
                <p><b>Order ID:</b> #${order.orderId}</p>
                <p><b>Transaction Reference:</b> ${order.transactionNo}</p>
                <p><b>Payment Method:</b> ${order.paymentMethod eq 'vnpay' ? 'VNPay Sandbox' : 'Test Simulator'}</p>
                <p><b>Total Amount Paid:</b> $${order.totalAmount}</p>

                <c:choose>
                    <c:when test="${emailSent}">
                        <p style="color: #137333;"><b>Email Confirmation:</b> An invoice has been successfully sent to <b>${order.customerEmail}</b>.</p>
                    </c:when>
                    <c:otherwise>
                        <p style="color: #b06000;"><b>Email Notice:</b> Payment is recorded. Email notification was not sent (environment variables MAIL_USERNAME/MAIL_PASSWORD or RESEND_API_KEY not configured).</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <c:if test="${not empty order.items}">
                <h3>Purchased Items</h3>
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
                        <c:forEach var="item" items="${order.items}">
                            <tr>
                                <td>${item.product.description}</td>
                                <td>${item.product.price}</td>
                                <td>${item.quantity}</td>
                                <td>${item.total}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <a href="${pageContext.request.contextPath}/home">
                <button type="button">Continue Shopping</button>
            </a>
        </c:when>

        <c:otherwise>
            <div style="background-color: #fce8e6; border: 1px solid #ea4335; padding: 15px; border-radius: 4px; margin-bottom: 20px; max-width: 800px;">
                <h2 style="color: #c5221f; margin-top: 0;">Payment Failed</h2>
                <p>${errorMessage}</p>
                <p>Order ID: #${order.orderId}</p>
            </div>

            <a href="${pageContext.request.contextPath}/checkout">
                <button type="button">Try Again</button>
            </a>
            <a href="${pageContext.request.contextPath}/cart">
                <button type="button">Back to Cart</button>
            </a>
        </c:otherwise>
    </c:choose>
</body>
</html>
