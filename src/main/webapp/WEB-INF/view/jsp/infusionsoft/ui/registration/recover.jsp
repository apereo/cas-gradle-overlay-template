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

    <c:if test="${empty error}">
        <div class="alert" style="margin: -20px -20px 20px -20px">
            We have emailed you a recovery code. Copy and paste the recovery code into the field below and
            click "Next".
        </div>
    </c:if>

    <form action="recover" method="post" id="fm1" class="form-vertical">
        <div class="control-group">
            <label for="recoveryCode" class="control-label">Recovery Code</label>
            <div class="controls">
                <input type="text" class="required" id="recoveryCode" name="recoveryCode" size="25" tabindex="1" style="width: 266px" />
            </div>
        </div>

        <input name="recoveryCode" type="hidden" value="${recoveryCode}"/>

        <div class="control-group" style="text-align: right">
            <input class="btn btn-primary" name="submit" accesskey="l" value="Next" tabindex="4" type="submit" />
        </div>
    </form>
</div>

<div id="back-to-signin">
    <c:url var="loginUrl" value="/login"/>
    <a href="${loginUrl}">Back to Sign In</a>
</div>