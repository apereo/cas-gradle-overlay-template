<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url var="getLogoImageUrl" value="/app/registration/getLogoImageUrl"/>
<c:url var="forgotPasswordUrl" value="/app/registration/forgot"/>
<c:url var="adDesktopImageUrl" value="/img/icon_ad.png"/>
<c:url var="adMobileImageUrl" value="/img/icon_ad_sm.png"/>

<!-- If they come from the CRM we want to redirect them to the old login -->
<c:choose>
    <c:when test="${appType == 'CRM'}">
        <c:set var="loginMessage"><spring:message code="login.redirect.message"/></c:set>
        <c:url var="registrationUrl" value="${appUrl}/app/authentication/login?msg=${loginMessage}"/>
    </c:when>
    <c:otherwise>
        <c:url var="registrationUrl" value="/app/registration/createInfusionsoftId"/>
    </c:otherwise>
</c:choose>

<html>
<head>
    <meta name="decorator" content="base-bootstrap3"/>
    <link type="text/css" rel="stylesheet" href="https://infusionmedia.s3.amazonaws.com/app/login-screen/cas-holiday.css">
    <title>Sign in to Infusionsoft</title>
</head>

<body>

<div class="container">
    <div class="row">
        <div class="col-md-6">
            <!-- This image is meant to be a placeholder only -->
            <img src="${adDesktopImageUrl}" class="iconAd img-responsive" alt="Get your tickets for ICON14 now">
            <!-- End -->
        </div>
        <div class="col-md-6 login-form">
            <object type="image/svg+xml" tabindex="-1" data="/img/is_logo.svg" width="159" height="26" class="logo">Infusionsoft</object>

            <form:form method="post" id="loginForm" cssClass="form-horizontal" commandName="${commandName}" htmlEscape="true" role="form" data-toggle="validator">
                <form:errors path="*" id="msg" cssClass="text-error" element="p">
                    <p class="text-error">
                        <object type="image/svg+xml" tabindex="-1" data="/img/ic-exclamation-circle.svg" width="16" height="16"></object>
                        <c:forEach var="error" items="${messages}">
                            ${error}
                        </c:forEach>
                    </p>
                </form:errors>

                <input type="hidden" name="lt" value="${loginTicket}"/>
                <input type="hidden" name="execution" value="${flowExecutionKey}"/>
                <input type="hidden" name="_eventId" value="submit"/>
                <input type="hidden" name="app_version" value="${appVersion}"/>

                <div class="form-group">
                    <div class="col-md-10">
                        <span class="ic-envelope"></span>
                        <form:input type="email" cssClass="form-control" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" htmlEscape="true" placeholder="email@example.com"/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-md-10">
                        <span class="ic-lock"></span>
                        <form:password cssClass="form-control" id="password" size="25" tabindex="2" path="password" accesskey="${passwordAccessKey}" htmlEscape="true" placeholder="password"/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-md-10">
                        <button class="btn btn-success btn-block" type="submit">Log In</button>
                    </div>
                </div>
            </form:form>
            <a href="${forgotPasswordUrl}">Forgot your password?</a>

            <div class="row infusionsoftID">
                <object type="image/svg+xml" tabindex="-1" data="/img/isID_logo.svg" width="60" height="30" class="isIDlogo">Infusionsoft ID</object>
                <p>Haven't created your Infusionsoft ID?</p>

                <p><a href="${registrationUrl}">Click here to get started</a></p>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12 login-footer">
            <p>Need Help? Call <strong>${supportPhoneNumber}</strong>.</p>
        </div>
    </div>
    <div class="navbar navbar-default navbar-fixed-bottom">
        <!-- This image is meant to be a placeholder only -->
        <img src="${adMobileImageUrl}" class="iconAdSm img-responsive" alt="Get your tickets for ICON14 now">
        <!-- End -->
    </div>
</div>

<content tag="local_script">
    <script type="text/javascript" src="<c:url value="/js/login.js"/>"></script>
</content>

</body>
</html>