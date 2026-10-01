<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Registration</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/style/main.css">
</head>
<body>
    <h1>User Registration</h1>
    <p>To add items to your cart, please enter your name and email address below.</p>

    <c:if test="${not empty message}">
        <p style="color: red;">${message}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/register" method="post" style="max-width: 450px;">
        <input type="hidden" name="productCode" value="${productCode}">

        <p>
            <label style="display: inline-block; width: 100px;"><b>Username:</b></label>
            <input type="text" name="username" required>
        </p>
        <p>
            <label style="display: inline-block; width: 100px;"><b>Password:</b></label>
            <input type="password" name="password" required>
        </p>
        <p>
            <label style="display: inline-block; width: 100px;"><b>Email:</b></label>
            <input type="email" name="email" required>
        </p>
        <div style="margin-top: 15px;">
            <button type="submit">Register & Continue</button>
        </div>
    </form>

    <a href="${pageContext.request.contextPath}/home">
        <button type="button">Back to CD List</button>
    </a>
</body>
</html>
