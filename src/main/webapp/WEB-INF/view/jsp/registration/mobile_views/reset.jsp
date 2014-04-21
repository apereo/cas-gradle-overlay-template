<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!doctype html>
<html>
<head>
    <meta name="decorator" content="base-bootstrap3"/>
    <meta name="robots" content="noindex">
    <title>Create a New Password</title>
</head>
<body>

<div class="container">
    <div class="rounded-box-wide">
        <div class="row">
            <div class="col-sm-8">
                <object type="image/svg+xml" tabindex="-1" data="/img/is_logo.svg" width="159" height="26" class="logo">Infusionsoft</object>
                <h2>Create a New Password</h2>

                <form action="reset" method="post" id="resetPasswordForm" class="form-horizontal">

                    <input id="recoveryCode" name="recoveryCode" type="hidden" value="${fn:escapeXml(recoveryCode)}"/>

                    <div class="form-group">
                        <div class="col-md-12">
                            <span class="ic-lock"></span>
                            <input id="password1" class="form-control" name="password1" value="" type="password" placeholder="new password" autocomplete="off"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12">
                            <span class="ic-lock"></span>
                            <input id="password2" class="form-control" name="password2" value="" type="password" placeholder="retype new password" autocomplete="off"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-md-12">
                            <button class="btn btn-success btn-block" type="submit">Change Password</button>
                        </div>
                    </div>
                </form>
                <c:url var="loginUrl" value="/login"/>
                <a href="${loginUrl}">Back to Sign In</a>
            </div>

            <div class="col-sm-4 pw-requirements">
                <ul>
                    <!-- Simply toggle/remove the 'valid' class as needed -->
                    <li id="pw_length"><spring:message code="password.criteria.length"/></li>
                    <li id="pw_number"><spring:message code="password.criteria.number"/></li>
                    <li id="pw_upper"><spring:message code="password.criteria.uppercase"/></li>
                    <li id="pw_lower"><spring:message code="password.criteria.lowercase"/></li>
                    <li id="pw_previous"><spring:message code="password.criteria.previous"/></li>
                </ul>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="need-help col-md-12">
            <p>Need Help? Call <strong>${supportPhoneNumber}</strong>.</p>
        </div>
    </div>
</div>

<content tag="local_script">
    <script type="text/javascript" src="<c:url value="/js/password-utils.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/password-reset.js"/>"></script>
</content>

</body>
</html>