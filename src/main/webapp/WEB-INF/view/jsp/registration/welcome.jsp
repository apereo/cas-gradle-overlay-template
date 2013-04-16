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

</style>

<script type="text/javascript">

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
            <div class="alert alert-error" style="margin-top: 10px">
                <spring:message code="${error}" text="${error}"/>
            </div>
        </c:if>

        <fieldset>
            <div class="control-group">
                <label class="control-label" for="firstName">First Name</label>
                <div class="controls">
                    <input id="firstName" name="firstName" tabindex="1" value="${fn:escapeXml(user != null ? user.firstName : '')}" type="text"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="lastName">Last Name</label>
                <div class="controls">
                    <input id="lastName" name="lastName" tabindex="2" value="${fn:escapeXml(user != null ? user.lastName : '')}" type="text"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="username">Email Address <span style="font-weight: normal">(this will be your Infusionsoft ID)</span></label>
                <div class="controls">
                    <input id="username" name="username" tabindex="3"value="${fn:escapeXml(user != null ? user.username : '')}" type="text"/>
                </div>
            </div>
            <div class="control-group">
                <label id="password-requirements" class="control-label" style="float: right; font-weight: normal; margin-right: 5px">
                    <a href="javascript:return false" tabindex="99">Password Requirements</a>
                </label>
                <label class="control-label" for="username">Password</label>
                <div class="controls">
                    <input id="password1" name="password1" value="" type="password" tabindex="4"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="username">Confirm Password</label>
                <div class="controls">
                    <input id="password2" name="password2" value="" type="password" tabindex="5"/>
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <label class="checkbox">
                        <input id="eula" name="eula" type="checkbox" value="agreed" tabindex="6"/>
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
            <input type="submit" value="Create ID" class="btn btn-primary" tabindex="7"/>
        </div>
    </form
</div>