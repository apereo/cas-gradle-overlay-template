<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="versioned" tagdir="/WEB-INF/tags/common/page" %>

<%--@elvariable id="supportPhoneNumbers" type="java.util.List<String>"--%>

<!doctype html>
<html>
<head>
    <meta name="decorator" content="login"/>
    <meta name="robots" content="noindex">
    <title>Password Reset</title>
</head>

<body>
<div class="container">
    <div class="row">
        <div class="rounded-box">
            <versioned:objectSvg type="image/svg+xml" tabindex="-1" data="/img/is_logo.svg" width="159" height="26" cssClass="logo">Infusionsoft</versioned:objectSvg>

            <h2>Password Reset</h2>

            <form:form action="recover" method="post" id="forgotPasswordForm" class="form-horizontal" role="form">

                <c:if test="${not empty error}">
                    <p class="text-error">
                        <versioned:objectSvg type="image/svg+xml" tabindex="-1" data="/img/ic-exclamation-circle.svg" width="16" height="16"/>
                        <spring:message code="${error}"/>
                    </p>
                </c:if>

                <div class="form-group ">
                    <div class="col-md-12">
                        <span class="ic-envelope"></span>
                        <input type="email" class="form-control" id="username" name="username" placeholder="email@example.com" size="25" tabindex="1" value="${fn:escapeXml(username)}"/>
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-md-12">
                        <button class="btn btn-success btn-block" type="submit">Next</button>
                    </div>
                </div>
            </form:form>
            <c:set var="loginUrl" value="${pageContext.request.contextPath}/login"/>
            <a href="${loginUrl}">Back to Sign In</a>
        </div>
    </div>
    <div class="row">
        <div class="need-help col-md-12">
            <p>Need help? Call toll free:
                <c:forEach var="supportPhoneNumber" items="${supportPhoneNumbers}">
                    <br>${supportPhoneNumber}
                </c:forEach>
            </p>
        </div>
    </div>
</div>

<content tag="local_script">
    <versioned:script type="text/javascript" src="${pageContext.request.contextPath}/js/password-forgot.js"/>
</content>

</body>
</html>