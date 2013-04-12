<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<meta name="decorator" content="anonymous"/>

<style type="text/css">

    #recover {
        color: #000;
        background: #fff;
        width: 276px;
        margin: 10px auto;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
        padding: 30px;
    }

    #back-to-signin {
        width: 336px;
        margin: 5px auto;
    }

</style>

<div id="recover">
    <c:if test="${not empty error}">
        <div class="alert alert-error" style="margin: -20px -20px 20px -20px">
            <spring:message code="${error}"/>
        </div>
    </c:if>

    <form action="reset" method="post" id="fm1" class="form-vertical">
        <h2>Please Create A New Password</h2>

        <fieldset>
            <div class="control-group">
                <label class="control-label" for="password1">Password</label>
                <div class="controls">
                    <input id="password1" name="password1" value="" type="password" style="width: 266px"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="password2">Confirm Password</label>
                <div class="controls">
                    <input id="password2" name="password2" value="" type="password" style="width: 266px"/>
                </div>
            </div>
        </fieldset>

        <input name="recoveryCode" type="hidden" value="${recoveryCode}"/>

        <div class="control-group" style="text-align: right">
            <input class="btn btn-primary" name="submit" accesskey="l" value="Change Password" tabindex="4" type="submit" />
        </div>
    </form>
</div>

<div id="back-to-signin">
    <c:url var="loginUrl" value="/login"/>
    <a href="${loginUrl}">Back to Sign In</a>
</div>