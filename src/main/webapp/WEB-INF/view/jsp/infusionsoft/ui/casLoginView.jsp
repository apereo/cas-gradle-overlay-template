<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:url var="getLogoImageUrl" value="/registration/getLogoImageUrl"/>
<c:url var="forgotPasswordUrl" value="/registration/forgot"/>

<meta name="decorator" content="anonymousNoLogo"/>

<script type="text/javascript">
    $(document).ready(function() {
        $("#username").attr("placeholder", "email address");
        $("#password").attr("placeholder", "password");
        $("input").placeholder();

        $.ajax({
            url: "${getLogoImageUrl}",
            data: {
                appType: "${sessionScope.refererAppType}",
                appName: "${sessionScope.refererAppName}"
            },
            type: "GET",
            success: function(logoUrl) {
                if (logoUrl && logoUrl.length > 1) {
                    $("#biglogo").css("background-image", "url('" + logoUrl + "')");
                } else {
                    $("#biglogo").css("background-image", "url(/images/big-logo.png)");
                }
            },
            error: function() {
                $("#biglogo").css("background-image", "url(/images/big-logo.png)");
            }
        });
    });
</script>

<style type="text/css">

    #login {
        color: #000;
        background: #fff;
        width: 276px;
        margin: 10px auto;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
        padding: 30px;
    }

    #login h3 {
        font-family: 'Open Sans', Arial, Verdana, sans-serif;
        font-weight: 300;
        text-align: center;
        margin: 0 0 20px 0;
        padding: 0;
        font-size: 16px;
        line-height: 16px;
    }

    #forgot-password {
        text-align: center;
        margin: 5px auto;
    }

    #affiliate-login {
        text-align: center;
        margin: 30px 0 0 0;
    }

    #affiliate-login a {
        color: #aaa;
    }

    #username {
        background: url(/images/username-bg.png) 8px center no-repeat;
        text-indent: 32px;
        padding: 8px 3px;
    }

    #password {
        background: url(/images/password-bg.png) 8px center no-repeat;
        text-indent: 32px;
        padding: 8px 3px;
    }

    #biglogo {
        margin-top: 0px;
        height: 100px;
    }

    .alert {
        margin: -20px -20px 20px -20px;
    }

</style>

<c:set var="daysRemaining" value="${sessionScope.daysToMigrate}"/>

<c:if test="${!requestScope.appMigrated}">
    <c:set var="hittingCasDirectly" value="${true}"/>
    <%@include file="registration/_banner.jsp"%>
    <div style="height: 20px"></div>
</c:if>
<c:if test="${requestScope.appMigrated}">
    <div style="height: 120px"></div>
</c:if>

<div id="biglogo"></div>

<div id="login">
    <form:form method="post" id="fm1" cssClass="form-vertical" commandName="${commandName}" htmlEscape="true">
        <form:errors path="*" id="msg" cssClass="alert alert-error" element="div" />

        <h3>Sign In With Your Infusionsoft ID</h3>

        <div class="control-group">
            <div class="controls">
                <form:input cssClass="required" cssErrorClass="error" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="false" htmlEscape="true" style="width: 266px" />
            </div>
        </div>

        <div class="control-group">
            <div class="controls">
                <form:password cssClass="required" cssErrorClass="error" id="password" size="25" tabindex="2" path="password" accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" style="width: 266px" />
            </div>
        </div>

        <input type="hidden" name="lt" value="${loginTicket}" />
        <input type="hidden" name="execution" value="${flowExecutionKey}" />
        <input type="hidden" name="_eventId" value="submit" />
        <label class="checkbox"><input type="checkbox" name="rememberMe" id="rememberMe" value="true" checked="checked"/> Remember Me</label>

        <div class="control-group">
            <input class="btn btn-primary" style="width: 100%; height: 36px; margin: 10px 0 0 0" name="submit" accesskey="l" value="Sign In" tabindex="4" type="submit" />
        </div>
    </form:form>
</div>

<div id="forgot-password">
    <a href="${forgotPasswordUrl}">Forgot your password?</a>
</div>

<c:if test="${not empty sessionScope.refererUrl}">
    <div id="affiliate-login">
        <a href="${sessionScope.refererUrl}/Client/">Referral Partners &amp; Customers</a>
    </div>
</c:if>

<div style="margin: 50px auto; width: 960px; height: 960px">
    <iframe src="https://infusionmedia.s3.amazonaws.com/cas/login-include.html" width="960" height="960" style="border: none"></iframe>
</div>
