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

<style type="text/css">

    .form-horizontal .control-label {
        width: 120px;
    }

    .form-horizontal .controls {
        margin-left: 135px;
    }

</style>

<div id="main">
    <h2 class="apps">
        Link an Infusionsoft App
    </h2>

    <p>
        Enter the username and password you were using before you created your Infusionsoft ID to link  your app.
    </p>

    <form id="associateForm" action="associate" method="post" class="form-horizontal">
        <input name="appType" type="hidden" value="crm"/>
        <input name="user" type="hidden" value="${user.id}"/>

        <fieldset>
            <div class="control-group">
                <label for="appName" class="control-label">App URL</label>
                <div class="controls">
                    <input id="appName" name="appName" type="text" value="${fn:escapeXml(appName)}"/>.infusionsoft.com
                </div>
            </div>
            <div class="control-group">
                <label for="appUsername" class="control-label">Username</label>
                <div class="controls">
                    <input id="appUsername" name="appUsername" type="text" value="${fn:escapeXml(appUsername)}" style="width: 300px"/>
                </div>
            </div>
            <div class="control-group">
                <label for="appPassword" class="control-label">Password</label>
                <div class="controls">
                    <input id="appPassword" name="appPassword" type="password" value="" style="width: 300px"/>
                </div>
            </div>
        </fieldset>

        <div class="buttonbar">
            <input type="submit" value="Link App" class="btn btn-primary"/>
            <a href="${centralUrl}" class="btn">Cancel</a>
        </div>
    </form>
</div>
