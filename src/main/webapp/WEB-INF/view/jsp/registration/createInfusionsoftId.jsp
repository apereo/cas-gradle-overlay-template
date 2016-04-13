<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="versioned" tagdir="/WEB-INF/tags/common/page" %>

<%--@elvariable id="registrationCode" type="java.lang.String"--%>
<%--@elvariable id="returnUrl" type="java.lang.String"--%>
<%--@elvariable id="userToken" type="java.lang.String"--%>
<%--@elvariable id="skipUrl" type="java.lang.String"--%>
<%--@elvariable id="skipWelcomeEmail" type="java.lang.Boolean"--%>
<%--@elvariable id="error" type="java.lang.String"--%>
<%--@elvariable id="user" type="com.infusionsoft.cas.domain.User"--%>
<%--@elvariable id="securityQuestions" type="java.util.List<SecurityQuestion>"--%>

<c:set var="createInfusionsoftIdJs" value="${pageContext.request.contextPath}/js/createInfusionsoftId.js"/>

<c:set var="infusionsoftIdImage" value="${pageContext.request.contextPath}/img/infusionsoft_Id.png"/>

<c:set var="buildVersion" value="<%=com.infusionsoft.cas.support.BuildVersion.getBuildVersion()%>"/>

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
    <meta name="decorator" content="black-header-minimal"/>
    <meta name="robots" content="noindex">
</head>
<body>

<div class="container">
    <div class="row">
        <div class="col-xs-12 text-center">
            <div class="page-header">
                <h1><spring:message code='registration.mainTitle.newUser'/></h1>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-xs-12 col-sm-9 col-md-6 col-lg-5 col-centered">
            <div class="panel panel-default">
                <div class="panel-heading text-center">
                    <versioned:img src="${infusionsoftIdImage}"/>

                    <div>
                        <spring:message code='registration.cardTop.title'/>
                    </div>
                </div>
                <div class="panel-body">
                    <form:form id="linkToExistingForm" action="linkToExisting" method="get">
                        ${commonFormElements}
                    </form:form>

                    <form:form id="registerForm" action="register" method="post" modelAttribute="user" role="form">
                        ${commonFormElements}
                        <input type="hidden" name="skipUrl" value="${fn:escapeXml(skipUrl)}"/>
                        <input type="hidden" name="skipWelcomeEmail" value="${fn:escapeXml(skipWelcomeEmail)}"/>

                        <form:errors path="*" id="msg" cssClass="text-error" element="p">
                            <p class="text-error">
                                <versioned:objectSvg tabindex="-1" data="/img/ic-exclamation-circle.svg" width="16" height="16" />
                                <c:forEach var="error" items="${messages}">
                                    ${error}
                                </c:forEach>
                            </p>
                        </form:errors>

                        <c:if test="${error != null}">
                            <p class="text-error">
                                <versioned:objectSvg tabindex="-1" data="/img/ic-exclamation-circle.svg" width="16" height="16"/>
                                <spring:message code="${error}" text="${error}"/>
                            </p>
                        </c:if>

                        <div class="form-group">
                            <label><spring:message code="user.firstName.label"/></label>
                            <form:input class="form-control" path="firstName" id="firstName" name="firstName" htmlEscape="true" tabindex="1" placeholder="first name"/>
                        </div>

                        <div class="form-group">
                            <label><spring:message code="user.lastName.label"/></label>
                            <form:input class="form-control" path="lastName" id="lastName" name="lastName" htmlEscape="true" tabindex="2" placeholder="last name"/>
                        </div>

                        <div class="form-group">
                            <label><spring:message code='registration.form.email1'/>
                                <small class="emailHelp">
                                    <spring:message code='registration.form.email1.usage'/>
                                </small>
                            </label>
                            <form:input class="form-control" path="username" id="username" name="username" htmlEscape="true" tabindex="3"/>
                        </div>

                        <div class="form-group">
                            <label><spring:message code='registration.form.email2'/></label>
                            <form:input class="form-control" path="" id="username2" name="username2" value="${fn:escapeXml(user != null ? user.username : '')}" tabindex="4"/>
                        </div>

                        <div class="row">
                            <div class="col-xs-12 col-sm-5 col-lg-5">
                                <div class="form-group">
                                    <label><spring:message code="registration.form.password1"/></label>
                                    <form:password class="form-control" path="" id="password1" name="password1" autocomplete="off" tabindex="5"/>
                                </div>

                                <div class="form-group">
                                    <label><spring:message code="password.password2.label"/></label>
                                    <form:password class="form-control" path="" id="password2" name="password2" autocomplete="off" tabindex="6"/>
                                </div>
                            </div>
                            <div class="col-xs-12 col-sm-7 col-lg-7">
                                <div class="well well-sm">
                                    <spring:message code="password.criteria.label"/><br/>
                                    <ul>
                                        <li id="pw_length"><spring:message code="password.criteria.length"/></li>
                                        <li id="pw_number"><spring:message code="password.criteria.number"/></li>
                                        <li id="pw_upper"><spring:message code="password.criteria.uppercase"/></li>
                                        <li id="pw_under"><spring:message code="password.criteria.lowercase"/></li>
                                    </ul>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label><spring:message code='registration.form.security.question'/></label>
                            <form:select path="" value="" cssClass="form-control" id="securityQuestionId" name="securityQuestionId" tabindex="7">
                                <option value="" disabled selected><spring:message code='registration.form.security.question.placeholder'/></option>
                                <form:options items="${securityQuestions}" itemValue="id" itemLabel="question" />
                            </form:select>
                        </div>

                        <div class="form-group">
                            <label><spring:message code='registration.form.security.answer'/></label>
                            <form:input class="form-control" path="" id="securityQuestionAnswer" name="securityQuestionAnswer" tabindex="7"/>
                        </div>

                        <div class="row">
                            <div class="col-xs-12 text-center">
                                <div class="form-group">
                                    <div class="checkbox">
                                        <label>
                                            <input id="eula" name="eula" type="checkbox" value="agreed" tabindex="7"/>
                                            <span class="checkbox-label">
                                                <spring:message code='registration.form.readEULA'/>
                                                <a tabindex="-1" href="http://www.infusionsoft.com/legal" target="_blank"><spring:message code='registration.link.policies'/></a>
                                            </span>
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">

                            <div class="col-xs-12 col-xs-push-0 col-sm-6 col-sm-push-6 text-center">
                                <button type="submit" class="btn btn-primary btn-block"><spring:message code="registration.button.create.id"/></button>
                            </div>

                            <div class="col-xs-12 col-xs-pull-0 col-sm-6 col-sm-pull-6 text-center">
                                <a href="#" class="btn btn-link linkToExisting"><spring:message code='registration.signin'/></a>
                            </div>

                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>

<content tag="local_script">
    <versioned:script type="text/javascript" src="${createInfusionsoftIdJs}"/>

    <script type="text/javascript">
        function resetCheckPassword() {
            $("#pw_length").css({"list-style-image": ''});
            $("#pw_number").css({"list-style-image": ''});
            $("#pw_upper").css({"list-style-image": ''});
            $("#pw_under").css({"list-style-image": ''});
        }
        function checkPasswordReq() {
            var currPass = $('#password1').val();
            resetCheckPassword();
            if (currPass.length >= 7) {
                $("#pw_length").css({"list-style-image": "url('/img/checkmark.png?b=${buildVersion}')"});
            }
            if (/\d/.test(currPass)) {
                $("#pw_number").css({"list-style-image": "url('/img/checkmark.png?b=${buildVersion}')"});
            }
            if (/[a-z]/.test(currPass)) {
                $("#pw_under").css({"list-style-image": "url('/img/checkmark.png?b=${buildVersion}')"});
            }
            if (/[A-Z]/.test(currPass)) {
                $("#pw_upper").css({"list-style-image": "url('/img/checkmark.png?b=${buildVersion}')"});
            }
        }
        setInterval(function () {
            checkPasswordReq();
        }, 100);

        $(document).ready(function () {
            jQuery.validator.addMethod("password", function (value, element) {
                return this.optional(element) || value.length >= 7 && /\d/.test(value) && /[a-z]/.test(value) && /[A-Z]/.test(value);
            }, "<spring:message code='password.error.invalid'/>");

            //Check to ensure that username matches
            //Validate the form
            $('#registerForm').validate({
                errorElement: "span",
                errorClass: "help-block",
                errorPlacement: function (error, element) {
                    console.log('error placement');
                    if (element.parent().is('label')) {
                        element.next().after(error);
                    } else {
                        element.after('<span class="is-icon is-icon-error form-control-feedback"><object type="image/svg+xml" tabindex="-1" data="/img/ic-message-danger.svg?b=${buildVersion}" width="4" height="16"></object></span>');
                        element.after(error);
                    }

                },
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
                    securityQuestionId: {
                        required: true
                    },
                    securityQuestionAnswer: {
                        required: true
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
                    securityQuestion: "<spring:message code='user.error.security.question.blank'/>",
                    securityQuestionAnswer: "<spring:message code='user.error.security.question.answer.blank'/>",
                    eula: "<spring:message code='registration.error.eula'/>"

                },
                highlight: function (element) {
                    $(element).closest('.form-group').addClass('has-error has-feedback');

                    //Unhide error message
                    $(element).next().removeClass("hidden");
                },
                success: function (element) {
                    element.closest('.form-group').removeClass('has-error has-feedback');

                    //Hide error message
                    element.addClass("hidden");

                    element.next().addClass("hidden");
                }

            });
        });
    </script>
</content>

</body>
</html>
