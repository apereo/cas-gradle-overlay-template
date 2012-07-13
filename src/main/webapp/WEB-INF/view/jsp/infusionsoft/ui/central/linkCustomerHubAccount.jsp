<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="central"/>

<c:url var="centralUrl" value="/central/home"/>

<script type="text/javascript">

</script>

<div id="main">
    <h2 class="apps">
        Link a CustomerHub App
    </h2>

    <form id="associateForm" action="associate" method="post">
        <input name="appType" type="hidden" value="customerhub"/>
        <input name="user" type="hidden" value="${user.getId()}"/>

        <p>
            Enter the username and password you were using before you created your Infusionsoft ID to link your app.
        </p>

        <table class="form" cellpadding="0" cellspacing="0">
            <tr>
                <th style="width: 100px">App URL</th>
                <td>
                    <input name="appName" type="text" value="${fn:escapeXml(appName)}"/>.customerhub.net
                </td>
            </tr>
            <tr>
                <th>Username</th>
                <td>
                    <input name="appUsername" type="text" value="${fn:escapeXml(appUsername)}" style="width: 300px"/>
                </td>
            </tr>
            <tr>
                <th>Password</th>
                <td>
                    <input name="appPassword" type="password" value="" style="width: 300px"/>
                </td>
            </tr>

        </table>

        <div class="buttonbar">
            <input type="submit" value="Link App" class="btn-primary"/>
            <a href="${centralUrl}" class="btn">Cancel</a>
        </div>
    </form>
</div>
