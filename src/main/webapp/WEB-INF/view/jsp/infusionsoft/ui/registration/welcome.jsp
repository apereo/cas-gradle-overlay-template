<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script type="text/javascript">

    // Old one... this is from when it was Ajax
    function registerAndLogin() {
        $.ajax({
            type: "POST",
            url: "register",
            data: $("#registerForm").serialize(),
            dataType: "json",
            success: function(response) {
                if (response.status == "OK") {
                }
            }
        });

        return false;
    }

</script>

<div id="main">
    <h2>Create your account</h2>

    <p>
        Create your single sign-on account with the username/email you'd like to use for all Infusionsoft services.
    </p>
    <p>
        It's okay if this doesn't match your current username, since you can associate existing services to your SSO
        account after it's created.
    </p>

    <form id="registerForm" action="register" method="post">
        <div id="registerFormError" class="formerror">
            <c:if test="${error != null}">
                 <spring:message code="${error}"/>
            </c:if>
        </div>

        <p>
            <div class="label">Email</div>
            <input id="username" name="username" value="${fn:escapeXml(user != null ? user.username : '')}" type="text"/>

            <div class="label">Password</div>
            <input id="password1" name="password1" value="" type="password"/>

            <div class="label">Password (again)</div>
            <input id="password2" name="password2" value="" type="password"/>
        </p>

        <input type="submit" value="Create my account"/>
    </form>
    
    <form id="loginForm" action="../login" method="post">
        <input type="hidden" id="loginUsername" name="username" value=""/>
        <input type="hidden" id="loginPassword" name="password" value=""/>
        <input type="hidden" name="lt" value="${loginTicket}" />
        <%-- <input type="hidden" name="execution" value="${flowExecutionKey}" /> --%>
        <input type="hidden" name="execution" value="e1s1" />

        <input type="hidden" name="_eventId" value="submit" />
    </form>
</div>
