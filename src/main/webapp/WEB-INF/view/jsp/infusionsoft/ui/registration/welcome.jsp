<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<style type="text/css">

    #welcome {
        width: 390px;
        margin: 100px auto 0 auto;
        text-align: center;
        font-size: 18px;
    }

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

    .controls input {
        width: 316px;
    }

</style>

<div id="welcome">
    Welcome!<br/>
    Please Create Your Infusionsoft ID
</div>

<div id="register">
    <c:url var="loginUrl" value="/login"/>

    <div id="hey">
        Already have an Infusionsoft ID? <a href="${loginUrl}">Sign in</a> to link this app to your ID.
    </div>

    <form id="registerForm" action="register" method="post" class="form-vertical">
        <div id="registerFormError" class="formerror">
            <c:if test="${error != null}">
                <spring:message code="${error}"/>
            </c:if>
        </div>

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
        </fieldset>

        <div style="text-align: right; margin-top: 15px">
            <input type="submit" value="Create ID" class="btn btn-primary"/>
        </div>
    </form>

    <form id="loginForm" action="../login" method="post">
        <input type="hidden" id="loginUsername" name="username" value=""/>
        <input type="hidden" id="loginPassword" name="password" value=""/>
        <input type="hidden" name="lt" value="${loginTicket}"/>
        <%-- <input type="hidden" name="execution" value="${flowExecutionKey}" /> --%>
        <input type="hidden" name="execution" value="e1s1"/>

        <input type="hidden" name="_eventId" value="submit"/>
    </form>
</div>

<div id="instructions">
    You will use this information to sign into your Infusionsoft app.
</div>
