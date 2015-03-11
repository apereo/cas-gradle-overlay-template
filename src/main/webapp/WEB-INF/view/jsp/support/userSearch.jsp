<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:url var="editUserUrl" value="/app/admin/editUser/"/>
<c:url var="unlockUserUrl" value="/app/support/unlockUser?id="/>
<c:url var="resetPasswordUrl" value="/app/support/resetPassword?id="/>
<c:url var="resetSecurityQuestionUrl" value="/app/support/resetSecurityQuestion?id="/>

<%--@elvariable id="users" type="org.springframework.data.domain.Page<User>"--%>
<%--@elvariable id="searchUsername" type="java.lang.String"--%>
<%--@elvariable id="crmDomain" type="java.lang.String"--%>
<%--@elvariable id="crmPort" type="java.lang.Integer"--%>

<html>
<head>
    <title><spring:message code="search.infusionsoft.id.label"/></title>
    <meta name="decorator" content="central"/>
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
                <spring:message code="securityQuestion.label"/>
            </th>
            <sec:authorize url="${unlockUserUrl}">
                <th>
                    <spring:message code="supportTools.label"/>
                </th>
            </sec:authorize>
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
                        <sec:authorize url="${editUserUrl}${user.id}">
                        <a href="${editUserUrl}${user.id}">
                            </sec:authorize>
                                ${user.username}
                            <sec:authorize url="${editUserUrl}${user.id}">
                        </a>
                        </sec:authorize>
                    </td>
                    <td>
                        <span>${user.firstName} ${user.lastName}</span>
                    </td>
                    <td>
                        <c:forEach var="securityQuestionResponse" items="${user.securityQuestionResponses}">
                            <div>${securityQuestionResponse.securityQuestion.question}</div>
                            <a href="#" class="showAnswer"
                               data-trigger="focus"
                               data-toggle="popover"
                               data-placement="bottom"
                               data-content="${securityQuestionResponse.response}">
                                <spring:message code="showAnswer.label"/>
                            </a>
                        </c:forEach>
                    </td>
                    <sec:authorize url="${unlockUserUrl}${user.id}">
                        <td>
                            <ul>
                                <li><a href="${unlockUserUrl}${user.id}"><spring:message code="unlock.label"/></a><br/></li>
                                <li><a href="${resetPasswordUrl}${user.id}"><spring:message code="reset.password.label"/></a><br/></li>
                                <li><a href="${resetSecurityQuestionUrl}${user.id}"><spring:message code="reset.securityQuestion.label"/></a></li>
                            </ul>
                        </td>
                    </sec:authorize>
                    <td class="account-list">
                        <ul class="list-group">
                            <c:forEach var="account" items="${user.accounts}">
                                <li class="list-group-item">
                                    <span><a href="https://${account.appName}.${crmDomain}:${crmPort}">${account.appName}.${crmDomain}</a></span>
                                </li>
                            </c:forEach>
                        </ul>
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

<content tag="local_script">
    <script type="text/javascript" src="<c:url value="/js/userSearch.js"/>"></script>
</content>

</body>
</html>