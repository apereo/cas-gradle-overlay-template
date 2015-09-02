<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--@elvariable id="supportPhoneNumbers" type="java.util.List<String>"--%>

<!doctype html>
<html>
<head>
    <meta name="decorator" content="login"/>
    <meta name="robots" content="noindex">
    <title>Enter Recovery Code</title>
</head>
<body>

<div class="container">
    <div class="row">
        <div class="rounded-box">
            <object type="image/svg+xml" tabindex="-1" data="/img/is_logo.svg" width="159" height="26" class="logo">Infusionsoft</object>

            <h2>Enter Recovery Code</h2>

            <c:if test="${not empty error}">
                <p class="text-error">
                    <object type="image/svg+xml" tabindex="-1" data="/img/ic-exclamation-circle.svg" width="16" height="16"></object>
                    <spring:message code="${error}"/>
                </p>
            </c:if>

            <c:if test="${empty error}">
                <p class="text-info">
                    <object type="image/svg+xml" tabindex="-1" data="/img/ic-message-info.svg" width="16" height="16"></object>
                    We have sent a recovery code to ${fn:escapeXml(username)}. Type it into the field below.
                </p>
            </c:if>

            <form:form action="recover" method="post" id="recoverPasswordForm" class="form-horizontal" role="form">

                <div class="form-group">
                    <div class="col-md-12">
                        <input type="text" class="form-control" id="recoveryCode" name="recoveryCode" placeholder="recovery code" size="25" tabindex="1" />
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
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/password-recover.js"></script>
</content>

</body>
</html>