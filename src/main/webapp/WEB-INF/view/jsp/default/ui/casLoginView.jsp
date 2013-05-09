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

<meta name="decorator" content="anonymousNoLogo"/>

<script type="text/javascript">
    $(document).ready(function () {
        $("#username").attr("placeholder", "email@example.com");
        $("#password").attr("placeholder", "password");
        $("#username").blur();
        $("#password").blur();
        $("#username").placeholder();
        $("#password").placeholder();
        $("#username").focus();

        <c:if test="${!empty appName}">
        $.ajax({
            url: "${getLogoImageUrl}",
            data: {
                appType: "${appType}",
                appName: "${appName}"
            },
            type: "GET",
            success: function (logoUrl) {
                if (logoUrl && logoUrl.length > 1) {
                    $("#biglogo").css("background-image", "url('" + logoUrl + "')");
                } else {
                    $("#biglogo").css("background-image", "url(/images/big-logo.png)");
                }
            },
            error: function () {
                $("#biglogo").css("background-image", "url(/images/big-logo.png)");
            }
        });
        </c:if>
    });
</script>

<style type="text/css">

    body {
        font-family: 'Open Sans', Arial, Verdana, sans-serif;
    }

    #login {
        color: #000;
        background: #fff;
        width: 251px;
        margin: 15px auto 7px auto;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
        padding: 20px 25px 25px 25px;
    }

    #login h3 {
        font-family: 'Open Sans', Arial, Verdana, sans-serif;
        font-weight: 300;
        text-align: center;
        margin: 0 0 18px 0;
        padding: 0;
        font-size: 16px;
        line-height: 16px;
        color: #444;
    }

    #forgot-password {
        text-align: center;
        margin: 5px auto;
    }

    #affiliate-login {
        text-align: center;
        margin: 28px 0 0 0;
    }

    #affiliate-login a {
        color: #aaa;
    }

    #username {
        background: url(/images/email-bg.png) 8px center no-repeat;
        text-indent: 32px;
        padding: 9px 3px;
        border-radius: 4px;
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        width: 243px;
        margin-bottom: 15px;
    }

    #password {
        background: url(/images/password-bg.png) 8px center no-repeat;
        text-indent: 32px;
        padding: 9px 3px;
        border-radius: 4px;
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        width: 243px;
        margin-bottom: 15px;
    }

    .greetings p {
        color: #fff;
    }

    #biglogo {
        margin-top: 0px;
        height: 100px;
    }

    .alert {
        margin: -10px -15px 15px -15px;
    }

    .control-group {
        margin-bottom: 0px;
    }

    .radio, .checkbox {
        min-height: 26px;
        color: #444;
        font-size: 13px;
    }

    select, textarea, input[type="text"], input[type="password"], input[type="datetime"], input[type="datetime-local"], input[type="date"], input[type="month"], input[type="time"], input[type="week"], input[type="number"], input[type="email"], input[type="url"], input[type="search"], input[type="tel"], input[type="color"], .uneditable-input {
        font-size: 13px;
        color: #444;
        box-shadow: none;
        -webkit-box-shadow: none;
        -moz-box-shadow: none;
    }

    ::-webkit-input-placeholder {
        font-style: italic;
        color: #444;
    }

    ::-moz-placeholder {
        font-style: italic;
        color: #444;
    }

        /* firefox 19+ */
    :-ms-input-placeholder {
        font-style: italic;
        color: #444;
    }

        /* ie */
    input:-moz-placeholder {
        font-style: italic;
        color: #444;
    }

    ::-webkit-input-placeholder {
        color: #444;
        font-style: italic;
    }

    :focus::-webkit-input-placeholder {
        text-indent: -999px
    }

    ::-moz-placeholder {
        color: #444;
        font-style: italic;
    }

    :focus::-moz-placeholder {
        text-indent: -999px
    }

</style>

<c:if test="${!appMigrated}">
    <c:set var="hittingCasDirectly" value="${true}"/>
    <%@include file="../../registration/_banner.jsp" %>
    <div style="height: 20px"></div>
</c:if>
<c:if test="${appMigrated}">
    <div style="height: 120px"></div>
</c:if>

<div id="biglogo"></div>

<div id="login">
    <form:form method="post" id="fm1" cssClass="form-vertical" commandName="${commandName}" htmlEscape="true">
        <form:errors path="*" id="msg" cssClass="alert alert-error" element="div"/>

        <h3>Sign In With Your Infusionsoft ID</h3>

        <div class="control-group">
            <div class="controls">
                <form:input title="Email Address" cssClass="" cssErrorClass="error" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" htmlEscape="true" />
            </div>
        </div>

        <div class="control-group">
            <div class="controls">
                <form:password title="Password" cssClass="" cssErrorClass="error" id="password" size="25" tabindex="2" path="password" accesskey="${passwordAccessKey}" htmlEscape="true" />
            </div>
        </div>

        <input type="hidden" name="lt" value="${loginTicket}"/>
        <input type="hidden" name="execution" value="${flowExecutionKey}"/>
        <input type="hidden" name="_eventId" value="submit"/>

        <div class="control-group">
            <input class="btn btn-primary sign-in-button" name="submit" accesskey="l" value="Sign In" tabindex="4" type="submit"/>
        </div>
    </form:form>
</div>

<div id="forgot-password">
    <a href="${forgotPasswordUrl}">Forgot your password?</a>
</div>

<c:if test="${not empty appUrl}">
    <div id="affiliate-login">
        <a href="${appUrl}/Client/">Referral Partners &amp; Customers</a>
    </div>
</c:if>

<div style="margin: 50px auto; width: 960px; height: 960px">
    <iframe src="https://infusionmedia.s3.amazonaws.com/cas/login-include.html" width="960" height="960" style="border: none"></iframe>
</div>
