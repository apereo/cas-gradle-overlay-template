<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="versioned" tagdir="/WEB-INF/tags/common/page" %>

<%--@elvariable id="adLinkUrl" type="java.lang.String"--%>
<%--@elvariable id="adDesktopImageSrcUrl" type="java.lang.String"--%>
<%--@elvariable id="adMobileImageSrcUrl" type="java.lang.String"--%>
<%--@elvariable id="enableAds" type="java.lang.Boolean"--%>
<%--@elvariable id="appType" type="com.infusionsoft.cas.domain.AppType"--%>
<%--@elvariable id="appUrl" type="java.lang.String"--%>
<%--@elvariable id="supportPhoneNumbers" type="java.util.List<String>"--%>

<c:set var="getLogoImageUrl" value="${pageContext.request.contextPath}/app/registration/getLogoImageUrl"/>
<c:set var="forgotPasswordUrl" value="${pageContext.request.contextPath}/app/registration/forgot"/>

<c:set var="adLinkHref" value="${adLinkUrl}"/>
<c:set var="adDesktopImageUrl" value="${adDesktopImageSrcUrl}"/>
<c:set var="adMobileImageUrl" value="${adMobileImageSrcUrl}"/>

<c:set var="adClass" value="${enableAds ? '' : 'noImage'}"/>

<!-- If they come from the CRM we want to redirect them to the old login -->
<c:choose>
    <c:when test="${appType == 'CRM'}">
        <c:set var="loginMessage"><spring:message code="login.redirect.message"/></c:set>
        <c:set var="registrationUrl" value="${appUrl}/app/authentication/login?msg=${loginMessage}"/>
    </c:when>
    <c:otherwise>
        <c:set var="registrationUrl" value="${pageContext.request.contextPath}/app/registration/createInfusionsoftId"/>
    </c:otherwise>
</c:choose>

<html>
<head>
    <meta name="decorator" content="login"/>
    <link type="text/css" rel="stylesheet" href="https://infusionmedia.s3.amazonaws.com/app/login-screen/cas-holiday.css">
    <title>Sign in to Infusionsoft</title>
</head>

<body>

<div class="container">
    <div class="row">


        <c:if test="${enableAds}">
            <div class="col-md-6 ">
                <a href="${adLinkHref}">
                    <versioned:img src="${adDesktopImageUrl}" cssClass="iconAd img-responsive"/>
                </a>
            </div>
        </c:if>
        <div class="col-md-6 login-form ${adClass}">
            <versioned:objectSvg tabindex="-1" data="/img/is_logo.svg" width="159" height="26" cssClass="logo">Infusionsoft</versioned:objectSvg>

            <form:form method="post" action="/login" id="loginForm" cssClass="form-horizontal" commandName="${commandName}" htmlEscape="true" role="form" data-toggle="validator">
                <form:errors path="*" id="msg" cssClass="text-error" element="p">
                    <p class="text-error">
                        <versioned:objectSvg tabindex="-1" data="/img/ic-exclamation-circle.svg" width="16" height="16"/>
                        <c:forEach var="error" items="${messages}">
                            <c:out value="${error}"/>
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
                        <button class="btn btn-primary btn-block" type="submit">Log In</button>
                    </div>
                </div>
            </form:form>
            <a href="${forgotPasswordUrl}">Forgot your password?</a>

            <div class="row infusionsoftID">
                <versioned:objectSvg tabindex="-1" data="/img/isID_logo.svg" width="60" height="30" cssClass="isIDlogo">Infusionsoft ID</versioned:objectSvg>
                <p>Haven't created your Infusionsoft ID?</p>

                <p><a href="${registrationUrl}">Click here to get started</a></p>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12 login-footer ${adClass}">
            <p>Need help? Call toll free:
                <c:forEach var="supportPhoneNumber" items="${supportPhoneNumbers}">
                    <br>${supportPhoneNumber}
                </c:forEach>
            </p>

            <c:if test="${not empty appUrl && appType == 'CRM'}">
                <p>
                    <a href="${appUrl}/Affiliate/">Referral Partner Sign-in</a>
                </p>
            </c:if>
        </div>

    </div>
    <c:if test="${enableAds}">
        <div class="navbar navbar-default navbar-fixed-bottom">
            <a href="${adLinkHref}">
                <versioned:img src="${adMobileImageUrl}" cssClass="iconAdSm img-responsive"/>
            </a>
        </div>
    </c:if>
</div>

<content tag="local_script">
    <versioned:script type="text/javascript" src="${pageContext.request.contextPath}/js/login.js"/>
</content>

</body>
</html>