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

    #back-to-signin {
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
        <input type="hidden" name="appName" value="${appName}"/>
        <input type="hidden" name="appType" value="${appType}"/>

        <h2>Verify That You Are A User Of This App</h2>

        <p>
            In order to sign in you need to verify that you have been a user of this app by entering the username
            and password you were using before you created your Infusionsoft ID.
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
        </div>
    </form>
</div>
