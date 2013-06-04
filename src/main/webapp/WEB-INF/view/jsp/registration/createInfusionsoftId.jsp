<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Infusionsoft ID</title>
    <meta name="decorator" content="green-header-minimal"/>
    <link href="<c:url value="/css/flip.css"/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value="/js/jquery.flippy.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/createInfusionsoftId.js"/>"></script>

    <script type="text/javascript">
        $(document).ready(function () {
            jQuery.validator.addMethod("password", function (value, element) {
                var result = this.optional(element) || value.length >= 7 && /\d/.test(value) && /[a-z]/.test(value) && /[A-Z]/.test(value);
                return result;
            }, "<spring:message code='registration.error.invalidPassword'/>");

            //Check to ensure that username matches
            //Validate the form
            $('#registerForm').validate(
                    {
                        rules: {
                            firstName: {
                                required: true
                            },
                            lastName: {
                                required: true
                            },
                            username: {
                                required: true,
                                email: true
                            },
                            username2: {
                                required: true,
                                email: true,
                                equalTo: "#username"
                            },
                            password1: {
                                required: true,
                                password: true
                            },
                            password2: {
                                required: true,
                                password: false,
                                equalTo: "#password1"
                            },
                            eula: {
                                required: true
                            }
                        },
                        messages: {
                            firstName: "<spring:message code='registration.error.invalidFirstName'/>",
                            lastName: "<spring:message code='registration.error.invalidLastName'/>",
                            username: {
                                required: "<spring:message code='registration.error.usernameRequired'/>",
                                email: "<spring:message code='registration.error.invalidEmail'/>"
                            },
                            username2: {
                                required: "<spring:message code='registration.error.usernameRequired'/>",
                                equalTo: "<spring:message code='registration.error.emailNoMatch'/>"
                            },
                            password1: {
                                required: "<spring:message code='registration.error.passwordsRequired'/>",
                                password: "<spring:message code='registration.error.invalidPassword'/>"
                            },
                            password2: {
                                required: "<spring:message code='registration.error.passwordsRequired'/>",
                                equalTo: "<spring:message code='registration.error.passwordsNoMatch'/>"
                            },
                            eula: "<spring:message code='registration.error.eula'/>"

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
    </script>
</head>
<body>
<div class="container-narrow">
    <div id="floaterLeft">
        Your Infusionsoft ID<br/>
        will replace your<br/>
        current username<br/>
        and password.<br/>
    </div>
    <div id="floaterRight">
        You will use this email<br/>
        address and password<br/>
        to sign in to all your<br/>
        Infusionsoft accounts<br/>
        and add-ons.
    </div>
    <div id="main_title" class="jumbotron">
        <h1>It's Time To Create Your Official Infusionsoft ID.</h1>
    </div>
    <div id="lanyard_background"></div>

    <div id="lanyard">
        <div id="main_title_div"></div>
        <div id="flipLink">
            <a id="learnMore" href="#">What is an Infusionsoft ID?</a>
            <a id="backBtn" style="display:none;" href="#">&laquo; Back to creating your ID</a>
        </div>
        <div class="flipbox-container">
            <div id="idcard">
                <div class="cardTop">
                    <div id="cardTopText">
                        Your All-Access Pass To Your Infusionsoft Accounts.
                    </div>
                </div>
                <div id="cardBottom">
                    <form:form id="registerForm" action="register" method="post"  class="form-vertical" modelAttribute="user">
                        <input type="hidden" name="registrationCode" value="${registrationCode}"/>
                        <input type="hidden" name="returnUrl" value="${returnUrl}"/>
                        <input type="hidden" name="userToken" value="${userToken}"/>

                        <c:if test="${error != null}">
                            <div class="alert alert-error" style="margin-top: 10px;">
                                <spring:message code="${error}" text="${error}"/>
                            </div>
                        </c:if>

                        <fieldset>
                            <label class="formLabel">Name</label>
                            <form:input path="firstName" id="firstName" name="firstName" style="width:158px;float:left" tabindex="1"/>
                            <form:input path="lastName" id="lastName" name="lastName" style="width:158px;float:right" tabindex="2"/>

                            <div class="form-spacer" style="clear:both;"></div>
                            <label class="formLabel">Email Address <span class="emailHelp">(you will use this to sign in)</span></label>
                            <form:input path="username" style="width:356px;" id="username" name="username"/>

                            <div class="form-spacer"></div>
                            <label class="formLabel">Retype Email Address</label>
                            <form:input path="" style="width:356px;" id="username2" name="username2" value="${fn:escapeXml(user != null ? user.username : '')}"/>

                            <div class="form-spacer"></div>
                            <div class="password-fields">
                                <label class="formLabel">Create Password</label>
                                <form:password path="" style="width:166px;" id="password1" name="password1" autocomplete="off"/>

                                <div class="form-spacer"></div>
                                <label class="formLabel">Retype Password</label>
                                <form:password path="" style="width:166px;" id="password2" name="password2" autocomplete="off"/>
                            </div>
                            <div class="password-info">
                                Must have at least:<br/>
                                <ul>
                                    <li>7 characters</li>
                                    <li>1 number</li>
                                    <li>1 uppercase letter</li>
                                    <li>1 lowercase letter</li>
                                </ul>
                            </div>
                            <div style="clear:both;" class="form-spacer"></div>
                            <div class="control-group">
                                <div class="controls">
                                    <label class="checkbox">
                                        <input id="eula" name="eula" type="checkbox" value="agreed" tabindex="7"/>
                                        <spring:message code='registration.form.readEULA'/>
                                        <a tabindex="-1" href="http://www.infusionsoft.com/legal-stuff/eula" target="_blank"><spring:message code='registration.link.EULA'/></a>,
                                        <a tabindex="-1" href="http://www.infusionsoft.com/legal-stuff/aup" target="_blank"><spring:message code='registration.link.AUP'/></a>,
                                        <a tabindex="-1" href="http://www.infusionsoft.com/legal-stuff/billing-policies" target="_blank"><spring:message code='registration.link.billingPolicy'/></a> &amp;,
                                        <a tabindex="-1" href="http://www.infusionsoft.com/legal-stuff/privacy-policy" target="_blank"><spring:message code='registration.link.privacyPolicy'/></a>.
                                    </label>
                                </div>
                            </div>
                            <div class="createDiv">
                                <button type="submit" class="btn btn-primary">Create Your Infusionsoft ID</button>
                            </div>
                        </fieldset>
                    </form:form>
                </div>
                <!--card bottom-->
            </div>
            <!--idcard-->
        </div>
        <!--flip container-->
    </div>
    <!--lanyard-->
</div>
<!--container-narrow-->
</body>
</html>
