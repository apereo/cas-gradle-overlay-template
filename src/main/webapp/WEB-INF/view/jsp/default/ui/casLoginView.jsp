<%@ page contentType="text/html; charset=UTF-8" %>

<jsp:directive.include file="includes/top.jsp" />

<c:if test="${not pageContext.request.secure}">
    <div id="msg" class="errors">
        <h2>Non-secure Connection</h2>
        <p>You are currently accessing CAS over a non-secure connection.  Single Sign On WILL NOT WORK.  In order to have single sign on work, you MUST log in over HTTPS.</p>
    </div>
</c:if>

<div id="login">
    <div id="loginleft">
        <form:form method="post" id="fm1" cssClass="fm-v clearfix" commandName="${commandName}" htmlEscape="true">
            <form:errors path="*" id="msg" cssClass="errors" element="div" />
            <h2>Sign into your account</h2>

            <p>
                <div class="label">Email</div>
                <c:if test="${not empty sessionScope.openIdLocalId}">
                    <strong>${sessionScope.openIdLocalId}</strong>
                    <input type="hidden" id="username" name="username" value="${sessionScope.openIdLocalId}" />
                </c:if>
                <c:if test="${empty sessionScope.openIdLocalId}">
                    <spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
                    <form:input cssClass="required" cssErrorClass="error" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="false" htmlEscape="true" />
                </c:if>

                <div class="label">Password</div>
                <spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
                <form:password cssClass="required" cssErrorClass="error" id="password" size="25" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />

                <%--
                <div class="row check">
                    <input id="warn" name="warn" value="true" tabindex="3" accesskey="<spring:message code="screen.welcome.label.warn.accesskey" />" type="checkbox" />
                    <label for="warn"><spring:message code="screen.welcome.label.warn" /></label>
                </div>
                --%>
            </p>

            <div class="row btn-row">
                <input type="hidden" name="lt" value="${loginTicket}" />
                <input type="hidden" name="execution" value="${flowExecutionKey}" />
                <input type="hidden" name="_eventId" value="submit" />

                <input class="btn-submit" name="submit" accesskey="l" value="Sign in" tabindex="4" type="submit" />
            </div>
        </form:form>
    </div>
    <div id="loginright">
        <h2>Not yet using SSO?</h2>
        <p>
            Set up your SSO account today! It's quick and easy, and you'll be able to access all your Infusionsoft
            apps and services with a single username and password.
        </p>
        <a href="registration/welcome">Create my account</a>
    </div>
    <div style="clear: both"></div>
</div>

<%--
<div id="sidebar">
    <p class="fl-panel fl-note fl-bevel-white fl-font-size-80"><spring:message code="screen.welcome.security" /></p>
</div>
--%>

<jsp:directive.include file="includes/bottom.jsp" />
