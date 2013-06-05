<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<meta name="decorator" content="anonymous"/>

<style type="text/css">

    #recover {
        color: #000;
        background: #fff;
        width: 280px;
        margin: 10px auto;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
        padding: 30px;
    }

    #back-to-signin {
        width: 336px;
        margin: 5px auto;
    }

</style>

<script type="text/javascript">

    $(document).ready(function() {
        jQuery.validator.addMethod("password", function( value, element ) {
            var result = this.optional(element) || value.length >= 7 && /\d/.test(value) && /[a-z]/.test(value) && /[A-Z]/.test(value);
            return result;
        }, "<spring:message code='registration.error.invalidPassword'/>");

        //Check to ensure that password matches
        //Validate the form
        $('#fm1').validate(
                {
                    rules: {
                        password1: {
                            required: true,
                            password: true
                        },
                        password2: {
                            required: true,
                            password: false,
                            equalTo: "#password1"
                        }
                    },
                    messages: {
                        password1: {
                            required:  "<spring:message code='registration.error.passwordsRequired'/>",
                            password: "<spring:message code='registration.error.invalidPassword'/>"
                        },
                        password2: {
                            required:  "<spring:message code='registration.error.passwordsRequired'/>",
                            equalTo: "<spring:message code='registration.error.passwordsNoMatch'/>"
                        }
                    },
                    highlight: function(element) {
                        $(element).closest('.control-group').removeClass('success').addClass('error');
                    },
                    success: function(element) {
                        element.closest('.control-group').removeClass('error');
                        element.closest('label.error').hide().removeClass('error').addClass('valid').addClass('error');
                    }

                });
    });

</script>

<div id="recover">
    <c:if test="${not empty error}">
        <div class="alert alert-error" style="margin: -20px -20px 20px -20px">
            <spring:message code="${error}"/>
        </div>
    </c:if>

    <form action="reset" method="post" id="fm1" class="form-vertical">
        <h2>Please Create A New Password</h2>

        <fieldset>
            <div class="control-group">
                <label class="control-label" for="password1">Password</label>
                <div class="controls">
                    <input id="password1" name="password1" value="" type="password" style="width: 266px"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="password2">Retype Password</label>
                <div class="controls">
                    <input id="password2" name="password2" value="" type="password" style="width: 266px"/>
                </div>
            </div>
        </fieldset>

        <input name="recoveryCode" type="hidden" value="${recoveryCode}"/>

        <div class="control-group" style="text-align: right">
            <input class="btn btn-primary" name="submit" accesskey="l" value="Change Password" tabindex="4" type="submit" />
        </div>
    </form>
</div>

<div id="back-to-signin">
    <c:url var="loginUrl" value="/login"/>
    <a href="${loginUrl}">Back to Sign In</a>
</div>