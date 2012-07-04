<%@ page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script type="text/javascript">

    // Old one... this is from when it was Ajax
    function registerAndLogin() {
        $.ajax({
            type:"POST",
            url:"register",
            data:$("#registerForm").serialize(),
            dataType:"json",
            success:function (response) {
                if (response.status == "OK") {
                }
            }
        });

        return false;
    }

</script>

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

    #register table.form input {
        width: 185px;
    }

    #instructions {
        font-size: 11px;
        font-style: italic;
        color: #ccc;
        text-align: center;
        margin: 15px auto;
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

    <form id="registerForm" action="register" method="post">
        <div id="registerFormError" class="formerror">
            <c:if test="${error != null}">
                <spring:message code="${error}"/>
            </c:if>
        </div>

        <table class="form" cellpadding="0" cellspacing="0">
            <tr>
                <th style="width: 200px">First Name</th>
                <td>
                    <input id="firstName" name="firstName" value="${fn:escapeXml(user != null ? user.firstName : '')}"
                           type="text"/>
                </td>
            </tr>
            <tr>
                <th>Last Name</th>
                <td>
                    <input id="lastName" name="lastName" value="${fn:escapeXml(user != null ? user.lastName : '')}"
                           type="text"/>
                </td>
            </tr>
            <tr>
                <th>Email Address</th>
                <td>
                    <input id="username" name="username" value="${fn:escapeXml(user != null ? user.username : '')}" type="text"/>
                </td>
            </tr>
            <tr>
                <th>Password</th>
                <td>
                    <input id="password1" name="password1" value="" type="password"/>
                </td>
            </tr>
            <tr>
                <th>Confirm Password</th>
                <td>
                    <input id="password2" name="password2" value="" type="password"/>
                </td>
            </tr>
        </table>

        <div style="text-align: right; margin-top: 15px">
            <input type="submit" value="Create ID" class="primary"/>
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
