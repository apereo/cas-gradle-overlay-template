<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%-- These form elements are used for both linkExisting and register --%>
<c:set var="commonFormElements">
    <input type="hidden" name="registrationCode" value="${fn:escapeXml(registrationCode)}"/>
    <input type="hidden" name="returnUrl" value="${fn:escapeXml(returnUrl)}"/>
    <input type="hidden" name="userToken" value="${fn:escapeXml(userToken)}"/>
</c:set>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Infusionsoft ID</title>
    <meta name="decorator" content="green-header-minimal"/>
    <link href="<c:url value="/css/flip.css"/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value="/js/jquery.flippy.min.js"/>"></script>
    <script type="text/javascript" src="<c:url value="/js/createInfusionsoftId.js"/>"></script>

    <script type="text/javascript">
        function resetCheckPassword() {
            $("#pw_length").css({"list-style-image":''});
            $("#pw_number").css({"list-style-image":''});
            $("#pw_upper").css({"list-style-image":''});
            $("#pw_under").css({"list-style-image":''});
        }
        function checkPasswordReq() {
            var currPass = $('#password1').val();
            resetCheckPassword();
            if (currPass.length >= 7) {
                $("#pw_length").css({"list-style-image":"url('/img/checkmark.png')"});
            }
            if (/\d/.test(currPass)) {
                $("#pw_number").css({"list-style-image":"url('/img/checkmark.png')"});
            }
            if (/[a-z]/.test(currPass)) {
                $("#pw_under").css({"list-style-image":"url('/img/checkmark.png')"});
            }
            if (/[A-Z]/.test(currPass)) {
                $("#pw_upper").css({"list-style-image":"url('/img/checkmark.png')"});
            }
        }
        setInterval(function() {checkPasswordReq();}, 100);

        $(document).ready(function () {
            jQuery.validator.addMethod("password", function (value, element) {
                var result = this.optional(element) || value.length >= 7 && /\d/.test(value) && /[a-z]/.test(value) && /[A-Z]/.test(value);
                return result;
            }, "<spring:message code='registration.error.invalidPassword'/>");

            //Check to ensure that username matches
            //Validate the form
            $('#registerForm').validate({
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
                            /*$(element).next('label.error').appendTo($(element).closest('.control-group'));*/
                        },
                        success: function (element) {
                            element.closest('.control-group').removeClass('error');
                            element.closest('label.error').hide().removeClass('error').addClass('valid').addClass('error');
                        }

            });

            <c:if test="${empty error}">
            openingAnimation();
            </c:if>
        });
    </script>
</head>
<body>
<div class="container-fullscreen">
<div class="container-narrow">
    <c:if test="${!empty userToken}">
        <div id="floaterLeft">
            Your Infusionsoft ID<br/>
            will replace your<br/>
            current username<br/>
            and password.<br/>
        </div>
    </c:if>
    <div id="floaterRight">
        You will use this email<br/>
        address and password<br/>
        to sign in to all your<br/>
        Infusionsoft accounts<br/>
        and add-ons.
    </div>
    <div id="main_title" class="jumbotron">
        <c:choose>
            <c:when test="${!empty userToken}">
                <h1>It's Time To Create Your Official Infusionsoft ID.</h1>
            </c:when>
            <c:otherwise>
                <h1>Please Create Your Infusionsoft ID.</h1>
            </c:otherwise>
        </c:choose>
    </div>
    <div id="lanyard_background"></div>

    <div id="lanyard">
        <div id="main_title_div"></div>
        <div id="links-above-lanyard" >
            <div id="already-have-id">
                <a href="#" class="linkToExisting"><spring:message code='registration.signin'/></a>
                <form:form id="linkToExistingForm" action="linkToExisting" method="post">
                    ${commonFormElements}
                </form:form>
            </div>
            <div id="flipLink">
                <a id="learnMore" href="#">What is an Infusionsoft ID?</a>
                <a id="backBtn" style="display:none;" href="#">&laquo; Back to creating your ID</a>
            </div>
            <div style="clear:both;"></div>
        </div>
        <div class="flipbox-container">
            <div id="idcard">
                <div class="cardTop">
                    <div id="cardTopText">
                        Your All-Access Pass To Your Infusionsoft Account(s).
                    </div>
                </div>
                <div id="cardBottom">
                    <form:form id="registerForm" action="register" method="post" class="form-vertical" modelAttribute="user">
                        ${commonFormElements}
                        <input type="hidden" name="skipUrl" value="${fn:escapeXml(skipUrl)}"/>

                        <c:if test="${error != null}">
                            <div class="alert alert-error">
                                <spring:message code="${error}" text="${error}"/>
                            </div>
                        </c:if>

                        <fieldset>
                            <label class="formLabel">Name</label>
                            <table>
                                <tr>
                                    <td>
                                        <div style="height: 56px;" class="control-group">
                                            <form:input path="firstName" id="firstName" name="firstName" style="width:158px;" tabindex="1" placeholder="first name"/>
                                        </div>
                                    </td>
                                    <td>
                                        <div style="height: 56px;padding-left: 22px;" class="control-group">
                                            <form:input path="lastName" id="lastName" name="lastName" style="width:158px;" tabindex="2" placeholder="last name"/>
                                        </div>
                                    </td>
                                </tr>
                            </table>

                            <div class="form-spacer" style="height:3px;clear:both;"></div>

                            <div class="control-group">
                                <label class="formLabel">Email Address <span class="emailHelp">(you will use this to sign in)</span></label>
                                <form:input path="username" style="width:356px;" id="username" name="username" tabindex="3"/>
                            </div>

                            <div class="form-spacer"></div>

                            <div class="control-group">
                                <label class="formLabel">Retype Email Address</label>
                                <form:input path="" style="width:356px;" id="username2" name="username2" value="${fn:escapeXml(user != null ? user.username : '')}" tabindex="4"/>
                            </div>

                            <div class="form-spacer"></div>

                            <div class="password-fields">
                                <div class="control-group">
                                    <label class="formLabel">Create Password</label>
                                    <form:password path="" style="width:166px;" id="password1" name="password1" autocomplete="off" tabindex="5"/>
                                </div>

                                <div class="form-spacer"></div>
                                <div class="control-group">
                                    <label class="formLabel">Retype Password</label>
                                    <form:password path="" style="width:166px;" id="password2" name="password2" autocomplete="off" tabindex="6"/>
                                </div>
                            </div>
                            <div class="password-info">
                                Password Criteria:<br/>
                                <ul>
                                    <li id="pw_length">At least 7 characters</li>
                                    <li id="pw_number">1 number</li>
                                    <li id="pw_upper">1 uppercase letter</li>
                                    <li id="pw_under">1 lowercase letter</li>
                                </ul>
                            </div>
                            <div style="clear:both;" class="form-spacer"></div>
                            <div class="control-group">
                                <div class="controls" style="position: relative;">
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
                            <div class="text-center">
                                <button type="submit" class="btn btn-primary">Create Your Infusionsoft ID</button>
                            </div>
                            <c:if test="${!empty skipUrl}">
                                <div class="text-center skipLink">
                                    <a href="${fn:escapeXml(skipUrl)}">Skip for Now </a>
                                </div>
                            </c:if>
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
</div>
</body>
</html>
