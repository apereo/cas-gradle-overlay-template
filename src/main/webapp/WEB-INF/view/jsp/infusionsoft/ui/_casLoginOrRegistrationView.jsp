<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<style type="text/css">

    #login-or-register {
        color: #000;
        background: #fff;
        width: 680px;
        margin: 10px auto;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
    }

    #login-or-register-left {
        width: 280px;
        padding: 30px;
        float: left;
        border-right: 1px solid #DDDDDD;
    }

    #login-or-register-right {
        width: 279px;
        padding: 30px;
        float: left;
    }

</style>

<div id="login-or-register">
    <div id="login-or-register-left">
        <form:form method="post" id="fm1" cssClass="form-vertical" commandName="${commandName}" htmlEscape="true">
            <form:errors path="*" id="msg" cssClass="errors" element="div" />

            <h2>Sign In With Your Infusionsoft ID</h2>

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

            <div class="row btn-row" style="text-align: right">
                <input type="hidden" name="lt" value="${loginTicket}" />
                <input type="hidden" name="execution" value="${flowExecutionKey}" />
                <input type="hidden" name="_eventId" value="submit" />

                <input class="btn btn-primary" name="submit" accesskey="l" value="Sign In" tabindex="4" type="submit" />
            </div>
        </form:form>
    </div>

    <div id="login-or-register-right">
        <h2>Don't have an Infusionsoft ID?</h2>
        <p>
            Having an Infusionsoft ID allows you to sign into all of your Infusionsoft
            apps with the same email address and password.
        </p>
        <p>
            You no longer have to remember multiple user names and passwords.
            <a href="#">Learn more</a>
        </p>
        <div style="text-align: center">
            <a class="btn" href="registration/welcome">Create Your ID</a>
        </div>
    </div>
    <div style="clear: both"></div>
</div>
