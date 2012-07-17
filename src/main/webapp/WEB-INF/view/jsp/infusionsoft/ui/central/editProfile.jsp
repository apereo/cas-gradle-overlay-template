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
        Edit Your Infusionsoft ID
    </h2>

    <p>
        Edit the information that you use to sign into all of your apps.
    </p>

    <form id="editProfileForm" action="updateProfile" method="post" class="form-horizontal">
        <input name="id" value="${user.id}" type="hidden"/>

        <c:if test="${error != null}">
            <div class="alert">
                <spring:message code="${error}"/>
            </div>
        </c:if>

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
                <label class="control-label" for="username">Email Address</label>
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

        <div class="buttonbar">
            <input type="submit" value="Save" class="btn btn-primary"/>
            <a class="btn" href="${homeLink}">Cancel</a>
        </div>
    </form>
</div>
