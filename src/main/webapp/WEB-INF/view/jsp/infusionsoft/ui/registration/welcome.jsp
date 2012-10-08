<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<style type="text/css">

    #register {
        width: 330px;
        padding: 30px;
        margin: 10px auto;
        background: #fff;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
    }

    #instructions {
        font-size: 11px;
        font-style: italic;
        color: #ccc;
        text-align: center;
        margin: 15px auto;
    }

    .controls input[type=text] {
        width: 316px;
    }

    .alert {
        margin: -20px -20px 20px -20px;
    }

</style>

<div class="modal-title">
    Welcome!<br/>
    Please Create Your Infusionsoft ID
</div>

<div id="register">
    <c:url var="loginUrl" value="/login"/>

    <div class="alert alert-info">
        Already have an Infusionsoft ID? <a href="${loginUrl}">Sign in</a> to link this app to your ID.
    </div>

    <form id="registerForm" action="register" method="post" class="form-vertical">

        <c:if test="${error != null}">
            <div class="alert alert-error" style="margin-top: 10px">
                <spring:message code="${error}"/>
            </div>
        </c:if>

        <fieldset>
            <div class="control-group">
                <label class="control-label" for="firstName">First Name</label>
                <div class="controls">
                    <input id="firstName" name="firstName" value="${fn:escapeXml(user != null ? user.firstName : '')}" type="text"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="lastName">Last Name</label>
                <div class="controls">
                    <input id="lastName" name="lastName" value="${fn:escapeXml(user != null ? user.lastName : '')}" type="text"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="username">Email Address <span style="font-weight: normal">(this is your username)</span></label>
                <div class="controls">
                    <input id="username" name="username" value="${fn:escapeXml(user != null ? user.username : '')}" type="text"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="username">Password</label>
                <div class="controls">
                    <input id="password1" name="password1" value="" type="password"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="username">Confirm Password</label>
                <div class="controls">
                    <input id="password2" name="password2" value="" type="password"/>
                </div>
            </div>
            <div class="control-group">
                <div class="controls">
                    <label class="checkbox">
                        <input id="eula" name="eula" type="checkbox" value="agreed"/>
                        I agree to the terms of the <a tabindex="-1" href="http://www.infusionsoft.com/legal-stuff/eula" target="_blank">EULA</a>, <a tabindex="-1" href="http://www.infusionsoft.com/legal-stuff/aup" target="_blank">AUP</a>, <span class="eulaAnd">and</span>  <a tabindex="-1" href="http://www.infusionsoft.com/legal-stuff/billing-policies" target="_blank">Billing Policies</a>
                    </label>
                </div>
            </div>

        </fieldset>

        <div style="text-align: right; margin-top: 15px">
            <input type="submit" value="Create ID" class="btn btn-primary"/>
        </div>
    </form
</div>