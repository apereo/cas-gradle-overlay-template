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

</style>

<script type="text/javascript">

    function isValidEmailAddress(emailAddress) {
        var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
        return pattern.test(emailAddress);
    };

    $(document).ready(function() {
        $("#password-requirements").qtip({
            content: "Your password needs to contain at least 7 characters and must include at least one number, an uppercase letter, and a lowercase letter.",
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

        //Check to ensure that username matches
        $('#username2').focus(function() {
            $('#usernameMismatchAlert').hide();
            $('#username2MismatchAlert').hide();
            if (isValidEmailAddress($('#username').val())) {
                $('#usernameDiv').removeClass("error");
            }
            $('#username2Div').removeClass("error");
        }).blur(function() {
            if ($('#username').val() !=  "" && $('#username').val() != $('#username2').val()) {
                $('#usernameDiv').addClass("error");
                $('#username2Div').addClass("error");
                $('#usernameMismatchAlert').show();
                $('#username2MismatchAlert').show();
            }
        });

        $('#username').focus(function() {
            $('#usernameMismatchAlert').hide();
            $('#username2MismatchAlert').hide();
            $("#usernameNotValidAlert").hide();
            $('#usernameDiv').removeClass("error");
            $('#username2Div').removeClass("error");
        }).blur(function() {
            //check to see if we have a valid email address:
            if (!isValidEmailAddress($('#username').val())) {
                $('#usernameDiv').addClass("error");
                $("#usernameNotValidAlert").show();
            }
            else if ($('#username2').val() != "" && $('#username').val() != $('#username2').val()) {
                $('#usernameDiv').addClass("error");
                $('#username2Div').addClass("error");
                $('#usernameMismatchAlert').show();
                $('#username2MismatchAlert').show();
            }
        });

        //Check for password mismatches
        $('#password2').focus(function() {
            $('#password1MismatchAlert').hide();
            $('#password2MismatchAlert').hide();
            $('#password1Div').removeClass("error");
            $('#password2Div').removeClass("error");
        }).blur(function() {
            if ($('#password1').val() !=  "" && $('#password2').val() != $('#password1').val()) {
                $('#password1Div').addClass("error");
                $('#password2Div').addClass("error");
                $('#password1MismatchAlert').show();
                $('#password2MismatchAlert').show();
            }
        });

        $('#password1').focus(function() {
            $('#password1MismatchAlert').hide();
            $('#password2MismatchAlert').hide();
            $('#password1Div').removeClass("error");
            $('#password2Div').removeClass("error");
        }).blur(function() {
            if ($('#password2').val() !=  "" && $('#password2').val() != $('#password1').val()) {
                $('#password1Div').addClass("error");
                $('#password2Div').addClass("error");
                $('#password1MismatchAlert').show();
                $('#password2MismatchAlert').show();
            }
        });

        $('#firstName').focus(function() {
            $("#firstNameRequired").hide();
            $('#firstNameDiv').removeClass("error");
        }).blur(function () {
            if(!$.trim(this.value).length) {
                $("#firstNameRequired").show();
                $('#firstNameDiv').addClass("error");
            }
        });

        $('#lastName').focus(function() {
            $("#lastNameRequired").hide();
            $('#lastNameDiv').removeClass("error");
        }).blur(function () {
            if(!$.trim(this.value).length) {
                $("#lastNameRequired").show();
                $('#lastNameDiv').addClass("error");
            }
        });

        $("#submitButton").click(function() {
            if(validate()){
                $(this).closest("form").submit();
                //alert("submit form!");
            } else {
                $('#submitError').show();
            }
        });

        function validate() {
            var retVal = true;
            if (
                $('#firstName').val() ==  ""  ||
                $('#lastName').val()  ==  ""  ||
                $('#username').val()  ==  ""  ||
                $('#username2').val() ==  ""  ||
                $('#password1').val() ==  ""  ||
                $('#password2').val() ==  ""  ||
                $('#username').val() != $('#username2').val() ||
                $('#password1').val() != $('#password2').val()
            ) {
                retVal = false;
            }

            return retVal;
        }



    });

</script>

<div class="modal-title">
    Fill Out The Fields Below To Create<br/>Your Infusionsoft ID.
</div>

<div class="instructions">
    All fields are required
</div>

<div id="register">
    <c:url var="linkToExistingUrl" value="/app/registration/linkToExisting"/>

    <div class="alert alert-info">
        Already created your Infusionsoft ID? <a href="${linkToExistingUrl}?registrationCode=${registrationCode}">Sign in</a>
    </div>

    <form id="registerForm" action="register" method="post" class="form-vertical">
        <input type="hidden" name="registrationCode" value="${registrationCode}"/>

        <c:if test="${error != null}">
            <div class="alert alert-error" style="margin-top: 10px;">
                <spring:message code="${error}" text="${error}"/>
            </div>
        </c:if>
        <div id="submitError" class="alert alert-error" style="display:none; margin-top: 10px;">
            Please correct the errors before submitting the form
        </div>

        <fieldset>
            <div id="firstNameDiv" class="control-group">
                <label class="control-label" for="firstName">First Name</label>
                <div class="controls">
                    <input id="firstName" name="firstName" tabindex="1" value="${fn:escapeXml(user != null ? user.firstName : '')}" type="text"/>
                    <span id="firstNameRequired" style="display:none" class="help-inline">First Name is required.</span>
                </div>
            </div>
            <div id="lastNameDiv" class="control-group">
                <label class="control-label" for="lastName">Last Name</label>
                <div class="controls">
                    <input id="lastName" name="lastName" tabindex="2" value="${fn:escapeXml(user != null ? user.lastName : '')}" type="text"/>
                    <span id="lastNameRequired" style="display:none" class="help-inline">Last Name is required.</span>
                </div>
            </div>
            <div id="usernameDiv" class="control-group">
                <label class="control-label" for="username">Email Address <span style="font-weight: normal">(this will be your Infusionsoft ID)</span></label>
                <div class="controls">
                    <input id="username" name="username" tabindex="3" value="${fn:escapeXml(user != null ? user.username : '')}" type="text"/>
                    <span id="usernameMismatchAlert" style="display:none" class="help-inline">Email addresses don't match.</span>
                    <span id="usernameNotValidAlert" style="display:none" class="help-inline">Please enter a valid email address. (e.g. email@example.com)</span>
                </div>
            </div>
            <div id="username2Div" class="control-group">
                <label class="control-label" for="username">Retype Email Address </label>
                <div class="controls">
                    <input id="username2" name="username2" tabindex="4" type="text"/>
                    <span id="username2MismatchAlert" style="display:none" class="help-inline">Email addresses don't match.</span>
                </div>
            </div>
            <div id="password1Div" class="control-group">
                <label id="password-requirements" class="control-label" style="float: right; font-weight: normal; margin-right: 5px">
                    <a href="javascript:return false" tabindex="99">Password Requirements</a>
                </label>
                <label class="control-label" for="username">Password</label>
                <div class="controls">
                    <input id="password1" name="password1" value="" type="password" tabindex="5"/>
                    <span id="password1MismatchAlert" style="display:none" class="help-inline">Passwords don't match.</span>
                </div>
            </div>
            <div id="password2Div" class="control-group">
                <label class="control-label" for="username">Confirm Password</label>
                <div class="controls">
                    <input id="password2" name="password2" value="" type="password" tabindex="6"/>
                    <span id="password2MismatchAlert" style="display:none" class="help-inline">Passwords don't match.</span>
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <label class="checkbox">
                        <input id="eula" name="eula" type="checkbox" value="agreed" tabindex="7"/>
                        I have read and agree to abide by Infusionsoft's
                        <a tabindex="-1" href="http://www.infusionsoft.com/legal-stuff/eula" target="_blank">End User Licensing Agreement</a>,
                        <a tabindex="-1" href="http://www.infusionsoft.com/legal-stuff/aup" target="_blank">Acceptable Use Policy</a>,
                        <a tabindex="-1" href="http://www.infusionsoft.com/legal-stuff/billing-policies" target="_blank">Billing Policy</a> and
                        <a tabindex="-1" href="http://www.infusionsoft.com/legal-stuff/privacy-policy" target="_blank">Privacy Policy</a>.
                    </label>
                </div>
            </div>

        </fieldset>

        <div style="text-align: right; margin-top: 15px">
            <input id="submitButton" type="button" value="Create ID" class="btn btn-primary" tabindex="8"/>
        </div>
    </form
</div>