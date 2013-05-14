<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit User</title>
    <meta name="decorator" content="central"/>
</head>
<body>
<div>

    <form:errors path="*" cssClass="errors"/>

    <form:form modelAttribute="user" cssClass="form-horizontal" action="/app/admin/saveUser">
        <form:hidden path="id" id="id"/>
        <div class="control-group">
            <label class="control-label" for="username"><spring:message code="infusionsoft.id.label"/></label>

            <div class="controls">
                <form:input id="username" path="username"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="firstName"><spring:message code="user.firstName.label"/></label>

            <div class="controls">
                <form:input id="firstName" path="firstName"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label" for="lastName"><spring:message code="user.lastName.label"/></label>

            <div class="controls">
                <form:input id="lastName" path="lastName"/>
            </div>
        </div>
        <div class="control-group">
            <label class="control-label"><spring:message code="user.authorities.label"/></label>

            <div class="controls">
                <c:forEach var="authority" items="${authorities}">
                    <label class="checkbox">
                        <form:checkbox path="authorities" value="${authority}" label="${authority.authority}" />
                    </label>
                </c:forEach>

            </div>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            <a href="/app/support/userSearch">
                <button type="button" class="btn"><spring:message code="button.cancel"/></button>
            </a>
        </div>
    </form:form>
</div>
</body>
</html>