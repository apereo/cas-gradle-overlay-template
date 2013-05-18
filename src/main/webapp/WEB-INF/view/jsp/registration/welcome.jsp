<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<meta name="decorator" content="modal"/>

<style type="text/css">

    #register {
        width: 330px;
        padding: 30px;
        margin: 10px auto;
        background: #fff;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
    }

    .controls input[type=text], .controls input[type=password] {
        width: 316px;
    }

    .alert {
        margin: -20px -20px 20px -20px;
    }

    .control-group.error .control-label, .control-group.error .help-block, .controls input {
        color: #444;
    }

    .control-group.error .checkbox, .control-group.error .radio, .control-group.error input, .control-group.error select, .control-group.error textarea {
        color: #444;
    }

    .help-inline {
        position: relative;
        bottom: 10px;
        right: 5px;
    }

    label.valid {
        height: 0;
        display: none;
        padding: 0;
        margin: 0;
    }
    label.error {
        color: #b94a48;
        padding: 1px 8px;
    }

</style>

<script type="text/javascript">

    $(document).ready(function() {
        $("#password-requirements").qtip({
            content: "<spring:message code='registration.error.invalidPassword'/>",
            position: {
                corner: {
                    tooltip: "leftMiddle",
                    target: "rightMiddle"
                }
            },
            show: "click",
            hide: "mouseout",
            style: {
                border: {
                    width: 6,
                    radius: 6
                },
                padding: 15,
                textAlign: 'left',
                tip: true,
                name: "cream"
            }
        });

        jQuery.validator.addMethod("password", function( value, element ) {
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
                    required:  "<spring:message code='registration.error.passwordsRequired'/>",
                    password: "<spring:message code='registration.error.invalidPassword'/>"
                },
                password2: {
                    required:  "<spring:message code='registration.error.passwordsRequired'/>",
                    equalTo: "<spring:message code='registration.error.passwordsNoMatch'/>"
                },
                eula: "<spring:message code='registration.error.eula'/>"

            },
            highlight: function(element) {
                $(element).closest('.control-group').removeClass('success').addClass('error');
            },
            success: function(element) {
                element.closest('.control-group').removeClass('error').addClass('success');
                element.closest('label.error').hide().removeClass('error').addClass('valid').addClass('error');
            }

        });



    });

</script>

<div class="modal-title">
    <spring:message code='registration.mainTitle'/><br/><spring:message code='yourInfusionsoft.id.label'/>.
</div>

<div class="instructions">
    <spring:message code='registration.instructions'/>
</div>

<div id="register">
    <c:url var="linkToExistingUrl" value="/app/registration/linkToExisting"/>

    <div class="alert alert-info">
        <spring:message code='registration.alreadyHaveAccount'/> <a href="${linkToExistingUrl}?registrationCode=${registrationCode}"><spring:message code='registration.signin'/></a>
    </div>

    <form id="registerForm" action="register" method="post" class="form-vertical">
        <input type="hidden" name="registrationCode" value="${registrationCode}"/>

        <c:if test="${error != null}">
            <div class="alert alert-error" style="margin-top: 10px;">
                <spring:message code="${error}" text="${error}"/>
            </div>
        </c:if>

        <fieldset>
            <div id="firstNameDiv" class="control-group">
                <label class="control-label" for="firstName"><spring:message code='registration.form.firstName'/></label>
                <div class="controls">
                    <input id="firstName" name="firstName" tabindex="1" value="${fn:escapeXml(user != null ? user.firstName : '')}" type="text"/>
                </div>
            </div>
            <div id="lastNameDiv" class="control-group">
                <label class="control-label" for="lastName"><spring:message code='registration.form.lastName'/></label>
                <div class="controls">
                    <input id="lastName" name="lastName" tabindex="2" value="${fn:escapeXml(user != null ? user.lastName : '')}" type="text"/>
                </div>
            </div>
            <div id="usernameDiv" class="control-group">
                <label class="control-label" for="username"><spring:message code='registration.form.email1'/> <span style="font-weight: normal"> (<spring:message code='registration.form.email1Hint'/>)</span></label>
                <div class="controls">
                    <input id="username" name="username" tabindex="3" value="${fn:escapeXml(user != null ? user.username : '')}" type="text"/>
                </div>
            </div>
            <div id="username2Div" class="control-group">
                <label class="control-label" for="username2"><spring:message code='registration.form.email2'/></label>
                <div class="controls">
                    <input id="username2" name="username2" tabindex="4" type="text"/>
                </div>
            </div>
            <div id="password1Div" class="control-group">
                <label id="password-requirements" class="control-label" style="float: right; font-weight: normal; margin-right: 5px">
                    <a href="javascript:return false" tabindex="99"><spring:message code='registration.link.passwordHints'/> </a>
                </label>
                <label class="control-label" for="password1"><spring:message code='registration.form.password1'/></label>
                <div class="controls">
                    <input id="password1" name="password1" value="" type="password" tabindex="5"/>
                </div>
            </div>
            <div id="password2Div" class="control-group">
                <label class="control-label" for="password2"><spring:message code='registration.form.password2'/></label>
                <div class="controls">
                    <input id="password2" name="password2" value="" type="password" tabindex="6"/>
                </div>
            </div>
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

        </fieldset>

        <div style="text-align: right; margin-top: 15px">
            <input id="submitButton" type="submit" value="Create ID" class="btn btn-primary" tabindex="8"/>
        </div>
    </form
</div>