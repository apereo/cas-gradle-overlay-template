<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="central"/>

<c:url var="centralUrl" value="/app/central/home"/>

<script type="text/javascript">

</script>

<style type="text/css">

    .form-horizontal .control-label {
        width: 120px;
    }

    .form-horizontal .controls {
        margin-left: 135px;
    }

</style>

<div id="main">
    <h2 class="apps">
        Connect an Infusionsoft Account
    </h2>

    <c:choose>
        <c:when test="${connectError == 'registration.error.expiredLegacyCredentials'}">
            <div class="alert alert-error" style="margin-top: 10px">
                Your old password is expired! Please
                <a target="oldapp" href="${appUrl}/app/authentication/login">sign in the old way</a>
                to reset it, then try again.
            </div>
        </c:when>
        <c:when test="${connectError == 'registration.error.invalidLegacyCredentials'}">
            <div class="alert alert-error" style="margin-top: 10px">
                You've entered an incorrect username and/or password.
                <a target="oldapp" href="https://${appDomain}/app/forgotPassword/enterEmail">Forgot your password on ${appDomain}?</a>
            </div>
        </c:when>
        <c:when test="${connectError != null}">
            <div class="alert alert-error" style="margin-top: 10px">
                <spring:message code="${connectError}"/>
            </div>
        </c:when>
    </c:choose>

    <p>
        To connect an account to your Infusionsoft ID, enter the username and password you were using before you
        created your Infusionsoft ID.
    </p>

    <form id="associateForm" action="associate" method="post" class="form-horizontal">
        <input name="appType" type="hidden" value="crm"/>
        <input name="user" type="hidden" value="${user.id}"/>

        <fieldset>
            <div class="control-group">
                <label for="appName" class="control-label">Account URL</label>
                <div class="controls">
                    <input id="appName" name="appName" type="text" value="${fn:escapeXml(appName)}"/>.${crmDomain}
                </div>
            </div>
            <div class="control-group">
                <label for="appUsername" class="control-label">Username</label>
                <div class="controls">
                    <input id="appUsername" name="appUsername" type="text" value="${fn:escapeXml(appUsername)}" style="width: 300px"/>
                </div>
            </div>
            <div class="control-group">
                <label for="appPassword" class="control-label">Password</label>
                <div class="controls">
                    <input id="appPassword" name="appPassword" type="password" value="" style="width: 300px"/>
                </div>
            </div>
        </fieldset>

        <div class="buttonbar">
            <input type="submit" value="Connect Account" class="btn btn-primary"/>
            <a href="${centralUrl}" class="btn">Cancel</a>
        </div>
    </form>
</div>
