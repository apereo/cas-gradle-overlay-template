<%--@elvariable id="user" type="com.infusionsoft.cas.domain.User"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<c:url var="editProfileUrl" value="/app/profile/editProfile"/>
<c:url var="alertImage" value="/img/ic-message-danger.png"/>

<html>
<head>
    <title><spring:message code="profile.changePassword.title"/></title>
    <meta name="decorator" content="central"/>
</head>

<body>

<p>
    Change your password that is used to sign into all of your accounts.
</p>

<form:form id="changePasswordForm" action="updatePassword" cssClass="form-horizontal" role="form">
    <input id="username" name="username" value="${user.username}" type="hidden"/>
    <input id="redirectFrom" name="redirectFrom" value="changePassword" type="hidden"/>

    <div class="form-group">
        <label class="col-sm-2 control-label" for="password1"><spring:message code="password.password1.label"/></label>

        <div class="col-sm-4">
            <input class="form-control" id="password1" name="password1" value="" type="password" autocomplete="off"/>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label" for="password2"><spring:message code="password.password2.label"/></label>

        <div class="col-sm-4">
            <input class="form-control" id="password2" name="password2" value="" type="password" autocomplete="off"/>
        </div>
    </div>
    <div class="form-group">
        <label class="col-sm-2 control-label" for="currentPassword"><spring:message code="password.currentPassword.label"/></label>

        <div class="col-sm-4">
            <input class="form-control" id="currentPassword" name="currentPassword" value="" type="password" autocomplete="off"/>
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-6">
            <div class="pull-right">
                <a class="btn btn-default" href="${editProfileUrl}">Cancel</a>
                <button type="submit" class="btn btn-primary"><spring:message code="button.save"/></button>
            </div>
        </div>
    </div>
    <%--</form>--%>
</form:form>
</body>
</html>
