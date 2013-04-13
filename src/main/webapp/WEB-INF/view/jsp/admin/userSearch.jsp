<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Infusionsoft ID Search</title>

    <meta name="decorator" content="central"/>

</head>
<body>

<form class=".form-search">
    <fieldset>
        <legend>Search</legend>
        <div class="input-append">
            <input type="text" class="input-medium search-query span2" name="username" placeholder="Infusionsoft ID" value="${username}"/>
            <button type="submit" class="btn"><i class="icon-search"></i></button>
        </div>
    </fieldset>
</form>

<c:if test="${!empty users}">
    <table class="table table-bordered table-striped">
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