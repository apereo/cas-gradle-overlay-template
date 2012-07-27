<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<style type="text/css">

    #login {
        color: #000;
        background: #fff;
        width: 276px;
        margin: 10px auto;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
        padding: 30px;
    }

    #forgot-password {
        width: 336px;
        margin: 5px auto;
    }

</style>

<div id="login">
    <form:form method="post" id="fm1" cssClass="form-vertical" commandName="${commandName}" htmlEscape="true">
        <form:errors path="*" id="msg" cssClass="errors" element="div" />

        <div class="control-group">
            <label for="username" class="control-label">Email Address</label>
            <div class="controls">
                <form:input cssClass="required" cssErrorClass="error" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="false" htmlEscape="true" style="width: 266px" />
            </div>
        </div>

        <div class="control-group">
            <label for="password" class="control-label">Password</label>
            <div class="controls">
                <form:password cssClass="required" cssErrorClass="error" id="password" size="25" tabindex="2" path="password" accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" style="width: 266px" />
            </div>
        </div>

        <input type="hidden" name="lt" value="${loginTicket}" />
        <input type="hidden" name="execution" value="${flowExecutionKey}" />
        <input type="hidden" name="_eventId" value="submit" />

        <div class="control-group" style="text-align: right">
            <span style="text-align: left; float: left; font-weight: normal; padding: 4px 0"><input type="checkbox" name="rememberMe" id="rememberMe" value="true" /> Stay signed in</span>

            <input class="btn btn-primary" name="submit" accesskey="l" value="Sign In" tabindex="4" type="submit" />
        </div>
    </form:form>
</div>

<div id="forgot-password">
    <c:url var="forgotPasswordUrl" value="/registration/forgot"/>
    <a href="${forgotPasswordUrl}">Forgot your password?</a>
</div>