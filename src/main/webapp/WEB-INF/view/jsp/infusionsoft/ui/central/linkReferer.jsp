<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<meta name="decorator" content="anonymous"/>

<c:url var="associateUrl" value="/central/associate"/>
<c:url var="homeUrl" value="/central/home"/>

<style type="text/css">

    #linkReferer {
        color: #000;
        background: #fff;
        width: 276px;
        margin: 10px auto;
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

</style>

<div id="linkReferer">
    <c:if test="${not empty error}">
        <div class="alert alert-error" style="margin: -20px -20px 20px -20px">
            <spring:message code="${error}"/>
        </div>
    </c:if>

    <form action="${associateUrl}" method="post" id="fm1" class="form-vertical">
        <input type="hidden" name="linkReferer" value="true"/>
        <input type="hidden" name="appName" value="${appName}"/>
        <input type="hidden" name="appType" value="${appType}"/>
        <input type="hidden" name="destination" value="app"/>

        <h2>Verify Account</h2>

        <p>
            We need to verify you are a user of ${appDomain}.
        </p>
        <p>
            Just this once, please enter your
            username and password for ${appDomain}.
        </p>

        <fieldset>
            <div class="control-group">
                <label class="control-label" for="username">Username</label>
                <div class="controls">
                    <input id="username" name="appUsername" value="" type="text"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="password">Password</label>
                <div class="controls">
                    <input id="password" name="appPassword" value="" type="password"/>
                </div>
            </div>
        </fieldset>

        <div class="control-group" style="text-align: right">
            <input class="btn btn-primary" name="submit" accesskey="l" value="Verify" tabindex="4" type="submit" />
            <a href="${homeUrl}" class="btn">Cancel</a>
        </div>
    </form>
</div>

<c:if test="${appType == 'crm'}">
    <div id="forgot-password">
        <a href="https://${appDomain}/app/forgotPassword/enterEmail">Forgot your password on ${appDomain}?</a>
    </div>
</c:if>
