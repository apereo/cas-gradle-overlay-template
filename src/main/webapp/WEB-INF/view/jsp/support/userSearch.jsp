<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="search.infusionsoft.id.label"/></title>
    <meta name="decorator" content="central"/>
    <script type="text/javascript">
        var userSearch = {
            getAppsGrantedAccessToAccount: function (userId, accountId) {
                var appsGrantedAccessToAccountInput = new Object();
                appsGrantedAccessToAccountInput.afterSuccess = this.appsGrantedAccessToAccountAfterSuccess;
                appsGrantedAccessToAccountInput.afterError = this.appsGrantedAccessToAccountAfterError;
                appsGrantedAccessToAccountInput.userId = userId;
                appsGrantedAccessToAccountInput.accountId = accountId;

                manageAppAccess.getAppsGrantedAccessToAccount(appsGrantedAccessToAccountInput);
            },
            appsGrantedAccessToAccountAfterSuccess: function (inputObject, response) {
                $(".displayManageAccountsMarker").each(function () {
                    $(this).hide()
                });
                $("#displayManageAccountsContent-" + inputObject.accountId).html(response);
                $("#displayManageAccountsWrapper-" + inputObject.accountId).slideDown(500);
                $(".open-marker").each(function () {
                    $(this).removeClass('open')
                });
                $("#accountAnchor-" + inputObject.accountId).addClass("open");
            },
            appsGrantedAccessToAccountAfterError: function (inputObject, response) {

            }
        }
    </script>

</head>
<body>
<div class="table-responsive">
    <table class="table table-bordered table-striped" id="userTable">
        <thead>
        <tr>
            <th>
                <spring:message code="infusionsoft.id.label"/>
            </th>
            <th>
                <spring:message code="user.full.name.label"/>
            </th>
            <th>
                <spring:message code="reset.password.label"/>
            </th>
            <th>
                <spring:message code="unlock.label"/>
            </th>
            <th>
                <spring:message code="accounts.label"/>
            </th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${fn:length(users.content) > 0}">
            <c:forEach var="user" items="${users.content}">
                <tr>
                    <td>
                        <sec:authorize access="hasRole('ROLE_CAS_ADMIN')">
                        <a href="/app/admin/editUser/${user.id}">
                            </sec:authorize>
                                ${user.username}
                            <sec:authorize access="hasRole('ROLE_CAS_ADMIN')">
                        </a>
                        </sec:authorize>
                    </td>
                    <td>
                        <span>${user.firstName} ${user.lastName}</span>
                    </td>
                    <td>
                        <a href="/app/support/resetPassword?id=${user.id}"><spring:message code="reset.password.label"/></a>
                    </td>
                    <td>
                        <a href="/app/support/unlockUser?id=${user.id}"><spring:message code="unlock.label"/></a>
                    </td>
                    <td class="account-list">
                        <ul class="list-group">
                            <c:forEach var="account" items="${user.accounts}">
                                <li class="list-group-item">
                                    <span><a id="accountAnchor-${account.id}" class="open-marker" onclick="userSearch.getAppsGrantedAccessToAccount('${user.id}', '${account.id}'); return false;">${account.appName}.${crmDomain}</a></span>
                                </li>
                            </c:forEach>
                        </ul>
                            <%--<table class="table table-bordered table-striped dataTable">--%>
                            <%--<c:forEach var="account" items="${user.accounts}">--%>
                            <%--<tr>--%>
                            <%--<td>--%>
                            <%--<span><a id="accountAnchor-${account.id}" class="open-marker" onclick="userSearch.getAppsGrantedAccessToAccount('${user.id}', '${account.id}'); return false;">${account.appName}.${crmDomain}</a></span>--%>
                            <%--</td>--%>
                            <%--</tr>--%>
                            <%--<tr id="displayManageAccountsWrapper-${account.id}" class="displayManageAccountsMarker" style="display: none">--%>
                            <%--<td id="displayManageAccountsContent-${account.id}"></td>--%>
                            <%--</tr>--%>
                            <%--</c:forEach>--%>
                            <%--</table>--%>
                    </td>
                </tr>
            </c:forEach>
        </c:if>
        </tbody>
    </table>
    <c:if test="${users.totalPages > 1}">
        <%--Pages are 0 based--%>
        <c:url var="searchUrl" value="/app/support/userSearch?"/>
        <c:set var="pagesHalfRange" value="${4}"/>
        <ul class="pagination">
                <%--Previous--%>
            <c:if test="${users.firstPage}">
                <li class="disabled"><span>&laquo;</span></li>
            </c:if>
            <c:if test="${!users.firstPage}">
                <li><a href="${searchUrl}page=${users.number - 1}&searchUsername=${searchUsername}">&laquo;</a></li>
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
                <li class="${pageClass}"><a href="${searchUrl}page=${page}&searchUsername=${searchUsername}">${page + 1}</a></li>
            </c:forEach>

                <%--Next--%>
            <c:if test="${users.lastPage}">
                <li class="disabled"><span>&raquo;</span></li>
            </c:if>
            <c:if test="${!users.lastPage}">
                <li><a href="${searchUrl}page=${users.number + 1}&searchUsername=${searchUsername}">&raquo;</a></li>
            </c:if>
        </ul>
    </c:if>
</div>
</body>
</html>