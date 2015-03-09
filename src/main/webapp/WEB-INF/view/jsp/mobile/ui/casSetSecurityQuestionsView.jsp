<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--@elvariable id="supportPhoneNumbers" type="java.util.List<String>"--%>

<head>
    <meta name="decorator" content="login"/>
    <meta name="robots" content="noindex">
    <title>Security Questions</title>
</head>
<body>

<div class="container">
    <div class="rounded-box-wide">
        <div class="row">
            <div class="col-sm-8">
                <object type="image/svg+xml" tabindex="-1" data="/img/is_logo.svg" width="159" height="26" class="logo">Infusionsoft</object>

                <h2>You need to set some security questions</h2>

                <p class="text-info">
                    <object type="image/svg+xml" tabindex="-1" data="/img/ic-message-info.svg" width="16" height="16"></object>
                    <spring:message code="security.question.not.set.page.instructions"/>
                </p>

                <%--<form id="resetPasswordForm" class="form-horizontal">--%>

                    <%--<input id="username" name="username" type="hidden" value="${credentials.username}"/>--%>
                    <%--<input id="currentPassword" name="currentPassword" type="hidden" value="${credentials.password}"/>--%>
                    <%--<input id="redirectFrom" name="redirectFrom" value="expirePassword" type="hidden"/>--%>
                    <%--<input id="service" name="service" value="${service}" type="hidden"/>--%>

                    <%--<div class="form-group">--%>
                        <%--<div class="col-md-12">--%>
                            <%--<span class="ic-lock"></span>--%>
                            <%--<input id="password1" class="form-control" name="password1" value="" type="password" placeholder="new password" autocomplete="off"/>--%>
                        <%--</div>--%>
                    <%--</div>--%>

                    <%--<div class="form-group">--%>
                        <%--<div class="col-md-12">--%>
                            <%--<span class="ic-lock"></span>--%>
                            <%--<input id="password2" class="form-control" name="password2" value="" type="password" placeholder="retype new password" autocomplete="off"/>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <%--<div class="form-group">--%>
                        <%--<div class="col-md-12">--%>
                            <%--<button class="btn btn-success btn-block" type="submit">Change Password</button>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                <%--</form>--%>
                <c:url var="loginUrl" value="/login"/>
                <a href="${loginUrl}">Back to Sign In</a>
            </div>
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
    <%--<script type="text/javascript" src="<c:url value="/js/password-utils.js"/>"></script>--%>
    <%--<script type="text/javascript" src="<c:url value="/js/password-expired.js"/>"></script>--%>
</content>

</body>