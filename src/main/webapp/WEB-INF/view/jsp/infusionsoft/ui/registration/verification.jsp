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

    #verification {
        color: #000;
        background: #fff;
        width: 276px;
        margin: 10px auto;
        border: 1px solid #DDDDDD;
        border-radius: 4px;
        padding: 30px;
    }

</style>

<div id="verification">
    <c:if test="${error != null}">
        <div class="alert">
            <spring:message code="${error}"/>
        </div>
    </c:if>

    <p>
        Please verify that you have been a user of this app by entering the username and password you were using
        before creating a new Infusionsoft ID.
    </p>
    <form action="verify" method="post" id="verificationForm" cssClass="form-vertical">
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
            <input class="btn btn-primary" name="submit" accesskey="l" value="Verify" tabindex="4" type="submit" />
        </div>
    </form>
</div>
