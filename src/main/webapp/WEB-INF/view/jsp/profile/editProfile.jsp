<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<meta name="decorator" content="central"/>

<c:url var="centralUrl" value="/app/central/home"/>


<style type="text/css">

    .form-horizontal .control-label {
        width: 120px;
    }

    .form-horizontal .controls {
        margin-left: 135px;
    }

</style>

<h2 class="apps">
    Edit Your Profile
</h2>

<p>
    Edit the information that you use to sign into all of your accounts.
</p>

<form id="editProfileForm" action="updateProfile" method="post" class="form-horizontal">
    <fieldset>
        <div class="control-group">
            <label class="control-label">Infusionsoft ID</label>

            <div class="controls">
                <span class="uneditable-input">${fn:escapeXml(user != null ? user.username : '')}</span>
                <span class="help-inline"><a href="/app/profile/changePassword">Change Password</a></span>
            </div>
        </div>
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
    </fieldset>

    <div class="buttonbar">
        <input type="submit" value="Save" class="btn btn-primary"/>
        <a class="btn" href="${centralUrl}">Cancel</a>
    </div>
</form>