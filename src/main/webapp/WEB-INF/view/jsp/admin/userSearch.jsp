<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin - User Search</title>

    <meta name="decorator" content="central"/>

</head>
<body>

<div style="width: 100%">
    <form>
        <input type="text" name="username"/>
        <button type="submit">Search</button>
    </form>
</div>

<c:if test="${!empty users}">
    <table>
        <tr>
            <th>
                Infusionsoft ID
            </th>
            <th>
                Name
            </th>
            <th>
                Reset Password
            </th>
            <th>
                Unlock
            </th>
        </tr>

        <c:forEach var="user" items="${users}">
            <tr>
                <td>
                        ${user.username}
                </td>
                <td>
                        ${user.firstName} ${user.lastName}
                </td>
                <td>
                    <a href="/app/admin/resetPassword?id=${user.id}">Reset Password</a>
                </td>
                <td>
                    <a href="/app/admin/unlockUser?id=${user.id}">Unlock</a>
                </td>
            </tr>
        </c:forEach>

    </table>
</c:if>
</body>
</html>