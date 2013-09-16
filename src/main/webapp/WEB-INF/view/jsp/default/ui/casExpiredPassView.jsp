<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<meta name="decorator" content="green-header-minimal"/>

<style type="text/css">
    #reset {
        width: 420px;
        margin: 10px auto;
    }
    input[type="text"] {
        padding: 6px 6px;
    }

</style>
<link href="<c:url value="/css/flip.css"/>" rel="stylesheet">

<script>
    //Password check stuff
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

        $('#fm1').validate({
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
                    required: "<spring:message code='password.error.blank'/>",
                    password: "<spring:message code='password.error.invalid'/>"
                },
                password2: {
                    required: "<spring:message code='password.error.blank'/>",
                    equalTo: "<spring:message code='password.error.passwords.dont.match'/>"
                }
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


        $("#fm1").submit(function () {
            var form = $('#fm1');
            form.validate();
            if (form.valid()) {
                $.post("/app/profile/ajaxUpdatePassword", $("#fm1").serialize(), "json")
                    .done(function () {
                        //
                        window.location = "/login?service=${service}";
                    })
                    .fail(function (data) {
                        var myData = $.parseJSON(data.responseText);
                        // the error message is actually an embedded string that must be parsed again...
                        if(myData) {
                            $("#error").html((jQuery.parseJSON(myData)).errorMessage);
                            $("#error").removeClass("hide");
                        }
                    });
            }

            return false;
        });
    });
</script>

<div id="top-spacer" style="height: 101px;"></div>
<div id="reset">

    <div class="top-blue">
        <spring:message code="password.expired.page.title"/>
    </div>
    <div class="bottom-brown" style="padding-bottom: 27px;">
        <div id="error" class="alert alert-error hide"></div>

        <form id="fm1" class="form-vertical">

                <div class="alert alert-info">
                    <spring:message code="password.expired.page.instructions"/>
                </div>

            <div style="float:left;">
                <fieldset>
                    <div class="control-group">
                        <label class="formLabel" for="password1"><spring:message code="password.password1.label"/></label>

                        <div class="controls">
                            <input id="password1" name="password1" value="" type="password" style="width: 164px" autocomplete="off"/>
                        </div>
                    </div>
                    <div style="height: 15px"></div>
                    <div class="control-group">
                        <label class="formLabel" for="password2"><spring:message code="password.password2.label"/></label>

                        <div class="controls">
                            <input id="password2" name="password2" value="" type="password" style="width: 164px" autocomplete="off"/>
                        </div>
                    </div>
                </fieldset>

                <input name="username" type="hidden" value="${credentials.username}"/>
                <input name="currentPassword" type="hidden" value="${credentials.password}"/>
                <input id="redirectFrom" name="redirectFrom" value="expirePassword" type="hidden"/>
            </div>
            <div style="float:left;">
                <div class="password-info">
                    <spring:message code="password.criteria.label"/><br/>
                    <ul>
                        <li id="pw_length"><spring:message code="password.criteria.length"/></li>
                        <li id="pw_number"><spring:message code="password.criteria.number"/></li>
                        <li id="pw_upper"><spring:message code="password.criteria.uppercase"/></li>
                        <li id="pw_under"><spring:message code="password.criteria.lowercase"/></li>
                    </ul>
                </div>
            </div>
            <div style="clear:both;height: 20px;"></div>
            <div class="control-group" style="text-align: right">
                <input class="btn btn-primary" name="submit" accesskey="l" value="<spring:message code="password.expired.page.submit.button.text"/>" tabindex="4" type="submit"/>
            </div>
        </form>
    </div>
</div>
