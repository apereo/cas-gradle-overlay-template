<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<meta name="decorator" content="modal"/>

<c:url var="associateUrl" value="/central/associate"/>
<c:url var="homeUrl" value="/central/home"/>

<style type="text/css">

    #linkReferer {
        color: #000;
        background: #fff;
        width: 276px;
        margin: 0 auto;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
        padding: 30px;
    }

    #linkReferer input[type=text], #linkReferer input[type=password] {
        width: 266px;
    }

    #forgot-password {
        width: 336px;
        margin: 5px auto;
    }

    #bypass {
        text-align: center;
        margin: 28px 0 0 0;
    }

    #bypass a {
        color: #aaa;
    }

</style>

<div class="modal-title">
    Please Connect This <br/> Account To Your Infusionsoft ID.
</div>
<div class="instructions">
    All fields are required
</div>
<div id="linkReferer">
    <c:choose>
        <c:when test="${error == 'registration.error.expiredLegacyCredentials'}">
            <div class="alert alert-error" style="margin: -20px -20px 20px -20px">
                Your old password is expired! Please
                <a target="oldapp" href="${appUrl}/app/authentication/login">sign in the old way</a>
                to reset it, then try again.
            </div>
        </c:when>
        <c:when test="${error != null}">
            <div class="alert alert-error" style="margin: -20px -20px 20px -20px">
                <spring:message code="${error}"/>
            </div>
        </c:when>
    </c:choose>

    <p>
        This account, ${appDomain}, must be connected to your Infusionsoft ID before you can access it.
    </p>
    <p>
        To connect this account to your Infusionsoft ID, enter the username and password you were using before you
        created your Infusionsoft ID.
    </p>
    <form action="${associateUrl}" method="post" id="fm1" cssClass="form-vertical">
        <input type="hidden" name="linkReferer" value="true"/>
        <input type="hidden" name="appName" value="${appName}"/>
        <input type="hidden" name="appType" value="${appType}"/>
        <input type="hidden" name="destination" value="app"/>

        <div class="control-group">
            <label for="appUsername" class="control-label">Username</label>
            <div class="controls">
                <input type="text" name="appUsername" id="appUsername" autocomplete="false" style="width: 266px" />
            </div>
        </div>

        <div class="control-group">
            <label for="appPassword" class="control-label">Password</label>
            <div class="controls">
                <input type="password" name="appPassword" id="appPassword" autocomplete="false" style="width: 266px" />
            </div>
        </div>

        <div class="row btn-row" style="text-align: right">
            <input class="btn btn-primary" name="submit" accesskey="l" value="Connect Account" tabindex="4" type="submit" />
        </div>
    </form>
</div>

<c:if test="${appType == 'crm'}">
    <div id="forgot-password">
        <a href="https://${appDomain}/app/forgotPassword/enterEmail">Forgot your previous password for ${appDomain}?</a>
    </div>
</c:if>

<div id="bypass">
    <a href="/central/home">Go To Account Central</a>
</div>
