<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="central"/>

<c:url var="centralUrl" value="/app/central/home"/>

<script type="text/javascript">

</script>

<h2 class="apps">
    Connect a Community Profile
</h2>

<c:if test="${error != null}">
    <div class="alert alert-error" style="margin-top: 10px">
        <spring:message code="${error}"/>
    </div>
</c:if>

<form id="associateForm" action="associate" method="post">
    <input name="appType" type="hidden" value="${appType}"/>
    <input name="appName" type="hidden" value="community"/>
    <input name="user" type="hidden" value="${user.id}"/>

    <p>
        To connect an account to your Infusionsoft ID, enter the username and password you were using before you
        created your Infusionsoft ID.
    </p>

    <table class="form" cellpadding="0" cellspacing="0">
        <tr>
            <th style="width: 100px">Username</th>
            <td>
                <input name="appUsername" type="text" value="${fn:escapeXml(appUsername)}" style="width: 300px" autocomplete="off"/>
            </td>
        </tr>
        <tr>
            <th>Password</th>
            <td>
                <input name="appPassword" type="password" value="" style="width: 300px" autocomplete="off"/>
            </td>
        </tr>

    </table>

    <div class="buttonbar">
        <input type="submit" value="Connect Account" class="btn btn-primary"/>
        <a href="${centralUrl}" class="btn">Cancel</a>
    </div>
</form>