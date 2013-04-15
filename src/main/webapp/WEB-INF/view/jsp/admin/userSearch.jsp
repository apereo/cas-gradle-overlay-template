<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Infusionsoft ID Search</title>
    <meta name="decorator" content="central"/>
</head>
<body>
<div class="text-center">
    <table class="table table-bordered table-striped dataTable" id="userTable">
        <thead>
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
        </thead>
        <tbody>
        <c:forEach var="user" items="${users.content}">
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
        </tbody>
    </table>
    <c:if test="${users.totalPages != 1}">
        <div class="pagination pagination-centered">
                <%--Pages are 0 based--%>
            <c:url var="searchUrl" value="/app/admin/userSearch?"/>
            <c:set var="pagesHalfRange" value="${4}"/>
            <ul>
                    <%--Previous--%>
                <c:if test="${users.firstPage}">
                    <li class="disabled"><span>&laquo;</span></li>
                </c:if>
                <c:if test="${!users.firstPage}">
                    <li><a href="${searchUrl}page=${users.number - 1}">&laquo;</a></li>
                </c:if>

                    <%--Pages--%>
                <c:choose>
                    <c:when test="${users.number < 4}">
                        <c:set var="begin" value="${0}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="begin" value="${users.number - 4}"/>
                    </c:otherwise>
                </c:choose>


                <c:choose>
                    <c:when test="${begin + 8  < users.totalPages}">
                        <c:set var="end" value="${begin + 8}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="end" value="${users.totalPages - 1}"/>

                        <c:if test="${end - 8 >= 0}">
                            <c:set var="begin" value="${end - 8}"/>
                        </c:if>
                    </c:otherwise>
                </c:choose>

                <c:forEach var="page" begin="${begin}" end="${end}">
                    <c:set var="pageClass" value="${users.number == page ? 'active' : ''}"/>
                    <li class="${pageClass}"><a href="${searchUrl}page=${page}">${page + 1}</a></li>
                </c:forEach>

                    <%--Next--%>
                <c:if test="${users.lastPage}">
                    <li class="disabled"><span>&raquo;</span></li>
                </c:if>
                <c:if test="${!users.lastPage}">
                    <li><a href="${searchUrl}page=${users.number + 1}">&raquo;</a></li>
                </c:if>
            </ul>
        </div>
    </c:if>
</div>
</body>
</html>