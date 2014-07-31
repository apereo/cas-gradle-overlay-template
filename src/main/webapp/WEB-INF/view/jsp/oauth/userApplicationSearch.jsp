<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="search.mashery.user.applications.label"/></title>
    <meta name="decorator" content="central"/>
</head>
<body>
<div class="text-center">
    <form class="form-search">
        <input type="text" name="username" class="input-large search-query" value="${username}"
               placeholder="Infusionsoft ID">
        <input type="text" name="appName" class="input-medium search-query" value="${appName}" placeholder="App Name">
        <button type="submit" class="btn">Search</button>
    </form>
    <table class="table table-bordered table-striped dataTable" id="userTable">
        <thead>
        <tr>
            <th>
                <spring:message code="mashery.id.label"/>
            </th>
            <th>
                <spring:message code="mashery.name.label"/>
            </th>
            <th>
                <spring:message code="mashery.client_id.label"/>
            </th>
            <th>
                <spring:message code="mashery.access_tokens.label"/>
            </th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${fn:length(userApplications) > 0}">
            <c:forEach var="masheryUserApplication" items="${userApplications}">
                <tr>
                    <td>
                            ${userApplication.id}
                    </td>
                    <td>
                            ${userApplication.name}
                    </td>
                    <td>
                            ${userApplication.client_id}
                    </td>
                    <td>
                        <ul>
                            <c:forEach var="accessToken" items="${userApplication.accessTokens}">
                                <li>
                                    <a href="/app/oauth/viewAccessToken?accessToken=${accessToken}">${accessToken}</a>
                                </li>
                            </c:forEach>
                        </ul>
                    </td>
                </tr>
            </c:forEach>
        </c:if>
        </tbody>
    </table>
</div>
</body>
</html>