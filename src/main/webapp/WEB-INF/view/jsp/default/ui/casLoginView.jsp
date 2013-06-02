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

        <c:url var="registrationUrl" value="/app/registration/welcome"/>
        $('#create-btn').click(function() {
            window.location = "${registrationUrl}";
        });

        $('#email-help-floater ').hide();
        $('#create-help-floater ').hide();

        $('#email-help-floater ').fadeIn(1700, function() {
            $('#create-help-floater ').fadeIn(1700);
        });


        /* this is for getting the custom image for an app - the new designs do not seem to use this
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
        */
    });

    function submitForgotPasswordForm() {
        var forgotPasswordForm = $("#forgotPasswordForm");
        var username = $("#username").val();
        if (username) {
            $(forgotPasswordForm[0]['username']).val(username);
            forgotPasswordForm.submit();
            return false;
        } else {
            return true;
        }
    }

</script>

<style type="text/css">

    body {
        font-family: 'Open Sans', Arial, Verdana, sans-serif;
        background: #FFFFFF;
    }

    #full-login-content {
        position: relative;
        margin: 0 auto;
        width: 618px;
        height: 330px;
        clear: both;

    }

    #login-form {
        color: #000;
        background: #e4e4e4;
        width: 249px;
        margin: 0px auto 7px auto;
        border-bottom-left-radius: 4px;
        border-bottom-right-radius: 4px;
        padding: 20px 25px 25px 25px;
    }

    #login-form h3 {
        font-family: 'Open Sans', Arial, Verdana, sans-serif;
        font-weight: 300;
        text-align: center;
        margin: 0 0 18px 0;
        padding: 0;
        font-size: 16px;
        line-height: 16px;
        color: #444;
    }

    #login-header {
        background: #dbe5eb url(/images/isid-logo.png) center center no-repeat;
        height: 92px;
        width: 299px;
        border-top-right-radius: 4px;
        border-top-left-radius: 4px;
        border-bottom: 3px solid #ffffff;
    }

    #login-header h3 {
        font-family: 'Open Sans', Arial, Verdana, sans-serif;
        font-weight: 300;
        text-align: center;
        padding: 0;
        font-size: 16px;
        line-height: 16px;
        color: #fff;
    }

    #login-left {
        float: left;
        margin-right: 20px;
    }

    #login-right {
        float: left;

    }

    #create-header {
        background: #cee8c1;
        height: 92px;
        width: 299px;
        border-top-right-radius: 4px;
        border-top-left-radius: 4px;
        border-bottom: 3px solid #ffffff;

    }

    #email-help-floater {
        position: absolute;
        top: 108px;
        left: -155px;
        padding-right: 50px;
        text-align: center;
        font-style: italic;
        background: url("/images/right-ball-login.png") 100% center no-repeat;
    }

    #create-help-floater {
        position: absolute;
        top: 214px;
        right: -133px;
        padding-left: 55px;
        text-align: center;
        font-style: italic;
        background: url("/images/left-ball-login.png") 0 center no-repeat;
    }

    #create-header-text {
        padding-top: 20px;
        color: #44742d;
        font-size: 17px;
        text-align: center;
    }

    #create-main {
        background: #e4e4e4;
        width: 249px;
        height: 174px;
        margin: 0px auto 7px auto;
        border-bottom-left-radius: 4px;
        border-bottom-right-radius: 4px;
        padding: 20px 25px 25px 25px;
    }

    #create-main-text {
        font-size: 12px;
        color: #444444;
        height: 109px;
    }


    #create-lower-help {
        padding-top: 15px;
        text-align: center;
    }

    #create-lower-help em {
        font-style: normal;
        font-weight: bold;
        font-size: larger;
    }

    #create-btn {
        background-color: #ffffff;
        -moz-border-radius: 4px;
        -webkit-border-radius: 4px;
        border-radius: 4px;
        color: #158ecb;
        font: 600 13px 'Open Sans';
        font-family: 'Open Sans', Arial, Verdana, Sans-Serif;
        border: 1px solid #bfbfbf;
        border-bottom: 2px solid #bbbbbb;
        text-shadow: 0 1px 0 #d0deea;
        cursor: pointer;
        display: inline-block;
        line-height: normal;
        position: relative;
        text-decoration: none;
        box-shadow: none;
        -webkit-box-shadow: none;
        -moz-box-shadow: none;
        padding: 8px 0px;
        width: 251px;
        display: block;
        margin-bottom: 0;
    }

    .forgot-password {
        display: block;
        text-align: center;
        position: relative;
        top: 12px;
        margin: 5px auto;
    }

    #affiliate-login {
        text-align: center;
        margin: 28px 0 0 0;
    }

    #username {
        background: #ffffff url(/images/email-bg.png) 8px center no-repeat;
        text-indent: 32px;
        padding: 9px 3px;
        border-radius: 4px;
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        width: 243px;
        margin-bottom: 15px;
    }

    #password {
        background: #ffffff url(/images/password-bg.png) 8px center no-repeat;
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
    <%--<%@include file="../../registration/_banner.jsp" %>--%>
    <%--<div style="height: 20px"></div>--%>
</c:if>
<%--<c:if test="${appMigrated}">--%>
    <div style="height: 120px"></div>
<%--</c:if>--%>

<%--<div id="biglogo"></div>--%>

<div id="full-login-content">
    <div id="email-help-floater">
        Sign in here if you've<br/>
        already created<br/>
        your Infusionsoft ID.<br/>
    </div>
    <div id="create-help-floater">
        Click this button<br/>
        to create your<br/>
        Infusionsoft ID<br/>
    </div>
    <div id="login-left">
        <div id="login-header"> </div>

        <div id="login-form">
            <form:form method="post" id="fm1" cssClass="form-vertical" commandName="${commandName}" htmlEscape="true">
                <form:errors path="*" id="msg" cssClass="alert alert-error" element="div"/>

                <div class="control-group">
                    <div class="controls">
                        <form:input title="Email Address" cssClass="" cssErrorClass="error" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" htmlEscape="true"/>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <form:password title="Password" cssClass="" cssErrorClass="error" id="password" size="25" tabindex="2" path="password" accesskey="${passwordAccessKey}" htmlEscape="true"/>
                    </div>
                </div>

                <input type="hidden" name="lt" value="${loginTicket}"/>
                <input type="hidden" name="execution" value="${flowExecutionKey}"/>
                <input type="hidden" name="_eventId" value="submit"/>

                <div class="control-group">
                    <input class="btn btn-primary sign-in-button" name="submit" accesskey="l" value="Sign In" tabindex="4" type="submit"/>
                    <a class="forgot-password" href="${forgotPasswordUrl}" onclick="return submitForgotPasswordForm();">Forgot your password?</a>
                </div>
            </form:form>
            <form:form action="${forgotPasswordUrl}" method="GET" id="forgotPasswordForm"><input name="username" type="hidden"/></form:form>
        </div>
    </div>
    <div id="login-right">
        <div id="create-header">
            <div id="create-header-text">
                Haven't Created Your<br/>Infusionsoft ID?
            </div>
        </div>
        <div id="create-main">
            <div id="create-main-text">
                Your Infusionsoft ID is a new way to sign<br/>
                in that allows you to access all aspects of<br/>
                Infusionsoft using one email address and</br/>
                one password.
            </div>
            <div id="create-main-button" class="control-group">
                <input id="create-btn" class="btn btn-primary" name="getStarted" accesskey="l" value="Get Started" tabindex="5" type="submit"/>
            </div>
            <div id="create-lower-help">
                Need help?  Call <em>1-877-296-7929</em>
            </div>

        </div>

    </div>
</div>

<c:if test="${not empty appUrl}">
    <div id="affiliate-login">
        <a href="${appUrl}/Client/">Referral Partner Sign-In</a>
    </div>
</c:if>

<div style="margin: 50px auto; width: 960px; height: 960px">
    <iframe src="https://infusionmedia.s3.amazonaws.com/cas/login-include.html" width="960" height="960" style="border: none"></iframe>
</div>
