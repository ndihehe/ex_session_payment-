<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset = "UTF-8">
    <title>Exercise_6-1</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/style/main.css">
</head>

<body>
    <h1>CD List</h1>
    <table>
        <thead>
        <tr>
            <th>Description</th>
            <th>Price</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="cd" items="${products}">
            <tr>
                <td>${cd.description}</td>
                <td>${cd.price}</td>

                <td>
                    <form action="${pageContext.request.contextPath}/cart" method="post">
                        <input type="hidden"
                               name="productCode"
                               value="${cd.code}">
                        <button type="submit">Add To Cart</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</body>
</html>