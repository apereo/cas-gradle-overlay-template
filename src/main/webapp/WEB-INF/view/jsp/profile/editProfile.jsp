<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<c:set var="centralUrl" value="${pageContext.request.contextPath}/app/central/home"/>
<c:set var="changePasswordUrl" value="${pageContext.request.contextPath}/app/profile/changePassword"/>

<%--@elvariable id="user" type="com.infusionsoft.cas.domain.User"--%>

<html>
<head>
    <meta name="decorator" content="central"/>
    <title><spring:message code="editprofile.title.label"/></title>
</head>

<body>
<p>
    Edit the information that you use to sign into all of your accounts.
</p>

<form:form id="editProfileForm" action="updateProfile" method="post" class="form-horizontal" role="form" modelAttribute="editProfileForm">
    <div class="form-group">
        <label class="col-sm-2 control-label">Infusionsoft ID</label>

        <div class="col-sm-4">
            <input class="form-control" id="username" name="username" value="${fn:escapeXml(user != null ? user.username : '')}" type="email"/>
            <%--<p class="form-control-static">--%>
                    <%--${fn:escapeXml(user != null ? user.username : '')}--%>
                <span class="help-block"><a href="${changePasswordUrl}">Change Password</a></span>
            <%--</p>--%>
        </div>

    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label" for="firstName">First Name</label>

        <div class="col-sm-4">
            <input class="form-control" id="firstName" name="firstName" value="${fn:escapeXml(user != null ? user.firstName : '')}" type="text"/>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label" for="lastName">Last Name</label>

        <div class="col-sm-4">
            <input class="form-control" id="lastName" name="lastName" value="${fn:escapeXml(user != null ? user.lastName : '')}" type="text"/>
        </div>
    </div>
    <div class="form-group">
        <div class="col-sm-6">
            <div class="pull-right">
                <a class="btn btn-default" href="${centralUrl}">
                    <spring:message code="button.cancel"/>
                </a>
                <button type="submit" class="btn btn-primary "><spring:message code="button.save"/></button>
            </div>
        </div>
    </div>

</form:form>
</body>
</html>