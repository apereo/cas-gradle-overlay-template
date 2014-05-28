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
    <title><spring:message code='registration.pageTitle'/></title>
    <meta name="decorator" content="green-header-minimal"/>
    <meta name="robots" content="noindex">
    <link href="<c:url value="/css/flip.css"/>" rel="stylesheet">

    <script type="text/javascript" src="<c:url value="/js/jquery-plugins/flippy-1.3/jquery.flippy.min.js"/>"></script>
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
            }, "<spring:message code='password.error.invalid'/>");

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
                            firstName: "<spring:message code='user.error.firstName.blank'/>",
                            lastName: "<spring:message code='user.error.lastName.blank'/>",
                            username: {
                                required: "<spring:message code='user.error.email.blank'/>",
                                email: "<spring:message code='user.error.email.invalid'/>"
                            },
                            username2: {
                                required: "<spring:message code='user.error.email.blank'/>",
                                equalTo: "<spring:message code='user.error.emails.dont.match'/>"
                            },
                            password1: {
                                required: "<spring:message code='password.error.blank'/>",
                                password: "<spring:message code='password.error.invalid'/>"
                            },
                            password2: {
                                required: "<spring:message code='password.error.blank'/>",
                                equalTo: "<spring:message code='password.error.passwords.dont.match'/>"
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
        <div id="floaterLeft"><spring:message code="registration.floaterLeft"/></div>
    </c:if>
    <div id="floaterRight"><spring:message code="registration.floaterRight"/></div>
    <div id="main_title" class="jumbotron">
        <c:choose>
            <c:when test="${!empty userToken}">
                <h1><spring:message code='registration.mainTitle.migration'/></h1>
            </c:when>
            <c:otherwise>
                <h1><spring:message code='registration.mainTitle.newUser'/></h1>
            </c:otherwise>
        </c:choose>
    </div>
    <div id="lanyard_background"></div>

    <div id="lanyard">
        <div id="main_title_div"></div>
        <div id="links-above-lanyard" >
            <div id="already-have-id">
                <a href="#" class="linkToExisting"><spring:message code='registration.signin'/></a>
                <form:form id="linkToExistingForm" action="linkToExisting" method="get">
                    ${commonFormElements}
                </form:form>
            </div>
            <div id="flipLink">
                <a id="learnMore" href="#"><spring:message code='registration.what.is.InfusionsoftID'/></a>
                <a id="backBtn" style="display:none;" href="#">&laquo; <spring:message code='registration.back.to.creating.your.id'/></a>
            </div>
            <div style="clear:both;"></div>
        </div>
        <div class="flipbox-container">
            <div id="idcard">
                <div class="cardTop">
                    <div id="cardTopText">
                        <spring:message code='registration.cardTop.title'/>
                    </div>
                </div>
                <div id="cardBottom">
                    <form:form id="registerForm" action="register" method="post" class="form-vertical" modelAttribute="user">
                        ${commonFormElements}
                        <input type="hidden" name="skipUrl" value="${fn:escapeXml(skipUrl)}"/>
                        <input type="hidden" name="skipWelcomeEmail" value="${fn:escapeXml(skipWelcomeEmail)}"/>

                        <c:if test="${error != null}">
                            <div class="alert alert-error">
                                <spring:message code="${error}" text="${error}"/>
                            </div>
                        </c:if>

                        <fieldset>
                            <label class="formLabel"><spring:message code="user.full.name.label"/></label>
                            <table>
                                <tr>
                                    <td>
                                        <div style="height: 56px;" class="control-group">
                                            <form:input path="firstName" id="firstName" name="firstName" htmlEscape="true" style="width:158px;" tabindex="1" placeholder="first name"/>
                                        </div>
                                    </td>
                                    <td>
                                        <div style="height: 56px;padding-left: 22px;" class="control-group">
                                            <form:input path="lastName" id="lastName" name="lastName" htmlEscape="true" style="width:158px;" tabindex="2" placeholder="last name"/>
                                        </div>
                                    </td>
                                </tr>
                            </table>

                            <div class="form-spacer" style="height:3px;clear:both;"></div>

                            <div class="control-group">
                                <label class="formLabel"><spring:message code='registration.form.email1'/> <span class="emailHelp"><spring:message code='registration.form.email1.usage'/></span></label>
                                <form:input path="username" style="width:356px;" id="username" name="username" htmlEscape="true" tabindex="3"/>
                            </div>

                            <div class="form-spacer"></div>

                            <div class="control-group">
                                <label class="formLabel"><spring:message code='registration.form.email2'/></label>
                                <form:input path="" style="width:356px;" id="username2" name="username2" value="${fn:escapeXml(user != null ? user.username : '')}" tabindex="4"/>
                            </div>

                            <div class="form-spacer"></div>

                            <div class="password-fields">
                                <div class="control-group">
                                    <label class="formLabel"><spring:message code="registration.form.password1"/></label>
                                    <form:password path="" style="width:166px;" id="password1" name="password1" autocomplete="off" tabindex="5"/>
                                </div>

                                <div class="form-spacer"></div>
                                <div class="control-group">
                                    <label class="formLabel"><spring:message code="password.password2.label"/></label>
                                    <form:password path="" style="width:166px;" id="password2" name="password2" autocomplete="off" tabindex="6"/>
                                </div>
                            </div>
                            <div class="password-info">
                                <spring:message code="password.criteria.label"/><br/>
                                <ul>
                                    <li id="pw_length"><spring:message code="password.criteria.length"/></li>
                                    <li id="pw_number"><spring:message code="password.criteria.number"/></li>
                                    <li id="pw_upper"><spring:message code="password.criteria.uppercase"/></li>
                                    <li id="pw_under"><spring:message code="password.criteria.lowercase"/></li>
                                </ul>
                            </div>
                            <div style="clear:both;" class="form-spacer"></div>
                            <div class="control-group">
                                <div class="controls" style="position: relative;">
                                    <label class="checkbox">
                                        <input id="eula" name="eula" type="checkbox" value="agreed" tabindex="7"/>
                                        <spring:message code='registration.form.readEULA'/>
                                        <a tabindex="-1" href="http://www.infusionsoft.com/legal" target="_blank"><spring:message code='registration.link.policies'/></a>.
                                    </label>
                                </div>
                            </div>
                            <div class="text-center">
                                <button type="submit" class="btn btn-primary"><spring:message code="registration.button.create.id"/></button>
                            </div>
                            <c:if test="${!empty skipUrl}">
                                <div class="text-center skipLink">
                                    <a href="${fn:escapeXml(skipUrl)}"><spring:message code="registration.link.skip"/></a>
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
