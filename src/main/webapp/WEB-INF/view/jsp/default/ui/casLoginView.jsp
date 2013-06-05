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

<meta name="decorator" content="anonymousNoLogo"/>
<html>
<head>
<title>Sign in to Infusionsoft</title>

<script type="text/javascript">
    $(document).ready(function () {
        $("#username").attr("placeholder", "email@example.com");
        $("#password").attr("placeholder", "password");
        $("#username").blur();
        $("#password").blur();
        $("#username").placeholder();
        $("#password").placeholder();
        $("#username").focus();


        $('#create-btn').click(function () {
            window.location = "${registrationUrl}";
        });

        $('#email-help-floater').hide();
        $('#create-help-floater').hide();

        $('#email-help-floater').delay(1000).fadeIn(750, function () {
            $('#create-help-floater').fadeIn(750);
        });

        /* this is for getting the custom image for an app - the new designs do not seem to use this
        <c:if test="${!empty appName}">
         $.ajax({
         url: "
        ${getLogoImageUrl}",
         data: {
         appType: "
        ${appType}",
         appName: "
        ${appName}"
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

        //Validate the form
        $('#fm1').validate(
                {
                    rules: {
                        username: {
                            required: true,
                            email: true
                        },
                        password: {
                            required: true,
                            password: false
                        }
                    },
                    messages: {
                        username: {
                            required: "<spring:message code='login.noUsername'/>",
                            email: "<spring:message code='error.invalidEmail'/>"
                        },
                        password: {
                            required: "<spring:message code='login.noPassword'/>"
                        }
                    },
                    highlight: function (element) {
                        $(element).closest('.control-group').removeClass('success').addClass('error');
                    },
                    success: function (element) {
                        element.closest('.control-group').removeClass('error');
                        element.closest('label.error').hide().removeClass('error').addClass('valid').addClass('error');
                    }

                });
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
    width: 620px;
    height: 330px;
    clear: both;
}

#login-form {
    color: #000;
    background: #e4e4e4;
    width: 250px;
    margin: 0px auto 7px auto;
    border-bottom-left-radius: 5px;
    border-bottom-right-radius: 5px;
    padding: 15px 25px 25px 25px;
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
    width: 300px;
    border-top-right-radius: 5px;
    border-top-left-radius: 5px;
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
    width: 300px;
    border-top-right-radius: 5px;
    border-top-left-radius: 5px;
    border-bottom: 3px solid #ffffff;
}

#email-help-floater, #create-help-floater {
    position: absolute;
    color: #888;
    font-size: 13px;
    text-align: center;
    font-style: italic;
}

#email-help-floater {
    top: 22px;
    left: -163px;
    padding-right: 50px;
    background: url("/images/right-ball-login.png") no-repeat scroll 100% center transparent;
}

#create-help-floater {
    top: 214px;
    right: -141px;
    padding-left: 55px;
    background: url("/images/left-ball-login.png") no-repeat scroll 0 center transparent;
}

#create-header-text {
    padding-top: 21px;
    color: #44742d;
    font-size: 18px;
    text-align: center;
    line-height: 25px;
}

#create-main {
    background: #e4e4e4;
    width: 250px;
    height: 174px;
    margin: 0px auto 7px auto;
    border-bottom-left-radius: 4px;
    border-bottom-right-radius: 4px;
    padding: 20px 25px 25px 25px;
}

#create-main-text {
    font-size: 13px;
    color: #444444;
    height: 109px;
    line-height: 20px;
}

#create-lower-help {
    padding-top: 15px;
    text-align: center;
}

#create-lower-help em {
    font-style: normal;
    font-weight: bold;
    font-size: 14px;
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
    width: 250px;
    display: block;
    margin: 0;
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
    background: #ffffff url(/images/email-bg.png) 10px center no-repeat;
    text-indent: 32px;
    padding: 8px 3px;
    border-radius: 4px;
    -webkit-border-radius: 4px;
    -moz-border-radius: 4px;
    width: 242px;
    margin: 10px 0 10px 0;
}

#password {
    background: #ffffff url(/images/password-bg.png) 10px center no-repeat;
    text-indent: 32px;
    padding: 8px 3px;
    border-radius: 4px;
    -webkit-border-radius: 4px;
    -moz-border-radius: 4px;
    width: 242px;
    margin: 5px 0 10px 0;
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

</head>

<body>

<div style="height: 120px"></div>

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
        <div id="login-header"></div>

        <div id="login-form">
            <form:form method="post" id="fm1" cssClass="form-vertical" commandName="${commandName}" htmlEscape="true">
                <form:errors path="*" id="msg" cssClass="alert alert-error" element="div"/>

                <div class="control-group">
                    <div class="controls">
                        <form:input title="Email Address" cssClass="" cssErrorClass="error" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" htmlEscape="true" placeholder="email@example.com"/>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <form:password title="Password" cssClass="" cssErrorClass="error" id="password" size="25" tabindex="2" path="password" accesskey="${passwordAccessKey}" htmlEscape="true" placeholder="password"/>
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
                Your Infusionsoft ID is a new way to sign in that allows you to access all aspects of Infusionsoft using one email address and one password.
            </div>
            <div id="create-main-button" class="control-group">
                <input id="create-btn" class="btn btn-primary" name="getStarted" accesskey="l" value="Get Started" tabindex="5" type="submit"/>
            </div>
            <div id="create-lower-help">
                Need help? Call <em>${supportPhoneNumber}</em>
            </div>

        </div>

    </div>
</div>

<div style="clear:both;"></div>

<c:if test="${not empty appUrl && appType == 'CRM'}">
    <div id="affiliate-login">
        <a href="${appUrl}/Affiliate/">Referral Partner Sign-In</a>
    </div>
</c:if>

<div style="margin: 50px auto; width: 960px; height: 960px">
    <iframe src="https://infusionmedia.s3.amazonaws.com/cas/login-include.html" width="960" height="960" style="border: none"></iframe>
</div>
</body>
</html>