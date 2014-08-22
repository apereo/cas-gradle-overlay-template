<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:url var="userSearchUrl" value="/app/support/userSearch"/>

<html>
<head>
    <title>Edit User</title>
    <meta name="decorator" content="central"/>
</head>

<body>

<form:form modelAttribute="user" action="/app/admin/saveUser" cssClass="form-horizontal" role="form">
    <form:hidden path="id" id="id"/>
    <div class="form-group">
        <label for="username" class="col-sm-2 control-label"><spring:message code="infusionsoft.id.label"/></label>

        <div class="col-sm-4">
            <form:input id="username" path="username" cssClass="form-control"/>
        </div>
    </div>
    <div class="form-group">
        <label for="firstName" class="col-sm-2 control-label"><spring:message code="user.firstName.label"/></label>

        <div class="col-sm-4">
            <form:input id="firstName" path="firstName" cssClass="form-control"/>
        </div>
    </div>
    <div class="form-group">
        <label for="lastName" class="col-sm-2 control-label"><spring:message code="user.lastName.label"/></label>

        <div class="col-sm-4">
            <form:input id="lastName" path="lastName" cssClass="form-control"/>
        </div>
    </div>
    <div class="form-group">
        <label for="roles" class="col-sm-2 control-label"><spring:message code="user.authorities.label"/></label>

        <div class="col-sm-4">
            <form:select path="authorities" cssClass="form-control chosen-select" id="roles" multiple="true" data-placeholder="No Roles Selected" name="authorities">
                <c:forEach var="authority" items="${authorities}">
                    <form:option value="${authority}" label="${authority.authority}"/>
                </c:forEach>
            </form:select>
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-6">
            <div class="pull-right">
                <a class="btn btn-default" href="${userSearchUrl}">Cancel</a>
                <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            </div>
        </div>
    </div>
</form:form>

</body>
</html>