<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<c:url var="centralUrl" value="/app/central/home"/>

<html>
<head>
    <meta name="decorator" content="central"/>
    <title><spring:message code="editprofile.title.label"/></title>
</head>

<body>
<p>
    Edit the information that you use to sign into all of your accounts.
</p>

<form id="editProfileForm" action="updateProfile" method="post" class="form-horizontal" role="form">
    <div class="form-group">
        <label class="col-sm-2 control-label">Infusionsoft ID</label>

        <div class="col-sm-4">
            <p class="form-control-static">
                ${fn:escapeXml(user != null ? user.username : '')}
                <span class="help-block"><a href="/app/profile/changePassword">Change Password</a></span>
            </p>
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
</form>
</body>
</html>