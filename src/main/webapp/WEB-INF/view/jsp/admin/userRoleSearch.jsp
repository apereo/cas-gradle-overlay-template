<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="versioned" tagdir="/WEB-INF/tags/common/page" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:set var="editUserUrl" value="${pageContext.request.contextPath}/app/admin/editUser/"/>
<c:set var="unlockUserUrl" value="${pageContext.request.contextPath}/app/support/unlockUser?id="/>
<c:set var="resetPasswordUrl" value="${pageContext.request.contextPath}/app/support/resetPassword?id="/>
<c:set var="resetSecurityQuestionUrl" value="${pageContext.request.contextPath}/app/support/resetSecurityQuestion?id="/>
<c:set var="userRoleSearchUrl" value="${pageContext.request.contextPath}/app/admin/userRoleSearch"/>

<%--@elvariable id="users" type="org.springframework.data.domain.Page<User>"--%>
<%--@elvariable id="authorities" type="java.util.List<org.apereo.cas.infusionsoft.domain.Authority>"--%>
<%--@elvariable id="userRoleSearchForm" type="org.apereo.cas.infusionsoft.web.controllers.commands.UserRoleSearchForm"--%>

<html>
<head>
    <title><spring:message code="search.infusionsoft.id.label"/></title>
    <meta name="decorator" content="central"/>
</head>
<body>
<form:form modelAttribute="userRoleSearchForm" action="${userRoleSearchUrl}" cssClass="form-horizontal" role="form">
    <div class="form-group">
        <label for="roles" class="col-sm-2 control-label"><spring:message code="user.authorities.label"/></label>

        <div class="col-sm-4">
            <form:select path="authority" cssClass="form-control chosen-select" id="roles" data-placeholder="No Roles Selected" name="authorities">
                <c:forEach var="authority" items="${authorities}">
                    <form:option value="${authority}" label="${authority.authority}"/>
                </c:forEach>
            </form:select>
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-6">
            <div class="pull-right">
                <a class="btn btn-default" href="${userRoleSearchUrl}">Cancel</a>
                <button type="submit" class="btn btn-primary"><spring:message code="button.search"/></button>
            </div>
        </div>
    </div>
</form:form>
<div class="table-responsive">
    <table class="table table-bordered table-striped" id="userTable">
        <thead>
        <tr>
            <th>

            </th>
            <th>
                <spring:message code="infusionsoft.id.label"/>
            </th>
            <th>
                <spring:message code="user.full.name.label"/>
            </th>
        </tr>
        </thead>
        <tbody>
        <c:if test="${fn:length(users.content) > 0}">
            <c:forEach var="user" items="${users.content}">
                <tr>
                    <td>
                            ${user.id}
                    </td>
                    <td>
                        <a href="${editUserUrl}${user.id}">
                                ${user.username}
                        </a>
                    </td>
                    <td>
                        <span>${user.firstName} ${user.lastName}</span>
                    </td>
                </tr>
            </c:forEach>
        </c:if>
        </tbody>
    </table>

    <c:if test="${users.totalPages > 1}">
        <%--Pages are 0 based--%>
        <c:set var="searchUrl" value="${pageContext.request.contextPath}/app/admin/userRoleSearch?"/>
        <c:set var="pagesHalfRange" value="${4}"/>
        <ul class="pagination">
                <%--Previous--%>
            <c:if test="${users.firstPage}">
                <li class="disabled"><span>&laquo;</span></li>
            </c:if>
            <c:if test="${!users.firstPage}">
                <li><a href="${searchUrl}page=${users.number - 1}&authority=${userRoleSearchForm.authority}">&laquo;</a></li>
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
                <li class="${pageClass}"><a href="${searchUrl}page=${page}&authority=${userRoleSearchForm.authority}">${page + 1}</a></li>
            </c:forEach>

                <%--Next--%>
            <c:if test="${users.lastPage}">
                <li class="disabled"><span>&raquo;</span></li>
            </c:if>
            <c:if test="${!users.lastPage}">
                <li><a href="${searchUrl}page=${users.number + 1}&authority=${userRoleSearchForm.authority}">&raquo;</a></li>
            </c:if>
        </ul>
    </c:if>
</div>

</body>
</html>